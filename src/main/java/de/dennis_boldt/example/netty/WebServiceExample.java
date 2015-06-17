package de.dennis_boldt.example.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.dennis_boldt.RXTX;
import de.dennis_boldt.example.StringWriter;

public class WebServiceExample extends WebSocketServer implements Observer  {

	@Option(name="--ports",usage="Set USB ports")
    public String ports = null;

	@Option(name="--rxtxlib",usage="Set RXTX lib")
    public String rxtxlib = "/usr/lib/jni";
	
	private RXTX rxtx;
	
	public WebServiceExample(String[] args, int port) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		super( new InetSocketAddress( port ) );
		
		this.start();
		System.out.println( "WebSocket Server started on port: " + this.getPort() );

		rxtx = new RXTX();
		
		CmdLineParser parser = new CmdLineParser(this);
        try {
        	parser.parseArgument(args);
        	rxtx.start(this.ports, this.rxtxlib, this);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
        
		(new Thread(new StringWriter(rxtx))).start();
		
	}
	
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		new WebServiceExample(args, 9999);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		
		if(arg instanceof byte[]) {
			byte[] bytes = (byte[]) arg;
			String s = new String(bytes);
			
			// SEND output to WS?
			
			System.out.print(s);
		}
		
	}
	
	@Override
	public void onMessage(WebSocket arg0, String arg1) {
		
		byte[] bytes = arg1.getBytes();
    	try {
			this.rxtx.write(bytes);
			this.rxtx.write(10);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
	}

	@Override
	public void onError(WebSocket arg0, Exception arg1) {
	}

	@Override
	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
	}
	
}
