package com.cloudsherpas.google.api;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Arrays;

import com.cloudsherpas.GlobalConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;

public class BigQueryApi implements GlobalConstants {

	public static final String KEY_PASSWORD = "notasecret";

	public GoogleCredential getCredential() {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();

		try {
			GoogleCredential credential = new GoogleCredential.Builder()
					.setTransport(httpTransport)
					.setJsonFactory(jsonFactory)
					.setServiceAccountId(SERVICE_ACCOUNT_ID)
					.setServiceAccountScopes(Arrays.asList(BigqueryScopes.BIGQUERY))
					.setServiceAccountPrivateKey(loadKeyFromPkcs12()).build();

			return credential;
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Bigquery authenticateToBigQuery() {
		HttpTransport httTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		
		GoogleCredential credential = getCredential();
		Bigquery bigquery = new Bigquery.Builder(httTransport, jsonFactory,
				credential).setApplicationName("iBrowser")
				.setHttpRequestInitializer(credential).build();
		
		return bigquery;
	}

    private PrivateKey loadKeyFromPkcs12() throws Exception {
        InputStream fis = getClass().getResourceAsStream(SERVICE_ACCOUNT_PKCS12_FILE_PATH);
        KeyStore ks = KeyStore.getInstance("PKCS12");

        ks.load(fis, KEY_PASSWORD.toCharArray());
        return (PrivateKey) ks.getKey("privatekey", KEY_PASSWORD.toCharArray());
    }

}
