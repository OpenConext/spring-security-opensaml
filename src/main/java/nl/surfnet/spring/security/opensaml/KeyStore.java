/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.spring.security.opensaml;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import nl.surfnet.spring.security.opensaml.util.KeyStoreUtil;

public class KeyStore implements InitializingBean {
  private String keystorePassword;
  private java.security.KeyStore keyStore;
  private Map<String, String> certificates;

  @Required
  public void setCertificates(Map<String, String> certificates) {
    this.certificates = certificates;
  }

  public java.security.KeyStore getJavaSecurityKeyStore() {
    return keyStore;
  }

  public void afterPropertiesSet() throws Exception {
    keystorePassword = "secret";
    try {
      keyStore = java.security.KeyStore.getInstance("JKS");
      keyStore.load(null, keystorePassword.toCharArray());
      for (Map.Entry<String, String> entry : certificates.entrySet()) {
        KeyStoreUtil.appendCertificateToKeyStore(keyStore, entry.getKey(), entry.getValue());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
