package com.adidas.mashery.plugins;

import java.io.IOException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mashery.trafficmanager.event.listener.LifeCycleContext;
import com.mashery.trafficmanager.event.listener.ListenerLifeCycle;
import com.mashery.trafficmanager.event.listener.ListenerLifeCycleException;
import com.mashery.trafficmanager.processor.ProcessorBean;
import java.security.interfaces.RSAPublicKey;

@ProcessorBean(enabled = true, name = "com.adidas.mashery.plugins.QAHypeTokenAuthenticator", immediate = true, lifeCycleClass="com.adidas.mashery.plugins.QAHypeTokenAuthenticator")
public final class QAHypeTokenAuthenticator extends JWTTokenAuthenticator implements ListenerLifeCycle{
	
	public QAHypeTokenAuthenticator() throws IOException {
		
		super("appid", true);	
	}
	@Override
	public void onUnLoad(LifeCycleContext arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoad(LifeCycleContext arg0) throws ListenerLifeCycleException {
		// TODO Auto-generated method stub
		RSAPublicKey pubKey=loadCertificate("/mnt/mashery/crt/devpub.cer",new String("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsOyBTjil5YRlVJXIILgQ\n"
				+ "0CJa/MhKAuvDRdFEhV1YjuW1XwlYrdJgxMyP/jCm9j43vL1VfkimLqu79Bl2dVer\n"
				+ "3EYKSSfEFVKlzJQnd/0jnfGrqe/m7lHhQFO5Ui11gFr8iwQzOrkBnNw7XfvXWHgK\n"
				+ "jVBDf//MH2SP58MkanhgnNTzRwSdbCaHiP/wgthJCiRLhjhKfWU5QG3QvSxPS7NK\n"
				+ "yHbX8qYsO1yDdo1dqGZkawXvt4lV8O8Ad5nHtGwgLelSwbNZ6EzMLQ3inKEWuJ1M\n"
				+ "qXCiYCk44J38B3fUUk69zRew9v8qSbgKPVyTI1SlwAiVI/FkP5kr7hClFUojubcj\n" 
				+ "zwIDAQAB\n"));
		Algorithm algorithm = Algorithm.RSA256(pubKey, null);
		verifier = JWT.require(algorithm)
					.withIssuer("http://sts-test.adidas-group.com/adfs/services/trust")
					.build(); // Reusable verifier instance	
	}
}
