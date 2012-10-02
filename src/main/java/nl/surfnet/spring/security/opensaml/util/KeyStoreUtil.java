/*
 Copyright 2012 SURFnet bv, The Netherlands

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package nl.surfnet.spring.security.opensaml.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

public class KeyStoreUtil {

  /**
   * Append a certificate and private key to a keystore.
   *
   * @param keyStore        where to append the certificate and private key to
   * @param keyAlias        the alias of the key
   * @param certificateInputStream the inputStream containing the certificate in the PEM format
   * @param privatekeyInputStream  the input stream containing the private key in the DER format
   * @param password        the password on the key
   *                        <p/>
   *                        Generate your private key: openssl genrsa -out something.key 1024
   *                        <p/>
   *                        Show the PEM private key: openssl asn1parse -inform pem -dump -i
   *                        -in something.key
   *                        <p/>
   *                        Translate the key to pkcs8 DER format: openssl pkcs8 -topk8
   *                        -inform PEM -outform DER -in something.key -nocrypt >
   *                        something.pkcs8.der
   *                        <p/>
   *                        Show the DER private key: openssl asn1parse -inform der -dump -i
   *                        -in something.pkcs8.der
   *                        <p/>
   *                        Generate a certificate request: openssl req -new -key
   *                        something.key -out something.csr
   *                        <p/>
   *                        Generate a certificate: openssl x509 -req -days 365 -in
   *                        something.csr -signkey something.key -out something.crt
   */

  public static void appendKeyToKeyStore(KeyStore keyStore, String keyAlias, InputStream certificateInputStream, InputStream privatekeyInputStream, char[] password) throws IOException {

    CertificateFactory certFact;
    Certificate cert;
    try {
      certFact = CertificateFactory.getInstance("X.509");
      cert = certFact.generateCertificate(certificateInputStream);
    } catch (CertificateException e) {
      throw new RuntimeException("Could not instantiate cert", e);
    }
    ArrayList<Certificate> certs = new ArrayList<Certificate>();
    certs.add(cert);

    byte[] privKeyBytes = IOUtils.toByteArray(privatekeyInputStream);

    try {
      KeySpec ks = new PKCS8EncodedKeySpec(privKeyBytes);
      RSAPrivateKey privKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(ks);
      keyStore.setKeyEntry(keyAlias, privKey, password, certs.toArray(new Certificate[certs.size()]));
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    } catch (KeyStoreException e) {
      throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Append a certificate to the given key store
   * @param keyStore
   * @param keyAlias
   * @param pemCert
   */
  public static void appendCertificateToKeyStore(KeyStore keyStore, String keyAlias, String pemCert) {
    String wrappedCert = "-----BEGIN CERTIFICATE-----\n" + pemCert + "\n-----END CERTIFICATE-----";
    ByteArrayInputStream certificateInputStream = new ByteArrayInputStream(wrappedCert.getBytes());
    try {
      final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      final Certificate cert = certificateFactory.generateCertificate(certificateInputStream);
      IOUtils.closeQuietly(certificateInputStream);
      keyStore.setCertificateEntry(keyAlias, cert);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
