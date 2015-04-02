package ex1.session;

import ex1.event.ConfirmEvent;
import ex1.event.ErrorEvent;
import ex1.event.SubmitEvent;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;

/**
 * Session implementing the TransformationHandler protocol. <br>
 * Changes the letters to uppercase
 * 
 * @author carlos.mestre
 */
public class TransformationHandlerSession extends Session {
	
	/** Creates a new instance of TransformationHandlerSession */
	public TransformationHandlerSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		if (event instanceof ChannelInit) {
			handleChannelInit((ChannelInit) event);
		} else if (event instanceof SubmitEvent) {
			handleSubmit((SubmitEvent) event);
		} 
	}

	private void handleChannelInit(ChannelInit init) {
		try {
			init.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	private void handleSubmit(SubmitEvent submit) {
		
		try {
			submit.setString(submit.getString().toUpperCase());
			submit.go();
			
			ConfirmEvent confirm = new ConfirmEvent();
			confirm.setId(submit.getId());
			confirm.setChannel(submit.getChannel());
			confirm.setDir(Direction.UP);
			confirm.setSourceSession(this);
			try {
				confirm.init();
				confirm.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			ErrorEvent error = new ErrorEvent();
			error.setChannel(submit.getChannel());
			error.setDir(Direction.UP);
			error.setSourceSession(this);
			error.setId(submit.getId());
			try {
				error.init();
				error.go();
			} catch (AppiaEventException ex) {
				e.printStackTrace();
			}
		}
				
	}

}
