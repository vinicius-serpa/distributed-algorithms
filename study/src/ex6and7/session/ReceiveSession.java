package ex6and7.session;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import ex6and7.event.ReceiverConfirmEvent;
import ex6and7.event.SenderRequestEvent;
import ex6and7.to.MyMessage;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.common.RegisterSocketEvent;

public class ReceiveSession extends Session {

	public ReceiveSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		System.out.println();

		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof SenderRequestEvent)
			handleSenderRequest((SenderRequestEvent) event);
	}

	private SocketAddress[] addresses;

	private void handleChannelInit(ChannelInit init) {
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}

		try {
			// sends this event to open a socket in the layer that is used has
			// perfect
			// point to point
			// channels or unreliable point to point channels.
			RegisterSocketEvent rse = new RegisterSocketEvent(
					init.getChannel(), Direction.DOWN, this);
			rse.port = ((InetSocketAddress) addresses[1]).getPort();
			rse.localHost = ((InetSocketAddress) addresses[1]).getAddress();
			rse.go();
		} catch (AppiaEventException e1) {
			e1.printStackTrace();
		}
		System.out.println("Channel is open.");
	}

	private void handleSenderRequest(SenderRequestEvent conf) {
		
		try {
			new Thread().sleep(3500); // 4 sec
		} catch (InterruptedException e1) {			
			e1.printStackTrace();
		}
		
		try {
			MyMessage s = (MyMessage) conf.getMessage().popObject();
			String receivedMessage = s.getString();
			System.out.println("[Receiver: received message: " + receivedMessage + "]");
			
			ReceiverConfirmEvent confirmationEvent = new ReceiverConfirmEvent();
			confirmationEvent.setChannel(conf.getChannel());
			confirmationEvent.setId(conf.getId());
			Message m = new Message();
			m.pushObject(s);
			confirmationEvent.setMessage(m);
			confirmationEvent.setDir(Direction.DOWN);
			confirmationEvent.setSourceSession(this);
			confirmationEvent.setSendSource(addresses[1]);
			confirmationEvent.setDest(addresses[0]);
			try {
				confirmationEvent.init();
				confirmationEvent.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("Discarting confirmed message");
		}
	}

	public void init(SocketAddress[] addresses) {
		this.addresses = addresses;
	}
}
