package session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import model.CustomProcess;
import model.ProcessList;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import event.HeartbeatEvent;
import event.TrustEvent;

/**
 * Session implementing the Print Application protocol. <br>
 * Reads strings, requests their printing and displays confirmations.
 * 
 * @author alexp
 */
public class SendReceiveApplicationSession extends Session {

	public SendReceiveApplicationSession(Layer layer) {
		super(layer);
	}

	public void handle(Event event) {
		
		System.out.println("SendReceiveApplicationSession recebeu um evento: " + event.toString());
		
		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
		else if (event instanceof HeartbeatEvent)
			handleDeliver((HeartbeatEvent) event);
	}

	private Timer timer = null;
	private ProcessList processes;
	private ProcessList candidates;
	private CustomProcess leader = null;
	private Channel channel = null;
	
	private long delta;

	private void handleChannelInit(ChannelInit init) {
		
		System.out.println("SendReceiveApplicationSession inicializando Channel");
		
		candidates = new ProcessList();
		leader = processes.getSelf();
		channel = init.getChannel();
		delta = 2000;		

		if (timer == null) {
			timer = new Timer();
			timer.schedule(new Timeout(), delta);
		}
		
		try {
			RegisterSocketEvent rse = new RegisterSocketEvent(
				init.getChannel(), Direction.DOWN, this);

			InetSocketAddress address = processes.getSelf()
				.getCompleteAddress();
				
			rse.port = address.getPort();
			rse.localHost = address.getAddress();

			rse.go();
		} catch (AppiaEventException e1) {
			e1.printStackTrace();
		}

		
		TrustEvent event = new TrustEvent();
		event.setLeader(leader);
		System.out.println(leader.getId());
		try {
			event.setChannel(channel);
			event.setDir(Direction.UP);
			event.setSourceSession(this);
			
			event.init();
			event.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
		
		File f = new File("epoch"+processes.getSelf().getId()+".txt");
		if( f.exists() ){
			 try {
				BufferedReader input =  new BufferedReader(new FileReader(f));
				int epoch = Integer.parseInt(input.readLine());
				processes.getSelf().setEpoch(epoch + 1);
				store();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				processes.getSelf().setEpoch(0);
				store();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		HeartbeatEvent heartbeat = null;
		Message message = null;
		
		for( CustomProcess process : processes ){
			heartbeat = new HeartbeatEvent();
			message = new Message();
			
			message.pushObject(processes.getSelf());
			
			heartbeat.setMessage(message);
			heartbeat.setDest(process);
			heartbeat.setSendSource(processes.getSelf());
			
			try {
				heartbeat.setChannel(channel);
				heartbeat.setDir(Direction.DOWN);
				heartbeat.setSourceSession(this);
				
				heartbeat.init();
				heartbeat.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		}

		
		try {
			init.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
		}
				
		System.out.println("Channel is open.");
	}

	private void handleDeliver(HeartbeatEvent conf) {
		
		if( conf.getDir() == Direction.DOWN ){
			
			System.out.println("SendReceiveApplicationSession DELIVER: DOWN");
			
			try {
				conf.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		} else {
			
			System.out.println("SendReceiveApplicationSession DELIVER: UP");
			
			CustomProcess source = (CustomProcess) conf.getMessage().popObject();
			candidates.update(source);
			
			System.out.println("SendReceiveApplicationSession DELIVER: " + source.getId());
		}
	}
	
	private void store() throws IOException{
		
		System.out.println("SendReceiveApplicationSession Armazenando Epoch");
		
		FileWriter outFile = new FileWriter("epoch"+processes.getSelf().getId()+".txt", false);
		PrintWriter out = new PrintWriter(outFile);

		out.print(processes.getSelf().getEpoch());
		out.close();
	}

	public void init(ProcessList processes) {
		System.out.println("Passei aqui: " + processes.size());
		this.processes = processes;
	}
	
	private class Timeout extends TimerTask {

		@Override
		public void run() {
			System.out.println("RUN DO TIMEOUT");
			if(!candidates.isEmpty()){
				System.out.println("PEGANDO CANDIDATOS");
				CustomProcess newleader = candidates.selectLeader();
				if( newleader.getId() != leader.getId() ){
					delta += delta;
					leader = newleader;
					
					TrustEvent event = new TrustEvent();
					event.setLeader(leader);
					System.out.println(leader.getId());
					try {
						event.asyncGo(channel, Direction.UP);
					} catch (AppiaEventException e) {
						e.printStackTrace();
					}
				}
			}
			HeartbeatEvent heartbeat = null;
			Message message = null;
			
			for( CustomProcess process : processes ){
					System.out.println("Eviando HB: " + process.getId());
					heartbeat = new HeartbeatEvent();
					message = new Message();
					
					message.pushObject(processes.getSelf());
					
					heartbeat.setMessage(message);
					heartbeat.setDest(process);
					heartbeat.setSendSource(processes.getSelf());
					
					try {
						heartbeat.asyncGo(channel, Direction.DOWN);
					} catch (AppiaEventException e) {
						e.printStackTrace();
					}
				
			}
			
			candidates.clear();
			timer.schedule(new Timeout(), delta);
		}
		
	}
}
