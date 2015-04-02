package ex3.layer;

import ex3.event.ConfirmEvent;
import ex3.event.ErrorEvent;
import ex3.event.SubmitEvent;
import ex3.session.ApplicationSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;

/**
 * Layer of the Application.
 * 
 * @author carlos.mestre
 */
public class ApplicationLayer extends Layer {

	/** Creates a new instance of PrintApplicationLayer */
	public ApplicationLayer() {
		
		/* events that the protocol will create */
		evProvide = new Class[1];
		evProvide[0] = SubmitEvent.class;

		/*
		 * events that the protocol requires to work This is a subset of the
		 * accepted events.
		 */
		evRequire = new Class[0];

		/* events that the protocol will accept */
		evAccept = new Class[3];
		evAccept[0] = ConfirmEvent.class;
		evAccept[1] = ErrorEvent.class;		
		evAccept[2] = ChannelInit.class;
	}

	public Session createSession() {
		return new ApplicationSession(this);
	}
}
