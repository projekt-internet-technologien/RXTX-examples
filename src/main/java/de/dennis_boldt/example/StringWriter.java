package de.dennis_boldt.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.dennis_boldt.RXTX;

public class StringWriter implements Runnable {

	private RXTX out;

	public StringWriter(RXTX out) {
		this.out = out;
	}

	public void run() {
		
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
		    for(String line = br.readLine(); line != null; line = br.readLine()) {
		    	byte[] bytes = line.getBytes();
		    	this.out.write(bytes);
		    	this.out.write(10); // NEW LINE
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}