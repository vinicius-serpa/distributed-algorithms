package ex1.layer;

import ex1.event.ConfirmEvent;
import ex1.event.SubmitEvent;
import ex1.session.JobHandlerSession;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;

/**
 * Layer of the JobHandler protocol.
 * 
 * @author carlos.mestre
 */
public class JobHandlerLayer extends Layer {

	public JobHandlerLayer() {

		/* events that the protocol will create */
		evProvide = new Class[1];
		evProvide[0] = ConfirmEvent.class;

		/*
		 * events that the protocol requires to work This is a subset of the
		 * accepted events.
		 */
		evRequire = new Class[0];

		/* events that the protocol will accept */
		evAccept = new Class[2];
		evAccept[0] = SubmitEvent.class;
		evAccept[1] = ChannelInit.class;
	}

	public Session createSession() {
		return new JobHandlerSession(this);
	}
}
