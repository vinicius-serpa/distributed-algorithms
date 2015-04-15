package appSender;


import org.apache.log4j.BasicConfigurator;

import layer.ApplicationLayer;
import layer.SendReceiveApplicationLayer;
import model.CustomProcess;
import model.ProcessList;
import net.sf.appia.core.Appia;
import net.sf.appia.core.AppiaCursorException;
import net.sf.appia.core.AppiaDuplicatedSessionsException;
import net.sf.appia.core.AppiaInvalidQoSException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.ChannelCursor;
import net.sf.appia.core.Layer;
import net.sf.appia.core.QoS;
import net.sf.appia.protocols.udpsimple.UdpSimpleLayer;
import session.SendReceiveApplicationSession;


public class ExampleSender {

	private static int PROC_ID_SENDER = 0;
	private static int PROC_ID_RECEIVER = 1;
	private static String ADDR_SENDER = "192.168.1.2";
	private static String ADDR_RECEIVER = "192.168.1.7";
	private static int PORT_SENDER = 8080;
	private static int PORT_RECEIVER = 9090;
	
	private static String BUILD_PROCESS_SET = "1";
	
	public static void main(String[] args) {
	
		// BasicConfigurator.configure();
		
	    // Utiliza UDP porque ele é FairLoss/Perda Justa
	    Layer[] qos = {new UdpSimpleLayer(), new SendReceiveApplicationLayer(), new ApplicationLayer()};

	    /* Create a QoS */
	    QoS myQoS = null;
	    try {
	      myQoS = new QoS("UDP Simple Example", qos);
	    } catch (AppiaInvalidQoSException ex) {
	      System.err.println("Invalid QoS");
	      System.err.println(ex.getMessage());
	      System.exit(1);
	    }
	    
	    Channel channel = myQoS
	        .createUnboundChannel("UDP Simple Channel");
	    
	    SendReceiveApplicationSession sas = (SendReceiveApplicationSession) qos[qos.length - 2]
	        .createSession();
	    sas.init(buildProcessSet(BUILD_PROCESS_SET));
	    ChannelCursor cc = channel.getCursor();
	    
	    try {
	      cc.top();
	      cc.down();
	      cc.setSession(sas);
	    } catch (AppiaCursorException ex) {
	      System.err.println("Unexpected exception in main. Type code:" + ex.type);
	      ex.printStackTrace();
	      System.exit(1);
	    }
	    
		try {

			channel.start();
		} catch (AppiaDuplicatedSessionsException ex) {
			System.err.println("Error in start");
			System.exit(1);
		}

		System.out.println("Starting Appia...");
		Appia.run();
	}

	private static ProcessList buildProcessSet(String arg) {
		int current = Integer.parseInt(arg);
		ProcessList processes = new ProcessList();

		processes.add(new CustomProcess(PROC_ID_RECEIVER, PORT_RECEIVER, ADDR_RECEIVER));
		processes.add(new CustomProcess(PROC_ID_SENDER, PORT_SENDER, ADDR_SENDER)); 
		
		processes.getProcessById(current).setSelf(true);
		System.out.println("current: " + current);
		return processes;
	}
}
