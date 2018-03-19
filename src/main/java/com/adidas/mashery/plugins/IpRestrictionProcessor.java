package com.adidas.mashery.plugins;

import java.util.Map;

import com.adidas.mashery.plugins.utils.IpRestriction;

import com.mashery.http.MutableHTTPHeaders;
import com.mashery.http.client.HTTPClientRequest;
import com.mashery.http.server.HTTPServerRequest;
import com.mashery.trafficmanager.event.listener.TrafficEventListener;
import com.mashery.trafficmanager.event.model.TrafficEvent;
import com.mashery.trafficmanager.event.processor.model.PreProcessEvent;
import com.mashery.trafficmanager.model.core.Endpoint;
import com.mashery.trafficmanager.model.core.Processor;
import com.mashery.trafficmanager.processor.ProcessorBean;

@ProcessorBean(enabled=true, name="com.adidas.mashery.plugins.IpRestrictionProcessor", immediate=true)
public class IpRestrictionProcessor implements TrafficEventListener {
  public void handleEvent(TrafficEvent event) {
    if (event instanceof PreProcessEvent) {
      PreProcessEvent preProcess = (PreProcessEvent)event;
      HTTPClientRequest clientRequest = preProcess.getClientRequest();
      HTTPServerRequest serverRequest = preProcess.getServerRequest();

      Endpoint endpoint = preProcess.getEndpoint();
      Processor processor = endpoint.getProcessor();
      Map<String, String> params = processor.getPreProcessorParameters();

      MutableHTTPHeaders headers = clientRequest.getHeaders();
      headers.add("X-Pre-Processed-By", "IpWhitelistProcessor");

      IpRestriction restrictions = new IpRestriction(params.get("whitelist"), params.get("blacklist"));

      if(!restrictions.isAllowed(serverRequest.getRemoteAddr())) {
        headers.add("Content-Type:", "application/problem+json");
        headers.add("X-Mashery-Error-Code:", "ERR_403_DEVELOPER_INACTIVE");
        preProcess.getCallContext().getResponse().setComplete();
      }
    }
  }
}
