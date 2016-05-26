/**
 *
 */
package de.uniluebeck.itm.ncoap.applications;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;

import com.google.common.primitives.Longs;
import com.google.common.util.concurrent.SettableFuture;

import de.uniluebeck.itm.ncoap.application.server.webservice.ObservableWebservice;
import de.uniluebeck.itm.ncoap.application.server.webservice.WrappedResourceStatus;
import de.uniluebeck.itm.ncoap.application.server.webservice.linkformat.LongLinkAttribute;
import de.uniluebeck.itm.ncoap.application.server.webservice.linkformat.StringLinkAttribute;
import de.uniluebeck.itm.ncoap.communication.dispatching.client.Token;
import de.uniluebeck.itm.ncoap.message.CoapMessage;
import de.uniluebeck.itm.ncoap.message.CoapRequest;
import de.uniluebeck.itm.ncoap.message.CoapResponse;
import de.uniluebeck.itm.ncoap.message.MessageCode;
import de.uniluebeck.itm.ncoap.message.options.ContentFormat;

/**
 * @author Oliver Kleine
 */
public class SoilSensorService extends ObservableWebservice<Long> implements Observer{

    public static long DEFAULT_CONTENT_FORMAT = ContentFormat.TEXT_PLAIN_UTF8;
    private static Logger log = Logger.getLogger(SoilSensorService.class.getName());

    private static HashMap<Long, String> payloadTemplates = new HashMap<>();
    static{
        //Add template for plaintext UTF-8 payload
        payloadTemplates.put(
                ContentFormat.TEXT_PLAIN_UTF8,
                "The current sensor value is %01d"
        );

        //Add template for XML payload
        payloadTemplates.put(
                ContentFormat.APP_XML,
                "<sensorValue>%01d</sensorValue>"
        );
        
        //Add template for Turtle payload
        payloadTemplates.put(
		        ContentFormat.APP_TURTLE,
		        "@prefix itm: <http://gruppe20.pit.itm.uni-luebeck.de/>\n" +
		        "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
		        "\n" + 
		        "itm:gruppe20 itm:sensorValue \"%01d\"^^xsd:integer .\n"
		);
        
    }

    //private ScheduledFuture periodicUpdateFuture;
    private int updateInterval;

