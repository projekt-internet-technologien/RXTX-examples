package de.dennis_boldt.example;

import java.util.Observable;
import java.util.Observer;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.dennis_boldt.RXTX;

public class StringExample implements Observer {

	@Option(name="--ports",usage="Set USB ports")
    public String ports = null;

	@Option(name="--rxtxlib",usage="Set RXTX lib")
    public String rxtxlib = "/usr/lib/jni";
	
	public StringExample(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		RXTX rxtx = new RXTX();
		
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
		new StringExample(args);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		
		if(arg instanceof byte[]) {
			byte[] bytes = (byte[]) arg;
			String s = new String(bytes);
			System.out.print(s);
		}
		
	}
	
}


