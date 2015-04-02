package ex6;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import ex6.layer.ReceiveLayer;
import ex6.session.ReceiveSession;
import net.sf.appia.core.Appia;
import net.sf.appia.core.AppiaCursorException;
import net.sf.appia.core.AppiaDuplicatedSessionsException;
import net.sf.appia.core.AppiaInvalidQoSException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.ChannelCursor;
import net.sf.appia.core.Layer;
import net.sf.appia.core.QoS;
import net.sf.appia.protocols.udpsimple.UdpSimpleLayer;


public class RunReceiver {

	private static String ADDR_SENDER = "localhost";
	private static String ADDR_RECEIVER = "localhost";
	private static int PORT_SENDER = 8080;
	private static int PORT_RECEIVER = 9090;

	public static void main(String[] args) {
		/* Creates a new PBLayer and initializes it */

		/* Create layers and put them on a array */
		Layer[] qos = { new UdpSimpleLayer(), new ReceiveLayer() };

		/* Create a QoS */
		QoS myQoS = null;
		try {
			myQoS = new QoS("UDP Simple Example", qos);
		} catch (AppiaInvalidQoSException ex) {
			System.err.println("Invalid QoS");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		/* Create a channel. Uses default event scheduler. */
		Channel channel = myQoS.createUnboundChannel("UDP Simple Channel");

		ReceiveSession sas = (ReceiveSession) qos[qos.length - 1]
				.createSession();
		sas.init(buildProcessSet());
		ChannelCursor cc = channel.getCursor();

		try {
			cc.top();
			cc.setSession(sas);
		} catch (AppiaCursorException ex) {
			System.err.println("Unexpected exception in main. Type code:"
					+ ex.type);
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

	private static SocketAddress[] buildProcessSet() {
		SocketAddress[] addresses = new SocketAddress[2];

		InetAddress addrSender = null;
		InetAddress addrReceiver = null;
		try {
			addrSender = InetAddress.getByName(ADDR_SENDER);
			addrReceiver = InetAddress.getByName(ADDR_RECEIVER);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		addresses[0] = new InetSocketAddress(addrSender, PORT_SENDER);
		addresses[1] = new InetSocketAddress(addrReceiver, PORT_RECEIVER);

		return addresses;
	}

}
