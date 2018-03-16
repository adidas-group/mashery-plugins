package com.adidas.mashery.plugins;

import java.util.Map;

import com.mashery.http.MutableHTTPHeaders;
import com.mashery.http.client.HTTPClientRequest;
import com.mashery.trafficmanager.event.listener.TrafficEventListener;
import com.mashery.trafficmanager.event.model.TrafficEvent;
import com.mashery.trafficmanager.event.processor.model.PreProcessEvent;
import com.mashery.trafficmanager.model.core.Endpoint;
import com.mashery.trafficmanager.model.core.Processor;
import com.mashery.trafficmanager.processor.ProcessorBean;

@ProcessorBean(enabled=true, name="com.adidas.mashery.plugins.AddHeaderProcessor", immediate=true)
public class AddHeaderProcessor implements TrafficEventListener
{
	 public void handleEvent(TrafficEvent event) 
	 {
    	if (event instanceof PreProcessEvent) {
			
			PreProcessEvent preprocessor = (PreProcessEvent)event;
			HTTPClientRequest httpReq = preprocessor.getClientRequest();
			Endpoint ep = preprocessor.getEndpoint();
			Processor preProc = ep.getProcessor();
			Map<String, String> params = preProc.getPreProcessorParameters();
			MutableHTTPHeaders headers = httpReq.getHeaders();
		    headers.add("X-Pre-Processed-By", "AddHeaderProcessor"); 
		    for (Map.Entry<String, String> entry : params.entrySet())
		    {
		        headers.add(entry.getKey(), entry.getValue());
		    }		    
		}
    	}
}
