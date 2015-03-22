package list1.event;

import net.sf.appia.core.Event;

/**
 * Job Submit confirmation event.
 * 
 * @author carlos.mestre
 */
public class ConfirmEvent extends Event {

	int rqid;

	public void setId(int rid) {
		rqid = rid;
	}

	public int getId() {
		return rqid;
	}
}
