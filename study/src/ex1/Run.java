package ex1;

import ex1.layer.ApplicationLayer;
import ex1.layer.JobHandlerLayer;
import ex1.layer.TransformationHandlerLayer;
import net.sf.appia.core.Appia;
import net.sf.appia.core.AppiaDuplicatedSessionsException;
import net.sf.appia.core.AppiaInvalidQoSException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Layer;
import net.sf.appia.core.QoS;

/**
 * This is the MAIN class to run the Job protocols.
 * 
 * @author carlos.mestre
 */
public class Run {

	public static void main(String[] args) {
		
		/* Create layers and put them on a array */
		Layer[] qos = { new JobHandlerLayer(), new TransformationHandlerLayer(), new ApplicationLayer() };

		/* Create a QoS */
		QoS myQoS = null;
		
		try {
			myQoS = new QoS("Job_stack", qos);
		} catch (AppiaInvalidQoSException ex) {
			ex.printStackTrace();
			System.exit(1);
		}

		/* Create a channel. Uses default event scheduler. */
		Channel channel = myQoS.createUnboundChannel("Job_Channel");
		try {
			channel.start();
		} catch (AppiaDuplicatedSessionsException ex) {
			System.err.println("Error in start");
			System.exit(1);
		}

		/* All set. Appia main class will handle the rest. */
		System.out.println("Starting Appia...");
		Appia.run();
	}
}
