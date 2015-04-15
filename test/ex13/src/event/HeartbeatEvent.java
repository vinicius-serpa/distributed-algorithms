package event;

import model.CustomProcess;
import net.sf.appia.core.events.SendableEvent;


public class HeartbeatEvent extends SendableEvent {
  static int rqid;
  static int str;
  private CustomProcess processDest;
  private CustomProcess processSource;

  public void setId(int rid) {
    rqid = rid;
  }

  public void setString(int s) {
    str = s;
  }

  public int getId() {
    return rqid;
  }

  public int getString() {  
	return str;
  }
  
  public void setDest(CustomProcess customProcess ){
		this.processDest = customProcess;
		
		dest = customProcess.getCompleteAddress();
  }
  
  public void setSendSource(CustomProcess customProcess){
		this.processSource = customProcess;
		
		source = customProcess.getCompleteAddress();

  }

public CustomProcess getProcessDest() {
	return processDest;
}

public CustomProcess getProcessSource() {
	return processSource;
}
  
}
