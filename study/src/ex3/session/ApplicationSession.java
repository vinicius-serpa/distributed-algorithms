package ex3.session;

import net.sf.appia.core.*;
import net.sf.appia.core.events.channel.ChannelInit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ex3.event.ConfirmEvent;
import ex3.event.ErrorEvent;
import ex3.event.SubmitEvent;

/**
 * Session implementing the Application protocol. <br>
 * Reads strings, requests their printing and displays confirmations.
 * 
 * @author carlos.mestre
 */
public class ApplicationSession extends Session {

	public ApplicationSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		System.out.println();

		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof ConfirmEvent)
			handleConfirm((ConfirmEvent) event);
		else if (event instanceof ErrorEvent)
			handleError((ErrorEvent) event);		
	}

	private PrintReader reader = null;

	private void handleChannelInit(ChannelInit init) {
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}

		if (reader == null)
			reader = new PrintReader(init.getChannel());
	}

	private void handleConfirm(ConfirmEvent confirm) {
		
		System.out.println("[Application: received confirmation of request " + confirm.getId() + "]");

		try {
			confirm.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
	}

	private void handleError(ErrorEvent error) {
		System.out.println("[Application: received ERROR]");

		try {
			error.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
	}	

	private class PrintReader extends Thread {

		public boolean ready = false;
		public Channel channel;
		private BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		private int rid = 0;

		public PrintReader(Channel channel) {
			ready = true;
			if (this.channel == null)
				this.channel = channel;
			this.start();
		}

		public void run() {
			boolean running = true;

			while (running) {
				++rid;
				System.out.println();
				System.out.print("[Application](" + rid + ")> ");
				try {
					String s = stdin.readLine();

					SubmitEvent submit = new SubmitEvent();
					submit.setId(rid);
					submit.setString(s);
					submit.asyncGo(channel, Direction.DOWN);
				} catch (AppiaEventException ex) {
					ex.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(1500);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				synchronized (this) {
					if (!ready)
						running = false;
				}
			}
		}
	}
}
