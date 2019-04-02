package com.cloudsherpas.google.api;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

import org.apache.commons.codec.binary.Base64;

import com.cloudsherpas.GlobalConstants;

public class CloudStorageSignedUrl implements GlobalConstants {
  public static final String KEY_PASSWORD = "notasecret";

  // filename format should be like this {/folder/filename}
  private String fileName;
  private long expiration;

  public CloudStorageSignedUrl(String fileName) {
    this.fileName = fileName;
    this.expiration = System.currentTimeMillis() + 1000 * 5;
  }

  public String getSignedUrl() throws Exception {
    String signature = signString("GET\n\n\n" + expiration + "\n/" + REPORTS_BUCKET + fileName);

    String url = "http://storage.googleapis.com/" + REPORTS_BUCKET + fileName + "?GoogleAccessId="
        + SERVICE_ACCOUNT_ID + "&Expires=" + expiration + "&Signature="
        + URLEncoder.encode(signature, "UTF-8");

    return url;
  }

  /**
   * @param stringToSign As described in
   *                     https://developers.google.com/storage/docs/accesscontrol#Signed-URLs
   * @return Base64 encoded signature.
   * @throws Exception
   */
  public String signString(String stringToSign) throws Exception {
    // load key
    PrivateKey key = loadKeyFromPkcs12(SERVICE_ACCOUNT_PKCS12_FILE_PATH,
        KEY_PASSWORD.toCharArray());

    // sign data
    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initSign(key);
    signer.update(stringToSign.getBytes("UTF-8"));
    byte[] rawSignature = signer.sign();

    return new String(Base64.encodeBase64(rawSignature, false), "UTF-8");
  }

  private PrivateKey loadKeyFromPkcs12(String filename, char[] password) throws Exception {
    FileInputStream fis = new FileInputStream(
        this.getClass().getClassLoader().getResource(filename).getPath());
    KeyStore ks = KeyStore.getInstance("PKCS12");

    ks.load(fis, password);
    return (PrivateKey) ks.getKey("privatekey", password);
  }
}
