package ex3.event;

import net.sf.appia.core.Event;

/**
 * Job Submit error event.
 * 
 * @author carlos.mestre
 */
public class ErrorEvent extends Event {
	
	int rqid;

	public void setId(int rid) {
		rqid = rid;
	}

	public int getId() {
		return rqid;
	}
}
