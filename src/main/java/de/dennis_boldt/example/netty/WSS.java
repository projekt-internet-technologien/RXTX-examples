package de.dennis_boldt.example.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import de.dennis_boldt.RXTX;

public class WSS extends WebSocketServer {

	RXTX rxtx = null;
	
	public WSS(int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		
		this.start();
		
		rxtx = new RXTX();
		System.out.println( "WebSocket Server started on port: " + this.getPort() );
		
	}

	@Override
	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(WebSocket arg0, Exception arg1) {
		// TODO Auto-generated method stub
		
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
	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) throws UnknownHostException {
		new WSS(9999);
	}

}