    /**
     * Creates a new instance of {@link de.uniluebeck.itm.ncoap.applications.SoilSensorService}
     * @param path the path of the URI of this service
     * @param updateInterval the interval (in millis) for resource status updates (e.g. 5000 for every 5 seconds).
     */
    public SoilSensorService(String path, int updateInterval, ScheduledExecutorService executor) {
        super(path, System.currentTimeMillis(), executor);

        //Set the update interval, i.e. the frequency of resource updates
        this.updateInterval = updateInterval;
        //schedulePeriodicResourceUpdate();

        //Sets the link attributes for supported content types ('ct')
        this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.TEXT_PLAIN_UTF8));
        this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.APP_XML));
        this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.CONTENT_TYPE, ContentFormat.APP_TURTLE));

        //Sets the link attribute for the resource type ('rt')
        String attributeValue = "The soil sensor value";
        this.setLinkAttribute(new StringLinkAttribute(StringLinkAttribute.RESOURCE_TYPE, attributeValue));

        //Sets the link attribute for max-size estimation ('sz')
        this.setLinkAttribute(new LongLinkAttribute(LongLinkAttribute.MAX_SIZE_ESTIMATE, 100L));

        //Sets the link attribute for interface description ('if')
        this.setLinkAttribute(new StringLinkAttribute(StringLinkAttribute.INTERFACE, "CoAP GET"));
    }


    @Override
    public boolean isUpdateNotificationConfirmable(InetSocketAddress remoteEndpoint, Token token) {
        return false;
    }


    @Override
    public byte[] getEtag(long contentFormat) {
        return Longs.toByteArray(getStatus() | (contentFormat << 56));
    }


    @Override
    public void updateEtag(Long resourceStatus) {
        //nothing to do here as the ETAG is constructed on demand in the getEtag(long contentFormat) method
    }

    @Override
    public void processCoapRequest(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest,
                                   InetSocketAddress remoteAddress) {
        try{
            if(coapRequest.getMessageCodeName() == MessageCode.Name.GET){
                processGet(responseFuture, coapRequest);
            }

            else {
                CoapResponse coapResponse = new CoapResponse(coapRequest.getMessageTypeName(),
                        MessageCode.Name.METHOD_NOT_ALLOWED_405);
                String message = "Service does not allow " + coapRequest.getMessageCodeName() + " requests.";
                coapResponse.setContent(message.getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
                responseFuture.set(coapResponse);
            }
        }
        catch(Exception ex){
            responseFuture.setException(ex);
        }
    }


    private void processGet(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest)
            throws Exception {

        //Retrieve the accepted content formats from the request
        Set<Long> contentFormats = coapRequest.getAcceptedContentFormats();

        //If accept option is not set in the request, use the default (TEXT_PLAIN_UTF8)
        if(contentFormats.isEmpty())
            contentFormats.add(DEFAULT_CONTENT_FORMAT);

        //Generate the payload of the response (depends on the accepted content formats, resp. the default
        WrappedResourceStatus resourceStatus = null;
        Iterator<Long> iterator = contentFormats.iterator();
        long contentFormat = DEFAULT_CONTENT_FORMAT;

        while(resourceStatus == null && iterator.hasNext()){
            contentFormat = iterator.next();
            resourceStatus = getWrappedResourceStatus(contentFormat);
        }

        //generate the CoAP response
        CoapResponse coapResponse;

        //if the payload could be generated, i.e. at least one of the accepted content formats (according to the
        //requests accept option(s)) is offered by the Webservice then set payload and content format option
        //accordingly
        if(resourceStatus != null){
            coapResponse = new CoapResponse(coapRequest.getMessageTypeName(), MessageCode.Name.CONTENT_205);
            coapResponse.setContent(resourceStatus.getContent(), contentFormat);

            coapResponse.setEtag(resourceStatus.getEtag());
            coapResponse.setMaxAge(resourceStatus.getMaxAge());

            if(coapRequest.getObserve() == 0)
                coapResponse.setObserve();
        }

        //if no payload could be generated, i.e. none of the accepted content formats (according to the
        //requests accept option(s)) is offered by the Webservice then set the code of the response to
        //400 BAD REQUEST and set a payload with a proper explanation
        else{
            coapResponse = new CoapResponse(coapRequest.getMessageTypeName(), MessageCode.Name.NOT_ACCEPTABLE_406);

            StringBuilder payload = new StringBuilder();
            payload.append("Requested content format(s) (from requests ACCEPT option) not available: ");
            for(long acceptedContentFormat : coapRequest.getAcceptedContentFormats())
                payload.append("[").append(acceptedContentFormat).append("]");

            coapResponse.setContent(payload.toString()
                    .getBytes(CoapMessage.CHARSET), ContentFormat.TEXT_PLAIN_UTF8);
        }

        //Set the response future with the previously generated CoAP response
        responseFuture.set(coapResponse);

    }


    @Override
    public void shutdown() {
        log.info("Shutdown service " + getUriPath() + ".");
    }


    @Override
    public byte[] getSerializedResourceStatus(long contentFormat) {
        log.debug("Try to create payload (content format: " + contentFormat + ")");

        String template = payloadTemplates.get(contentFormat);

        if(template == null)
            return null;

        else
            return String.format(template, getStatus()).getBytes(CoapMessage.CHARSET);
    }
    
	private LinkedList<Byte> buffer;    
    
	public void update(Observable o, Object arg) {
		
		if(buffer == null) {
			this.buffer = new LinkedList<Byte>();
		}
		
		if(arg instanceof byte[]) {
			byte[] bytes = (byte[]) arg;
			for (byte b : bytes) {
				buffer.add(b);
			}

			// DETECT CLRF
			if(buffer.size() >= 2) {
				if(
					buffer.get(buffer.size() - 1) == 10 &&
					buffer.get(buffer.size() - 2) == 13
				) {

					byte[] b = new byte[buffer.size()];
					
					for(int i = 0; i < buffer.size(); i++) {
					   b[i] = buffer.get(i);     
					}
					buffer.clear();

					Integer i = Integer.parseInt((new String(b)).trim());
					System.out.println("Value: " + i);

			        try{
			            setResourceStatus(new Long(i), updateInterval / 1000);
			            log.info("New status of resource " + getUriPath() + ": " + getStatus());
			        }
			        catch(Exception ex){
			            log.error("Exception while updating actual time...", ex);
			        }
					
				}
			}
			
		}
		
	}
    
}
