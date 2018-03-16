package com.adidas.mashery.plugins;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mashery.http.MutableHTTPHeaders;
import com.mashery.http.client.HTTPClientRequest;
import com.mashery.http.io.ContentProducer;
import com.mashery.http.server.HTTPServerResponse;
import com.mashery.trafficmanager.event.listener.LifeCycleContext;
import com.mashery.trafficmanager.event.listener.ListenerLifeCycle;
import com.mashery.trafficmanager.event.listener.ListenerLifeCycleException;
import com.mashery.trafficmanager.event.listener.TrafficEventListener;
import com.mashery.trafficmanager.event.model.TrafficEvent;
import com.mashery.trafficmanager.event.processor.model.PreProcessEvent;
import com.mashery.trafficmanager.model.core.Application;
import com.mashery.trafficmanager.model.core.Key;
import com.mashery.trafficmanager.model.core.TrafficManagerResponse;
import com.mashery.trafficmanager.processor.ProcessorBean;

@ProcessorBean(enabled = true, name = "com.adidas.mashery.plugins.JWTTokenPreValidator", immediate = true, lifeCycleClass="com.adidas.mashery.plugins.JWTTokenPreValidator")
public class JWTTokenPreValidator implements TrafficEventListener, ListenerLifeCycle {
	private RSAPublicKey pubKey;
	private static String val="nonloaded";

	public JWTTokenPreValidator() throws IOException {
		FileInputStream input;
		byte[] publicKey;
		try {
			input = new FileInputStream("pub.cer");
			publicKey = new byte[input.available()];
			input.read(publicKey);
			input.close();
		} catch (FileNotFoundException e) {
			publicKey = Base64
					.decodeBase64(new String("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsOyBTjil5YRlVJXIILgQ\n"
							+ "0CJa/MhKAuvDRdFEhV1YjuW1XwlYrdJgxMyP/jCm9j43vL1VfkimLqu79Bl2dVer\n"
							+ "3EYKSSfEFVKlzJQnd/0jnfGrqe/m7lHhQFO5Ui11gFr8iwQzOrkBnNw7XfvXWHgK\n"
							+ "jVBDf//MH2SP58MkanhgnNTzRwSdbCaHiP/wgthJCiRLhjhKfWU5QG3QvSxPS7NK\n"
							+ "yHbX8qYsO1yDdo1dqGZkawXvt4lV8O8Ad5nHtGwgLelSwbNZ6EzMLQ3inKEWuJ1M\n"
							+ "qXCiYCk44J38B3fUUk69zRew9v8qSbgKPVyTI1SlwAiVI/FkP5kr7hClFUojubcj\n" + "zwIDAQAB\n")
									.getBytes());
		}
		// String text = new String(fileData, "UTF-8");
		// byte[] decoded = Base64.decodeBase64(text);
		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// extraction cle
		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(publicKey);
		try {
			pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void handleEvent(TrafficEvent event) {
		if (event instanceof PreProcessEvent) {

			PreProcessEvent preprocessor = (PreProcessEvent) event;
			HTTPClientRequest httpReq = preprocessor.getClientRequest();
			MutableHTTPHeaders headers = httpReq.getHeaders();
			String JWTToken = headers.get("Authorization");
			TrafficManagerResponse response = preprocessor.getCallContext().getResponse();
			if (JWTToken == null || JWTToken.length() < 8) {
				HTTPServerResponse httpResponse = response.getHTTPResponse();
				httpResponse.setBody(new StringContentProducer(
						"{\"title\": \"Forbidden\",\"detail\": \"Authorization header is missing\"}"));
				httpResponse.setStatusCode(403);
				httpResponse.getHeaders().add("Content-Type:", "application/problem+json");
				httpResponse.getHeaders().add("X-Mashery-Error-Code:", "ERR_403_DEVELOPER_INACTIVE");
				preprocessor.getCallContext().getResponse().setComplete();
				return;

			}
			// remove the Bearer part of the string
			JWTToken = JWTToken.substring(7);
			Algorithm algorithm = Algorithm.RSA256(pubKey, null);
			JWTVerifier verifier = JWT.require(algorithm)
					// .withIssuer("adidas")
					.build(); // Reusable verifier instance
			Key apikey = preprocessor.getKey();
			Application apikey_app = apikey.getApplication();
			
			
			//external client id
			String externalAppId=apikey.getApplication().getExternalID();

			headers.add("X-Pre-Processed-By", "JWTTokenPreValidator");
			headers.add("X-App-ExtID", externalAppId);
			headers.add("X-VAL", this.val);
			try {
				verifier.verify(JWTToken);
			} catch (JWTVerificationException exception) {
				HTTPServerResponse httpResponse = response.getHTTPResponse();
				httpResponse.setBody(new StringContentProducer(
						"{\"title\": \"Forbidden\",\"detail\": \"The request is not authorized\"}"));
				httpResponse.getHeaders().add("Content-Type:", "application/problem+json");
				httpResponse.getHeaders().add("X-Mashery-Error-Code:", "ERR_403_DEVELOPER_INACTIVE");
				httpResponse.setStatusCode(403);
				response.setComplete();
				System.out.print(exception.getMessage());
			}
		}

	}
	private class StringContentProducer implements ContentProducer {
		private static final int MAX_FIXED_LENGTH = 4096;
		protected final String content;
		protected final Charset charset;
		private byte[] buffer;
		private long contentLength = -2L;
		/**
		 * @param content string content to output
		 * @param charset desired output charset
		 */
		public StringContentProducer(String content, Charset charset) {
			this.content = content;
			if (charset != null) {
				this.charset = charset;
			} else {
				this.charset = Charset.defaultCharset();
			}
		}
		public StringContentProducer(String content) {
			this(content, Charset.forName("UTF-8"));
		}
		private void initialize() {
			if (content == null) {
				return;
			}
			if (contentLength == -2L) {
				String content = this.content;
				int len = content.length();
				if (len > MAX_FIXED_LENGTH) {
					// we'll chunk
					contentLength = -1L;
				} else {
					// small enough to buffer ahead
					byte[] buffer = content.getBytes(charset);
					contentLength = buffer.length;
					this.buffer = buffer;
				}
			}
		}
		@Override
		public long getContentLength() {
			initialize();
			return contentLength;
		}
		@Override
		public void writeTo(OutputStream out) throws IOException {
			initialize();
			byte[] buffer = this.buffer;
			if (buffer == null) {
				String content = this.content;
				int chunkSize = MAX_FIXED_LENGTH;
				Charset charset = this.charset;
				if (content != null) {
					for (int i = 0, n = content.length(); i < n; i += chunkSize) {
						int end = Math.min(n, i + chunkSize);
						String str = content.substring(i, end);
						out.write(str.getBytes(charset));
					}
				} else {
					throw new IOException("No content to write");
				}
			} else {
				out.write(buffer);
			}
		}
		@Override
		public boolean isRepeatable() {
			return true;
		}
}
	@Override
	public void onLoad(LifeCycleContext arg0) throws ListenerLifeCycleException {
		// TODO Auto-generated method stub
		val="Loaded";
	}

	@Override
	public void onUnLoad(LifeCycleContext arg0) {
		// TODO Auto-generated method stub
		
	}
}