package ex6.layer;

import ex6.event.ReceiverConfirmEvent;
import ex6.event.SenderRequestEvent;
import ex6.session.SendSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;

public class SendLayer extends Layer {
	
	public SendLayer() {
		
		/* events that the protocol will create */
		evProvide = new Class[1];
		evProvide[0] = SenderRequestEvent.class;

		/*
		 * events that the protocol requires to work This is a subset of the
		 * accepted events.
		 */
		evRequire = new Class[0];

		/* events that the protocol will accept */
		evAccept = new Class[2];
		evAccept[0] = ReceiverConfirmEvent.class;
		evAccept[1] = ChannelInit.class;
	}

	public Session createSession() {
		return new SendSession(this);
	}

}
