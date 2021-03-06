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

	@Option(name="--baud",usage="Set baud rate")
    public int baud = 9600;
	
	public StringExample(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		RXTX rxtx;
		CmdLineParser parser = new CmdLineParser(this);
        try {
        	parser.parseArgument(args);
        	rxtx = new RXTX(this.baud);
        	rxtx.start(this.ports, this.rxtxlib, this);
        	(new Thread(new StringWriter(rxtx))).start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
	}
	
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		new StringExample(args);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(arg instanceof byte[]) {
			byte[] bytes = (byte[]) arg;
			System.out.print(new String(bytes));
		}
		
	}
	
}


