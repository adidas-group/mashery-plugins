package com.adidas.mashery.plugins;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mashery.http.HTTPHeaders;
import com.mashery.http.MutableHTTPHeaders;
import com.mashery.http.io.ContentProducer;
import com.mashery.http.server.HTTPServerRequest;
import com.mashery.trafficmanager.event.listener.Authenticator;
import com.mashery.trafficmanager.event.listener.TrafficEventListener;
import com.mashery.trafficmanager.event.model.TrafficEvent;
import com.mashery.trafficmanager.event.processor.model.AuthenticationEvent;
import com.mashery.trafficmanager.model.core.Key;
import com.mashery.trafficmanager.model.core.TrafficManagerResponse;
import com.mashery.trafficmanager.processor.ProcessorException;

public class JWTTokenAuthenticator implements TrafficEventListener,Authenticator{
	
	protected HashMap<String, String> claimsToCheck;
	protected static JWTVerifier verifier;
	protected String clientIDField;
	protected boolean validateAudience;
	
	protected HashMap<String, String> getClaimsToCheck() {
		return claimsToCheck;
	}
	protected void setClaimsToCheck(HashMap<String, String> claimsToCheck) {
		this.claimsToCheck = claimsToCheck;
	}
	
	public JWTTokenAuthenticator(String clientIDField, boolean validateAudience) throws IOException 
		{
		this.clientIDField=clientIDField;	
		this.validateAudience=validateAudience;		
	}
	
	protected static RSAPublicKey loadCertificate(String fileLocation, String failcrt)
	{
		FileInputStream input;
		byte[] publicKey;
		try {
			input = new FileInputStream(fileLocation);
			publicKey = new byte[input.available()];
			input.read(publicKey);
			input.close();
		} catch (IOException e) {
			publicKey = Base64.decodeBase64(failcrt.getBytes());
		}
		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// extraction cle
		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(publicKey);
		try {
			return (RSAPublicKey) keyFactory.generatePublic(pubSpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public void handleEvent(TrafficEvent event) {
		// TODO Auto-generated method stub
		try {
			if (event instanceof AuthenticationEvent) {
				authenticate((AuthenticationEvent) event);
			}
			} catch (ProcessorException e) {
			}
		
	}
	
	private void authenticate(AuthenticationEvent event)
			throws ProcessorException {
		{
			HTTPServerRequest httpReq = event.getCallContext().getRequest().getHTTPRequest();
			HTTPHeaders headers =  httpReq.getHeaders();
			String JWTToken = headers.get("authorization");
			// manipulate the response
			TrafficManagerResponse response = event.getCallContext().getResponse();
			MutableHTTPHeaders responseHeaders = event.getCallContext().getResponse().getHTTPResponse().getHeaders();
			responseHeaders.add("X-Auth-By", "JWTTokenAuthenticator");
			
			responseHeaders.add("X-URI", event.getCallContext().getRequest().getURI().toString());
			
			if (JWTToken == null || JWTToken.length() < 8) {
				denyTraffic(event);
				return;
			}
			// remove the Bearer part of the string
			JWTToken = JWTToken.substring(7);
						
			try {
				
				DecodedJWT jwt=verifier.verify(JWTToken);
				//additional checks
				//validate client id in the token to be equal to the one from api key
				if(this.clientIDField!=null)
				{
					//get app key
					Key apikey = event.getKey();
					//external client id
					String externalAppId=apikey.getApplication().getExternalID();
					Claim clientIdclaim = jwt.getClaim(this.clientIDField);
					if (clientIdclaim==null || !clientIdclaim.asString().equals(externalAppId))
					{
						responseHeaders.add("X-Error", "failed to clid");
						responseHeaders.add("X-clientIdclaim", clientIdclaim.asString());
						responseHeaders.add("X-externalAppId", externalAppId);
						denyTraffic(event);
						return;
					}					
				}
				//validate audience
				if(this.validateAudience)
				{
					Claim audclaim = jwt.getClaim("aud");
					String mashURI=event.getCallContext().getRequest().getURI().toString();
					// if aud claim is empty or mashuri doesnt contain aud claim value
					if(audclaim==null || (!mashURI.toLowerCase().contains(audclaim.asString().toLowerCase())))
					{
						responseHeaders.add("X-Error", "failed to aud");
						denyTraffic(event);
						return;
					}
				}			
				
			} catch (JWTVerificationException exception) {
				responseHeaders.add("X-Error", "failed to validate");
				denyTraffic(event);
			}			
		}
			
	}
	
	private void denyTraffic(AuthenticationEvent event) {
		event.getCallContext().getResponse().setComplete();
		return;
	}
}
