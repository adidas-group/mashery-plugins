package com.adidas.mashery.plugins;

import com.adidas.mashery.plugins.utils.IpWhitelist;

import com.mashery.http.MutableHTTPHeaders;
import com.mashery.http.client.HTTPClientRequest;
import com.mashery.trafficmanager.event.listener.TrafficEventListener;
import com.mashery.trafficmanager.event.model.TrafficEvent;
import com.mashery.trafficmanager.event.processor.model.PreProcessEvent;
import com.mashery.trafficmanager.model.core.Endpoint;
import com.mashery.trafficmanager.model.core.Processor;
import com.mashery.trafficmanager.processor.ProcessorBean;

@ProcessorBean(enabled=true, name="com.adidas.mashery.plugins.IpWhitelistProcessor", immediate=true)
public class IpWhitelistProcessor implements TrafficEventListener {
  public void handleEvent(TrafficEvent event) {
    if (event instanceof PreProcessEvent) {
      PreProcessEvent preprocessor = (PreProcessEvent)event;
      HTTPClientRequest httpReq = preprocessor.getClientRequest();
      Endpoint ep = preprocessor.getEndpoint();
      Processor preProc = ep.getProcessor();

      // Prepare preprocessor
      MutableHTTPHeaders headers = httpReq.getHeaders();
      headers.add("X-Pre-Processed-By", "IpWhitelistProcessor");

      // TODO: how to get source IP?
      // if(!isWhitelisted(preProc.getPreProcessorParameters(), httpReq.getRemoteIp())) {
      //
      // }
    }
  }
}
