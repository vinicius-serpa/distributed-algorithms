package ex6and7.session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

import ex6and7.event.ReceiverConfirmEvent;
import ex6and7.event.SenderRequestEvent;
import ex6and7.to.MyMessage;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.common.RegisterSocketEvent;

public class SendSession extends Session {
	
	private MessageReader reader = null;
	private SocketAddress[] addresses;
	private boolean notConfirm = true;
	
	public SendSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		
		System.out.println();

		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof ReceiverConfirmEvent)
			handleReceiverConfirm((ReceiverConfirmEvent) event);
	}	

	private void handleChannelInit(ChannelInit init) {
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}

		if (reader == null)
			reader = new MessageReader(init.getChannel());
		
		
		try {
		      // sends this event to open a socket in the layer that is used has perfect
		      // point to point
		      // channels or unreliable point to point channels.
		      RegisterSocketEvent rse = new RegisterSocketEvent(init.getChannel(), Direction.DOWN, this);
		      rse.port = ((InetSocketAddress) addresses[0]).getPort();
		      rse.localHost = ((InetSocketAddress)addresses[0]).getAddress();
		      rse.go();
		    } catch (AppiaEventException e1) {
		      e1.printStackTrace();
		    }
		    System.out.println("Channel is open.");
	}

	private void handleReceiverConfirm(ReceiverConfirmEvent conf) {
		System.out.println("[Sender: received confirmation of request "
						+ ((MyMessage)conf.getMessage().popObject()).getId() + "]");
		
		notConfirm = false;
		
		try {
			conf.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
	}

	private class MessageReader extends Thread {

		public boolean ready = false;
		public Channel channel;
		private BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));
		private int rid = 0;

		public MessageReader(Channel channel) {
			ready = true;
			if (this.channel == null)
				this.channel = channel;
			this.start();
		}

		public void run() {
			boolean running = true;			

			while (running) {
				rid++;
				System.out.println();
				System.out.print("[Sender](" + rid + ")> ");
				try {
					
					notConfirm = true;
					String s = stdin.readLine();
					
					SenderRequestEvent request = new SenderRequestEvent();
					request.setId(rid);
					
					Message m = new Message();
					m.pushObject(new MyMessage(rid, s));
					request.setMessage(m);
					request.setDest(addresses[1]);
					request.setSendSource(addresses[0]);
					
					while (notConfirm) {
																							
						System.out.println("sending... " );
						request.asyncGo(channel, Direction.DOWN);
						
						Thread.sleep(1500);
					}
										
				} catch (AppiaEventException ex) {
					ex.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(1000);	// 1 sec
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

	public void init(SocketAddress[] addresses) {
		this.addresses = addresses;
	}
}
