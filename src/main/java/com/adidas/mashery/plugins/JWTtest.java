package com.adidas.mashery.plugins;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import sun.misc.BASE64Decoder;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509Key;
import org.apache.commons.codec.binary.Base64;

public class JWTtest {

	public static void main(String[] args) throws CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		//InputStream inputStream = new FileInputStream("C:\\projects\\Mashery\\bitbucket\\mashery\\plugins\\pub.pem");
		
		//FileInputStream input = new FileInputStream("pub.cer");
	    byte[] fileData = fileData=Base64.decodeBase64(new String("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsOyBTjil5YRlVJXIILgQ\n"
				+"0CJa/MhKAuvDRdFEhV1YjuW1XwlYrdJgxMyP/jCm9j43vL1VfkimLqu79Bl2dVer\n"
				+"3EYKSSfEFVKlzJQnd/0jnfGrqe/m7lHhQFO5Ui11gFr8iwQzOrkBnNw7XfvXWHgK\n"
				+"jVBDf//MH2SP58MkanhgnNTzRwSdbCaHiP/wgthJCiRLhjhKfWU5QG3QvSxPS7NK\n"
				+"yHbX8qYsO1yDdo1dqGZkawXvt4lV8O8Ad5nHtGwgLelSwbNZ6EzMLQ3inKEWuJ1M\n"
				+"qXCiYCk44J38B3fUUk69zRew9v8qSbgKPVyTI1SlwAiVI/FkP5kr7hClFUojubcj\n"
				+"zwIDAQAB\n").getBytes());
	    		//new byte[input.available()];
	    //input.read(fileData);
	    //input.close();
	    //String text = new String(fileData, "UTF-8");
	    //byte[] decoded = Base64.decodeBase64(text);
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    // extraction cle
	    X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(fileData);
	    RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);
		
		String token="Bearer eyJhbGciOiJSUzI1NiJ9.ewoJInJlc2VydmF0aW9uSWQiOiIxMjMiLAoJImVVQ0kiOiI0NTYiLAoJInNrdSI6Ijc4OSIsCgkic2hpcHBpbmdBZGRyZXNzSWQiOiIxMjMiLAoJImJpbGxpbmdBZGRyZXNzSWQiOiI5MDEiLAoJInBob25lTnVtYmVyIjoiMTIzNDU2Nzg5MCIsCgkiY2hlY2tvdXRXaW5kb3dFbmQiOiI2OTc4MCIsCgkiZXJyb3JVUkwiOiJodHRwOi8vbG9jYWxob3N0OjY3ODkwL2Vycm9yIgp9.W1FhHO6wa+eYZtjOLITouulFqRnPU8zgU/vtq2eG021JNX3Foyx3DptY3cwd2j8avUHb6oFcPDNvIs+1jKjbzyN0ttK+LzRvqMzki/nVTmx5a5RY2Rn4aFh64rtxLOmp+vdtCrS8/OI3JrQy3nj6gk5vLdy4pz5mrjgotKSbxtsnBdaszMTRDMQ1BEpnuLxUm5hpZ/rmD8x7rNnrRxOiw5VVSt6I8oihgT1fS6I9xjrCtJFSmWwQ+Un+TcVwISWCx7LpJer5z/gu2EzpxWLbiLy71GobwKoVfwGtkCYipCccUsmKXfNUAshsoqlDKo+MPrZYb7LiuDefC1E/j+tX/A==";
		token=token.substring(7);
		try {
		    Algorithm algorithm = Algorithm.RSA256(pubKey, null);
		    JWTVerifier verifier = JWT.require(algorithm)
		        .build(); //Reusable verifier instance
		    DecodedJWT jwt = verifier.verify(token);
		    String pay=jwt.getHeader();
		    System.out.print(pay);
			} catch (JWTVerificationException exception)
			{
				System.out.print(exception.getMessage());
		    //Invalid signature/claims
			}
		
		//RSAPrivateKey privKey=(RSAPrivateKey)this.keyPair.getPrivate();

	    /*KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	    keystore.load(is, "password".toCharArray());

	    String alias = "mashcrtstore";

	    Key key = keystore.getKey(alias, "password".toCharArray());
	    if (key instanceof PrivateKey) {
	      // Get certificate of public key
	      Certificate cert = keystore.getCertificate(alias);
	      // Get public key
	      PublicKey publicKey = cert.getPublicKey();
	      // Return a key pair
	      new KeyPair(publicKey, (PrivateKey) key);
	    }
		
		JwkStore jwkStore = new JwkStore("{JWKS_FILE_HOST}");

		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc7yUa5MhvcP03nJPyCPzZtQcGEp-zWfOkEE";
		RSAPublicKey publicKey = //Get the key instance
		RSAPrivateKey privateKey = //Get the key instance
		try {
		    Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer("auth0")
		        .build(); //Reusable verifier instance
		    DecodedJWT jwt = verifier.verify(token);
		} catch (JWTVerificationException exception){
		    //Invalid signature/claims
		}*/

	}

}
