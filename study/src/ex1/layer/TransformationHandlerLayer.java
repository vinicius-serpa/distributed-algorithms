package ex1.layer;

import ex1.event.ConfirmEvent;
import ex1.event.ErrorEvent;
import ex1.event.SubmitEvent;
import ex1.session.TransformationHandlerSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;

/**
 * Layer of the TransformationHandler protocol.
 * 
 * @author carlos.mestre
 */
public class TransformationHandlerLayer extends Layer {

	public TransformationHandlerLayer() {
		
		/* events that the protocol will create */
		evProvide = new Class[2];
		evProvide[0] = ConfirmEvent.class;
		evProvide[1] = ErrorEvent.class;

		/*
		 * events that the protocol requires to work. This is a subset of the
		 * accepted events.
		 */
		evRequire = new Class[1];
		evRequire[0] = ConfirmEvent.class;

		/* events that the protocol will accept */
		evAccept = new Class[3];
		evAccept[0] = SubmitEvent.class;
		evAccept[1] = ConfirmEvent.class;
		evAccept[2] = ChannelInit.class;
	}

	public Session createSession() {
		return new TransformationHandlerSession(this);
	}
}
