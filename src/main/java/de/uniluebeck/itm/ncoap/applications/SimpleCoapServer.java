/**
 */
package de.uniluebeck.itm.ncoap.applications;

import java.net.InetSocketAddress;
import java.net.URI;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.dennis_boldt.RXTX;
import de.uniluebeck.itm.ncoap.application.client.CoapClientApplication;
import de.uniluebeck.itm.ncoap.application.server.CoapServerApplication;
import de.uniluebeck.itm.ncoap.message.CoapRequest;
import de.uniluebeck.itm.ncoap.message.MessageCode;
import de.uniluebeck.itm.ncoap.message.MessageType;

/**
 * Created by olli on 30.03.14.
 */
public class SimpleCoapServer extends CoapServerApplication  {

	@Option(name="--host",usage="Set the SSP IP")
	private static String host = "141.83.151.196";

	@Option(name="--port",usage="Set USB port")
    public String port = "/dev/ttyACM0";	
	
	@Option(name="--rxtxlib",usage="Set RXTX lib")
    public String rxtxlib = "/usr/lib/jni";

	private RXTX rxtx;	

	public SimpleCoapServer(String[] args) throws Exception {

		// lifetime = sleep from arduino code
        SoilSensorService soilService = new SoilSensorService("/soil", 500, this.getExecutor());
		
		rxtx = new RXTX();
		
		CmdLineParser parser = new CmdLineParser(this);
        try {
        	parser.parseArgument(args);
        	rxtx.start(this.port, this.rxtxlib, soilService);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    	
    	
        LoggingConfiguration.configureDefaultLogging();

        this.registerService(soilService);		
        
        
        // Initial Call to the SSP to initialize the obervation
		URI webserviceURI = new URI("coap://" + host + ":" + CoapServerApplication.DEFAULT_COAP_SERVER_PORT + "/registry");
		CoapRequest coapRequest = new CoapRequest(MessageType.Name.CON, MessageCode.Name.POST, webserviceURI, false);
		SimpleCallback responseProcessor = new SimpleCallback();
		InetSocketAddress recipient = new InetSocketAddress(host, CoapServerApplication.DEFAULT_COAP_SERVER_PORT);
		CoapClientApplication c = new CoapClientApplication();
		c.sendCoapRequest(coapRequest, responseProcessor, recipient);
		
	}
	
    public static void main(String[] args) throws Exception {
    	new SimpleCoapServer(args);
    }    
    
}
