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
package nl.surfnet.spring.security.opensaml.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;

import nl.surfnet.spring.security.opensaml.KeyStore;
import nl.surfnet.spring.security.opensaml.SAMLMessageHandlerImpl;
import nl.surfnet.spring.security.opensaml.SecurityPolicyDelegate;
import nl.surfnet.spring.security.opensaml.crypt.KeyStoreCredentialResolverDelegate;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml2.binding.decoding.HTTPPostSimpleSignDecoder;
import org.opensaml.ws.security.SecurityPolicyResolver;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.ws.security.provider.StaticSecurityPolicyResolver;
import org.opensaml.xml.parse.BasicParserPool;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

/**
 * AuthnRequestControllerTest.java
 * 
 */
public class AuthnRequestControllerTest {

  private static final String ENTITY_ID = "https;//localhost/entityID";
  private static final String ASSERTION_CONSUMER_SERVICE_URL = "https://localhost/assertionConsumerServiceURL";
  private AuthnRequestController controller = new AuthnRequestController();

  @Test
  public void test_metadata_with_overriden_metadata() throws IOException {
    String metaData = getMetaData("metadata.test.properties");

    assertTrue(metaData.contains(ENTITY_ID));
    assertTrue(metaData.contains(ASSERTION_CONSUMER_SERVICE_URL));

    assertTrue(metaData.contains("beheer@surfconext.nl"));
    assertTrue(metaData.contains("beheer-support@surfconext.nl"));

    assertTrue(metaData.contains("SURFConext"));
    assertTrue(metaData.contains("SURFConext-support-given-name"));

    assertTrue(metaData.contains("SURFConext-nice-name-en"));
    assertTrue(metaData.contains("SURFConext-nice-description-en"));

    assertTrue(metaData.contains("SURFConext-name-nl"));
    assertTrue(metaData.contains("SURFConext-description-nl"));
  }

  @Test
  public void test_metadata_with_default_metadata() throws IOException {
    // just test for non %
    getMetaData(null);
  }

  @Test
  public void test_metadata_with_wrong_configured_metadata() throws IOException {
    // just test for non %
    getMetaData("metadata.does.not.exists.properties");
  }

  @Test
  public void test_send_authn_request_happy_flow() throws Exception {
    DefaultBootstrap.bootstrap();
    controller.setEntityID("http://entityid");
    controller.setAssertionConsumerServiceURL("http://assertionConsumerServiceURL");
    KeyStoreCredentialResolverDelegate delegate = new KeyStoreCredentialResolverDelegate();
    delegate.setKeyStore(new KeyStore());
    controller.setCredentialResolver(delegate);

    SAMLMessageHandlerImpl samlMessageHandler = new SAMLMessageHandlerImpl(new HTTPPostSimpleSignDecoder(new BasicParserPool()),
        new StaticSecurityPolicyResolver(new SecurityPolicyDelegate(new ArrayList<SecurityPolicyRule>())));
    samlMessageHandler.setEntityId("http://entityid");
    samlMessageHandler.setVelocityEngine(velocityEngine());
    samlMessageHandler.setNeedsSigning(false);

    controller.setSAMLMessageHandler(samlMessageHandler);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    controller.commence("http://localhost:324", request, response);
    
    String content = response.getContentAsString();
    assertTrue(content.contains("<input type=\"hidden\" name=\"SAMLRequest\""));
  }

  protected VelocityEngine velocityEngine() {
    final VelocityEngineFactoryBean velocityEngineFactoryBean = new VelocityEngineFactoryBean();
    velocityEngineFactoryBean.setPreferFileSystemAccess(false);
    Properties velocityEngineProperties = new Properties();
    velocityEngineProperties.setProperty("resource.loader", "classpath");
    velocityEngineProperties.setProperty("classpath.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocityEngineFactoryBean.setVelocityProperties(velocityEngineProperties);
    try {
      return velocityEngineFactoryBean.createVelocityEngine();
    } catch (IOException e) {
      throw new RuntimeException("Unable to create velocity engine instance");
    }
  }

  private String getMetaData(String metaDataProperties) throws IOException, UnsupportedEncodingException {
    controller.setAssertionConsumerServiceURL(ASSERTION_CONSUMER_SERVICE_URL);
    controller.setEntityID(ENTITY_ID);
    controller.setMetaDataProperties(metaDataProperties);

    MockHttpServletResponse response = new MockHttpServletResponse();
    controller.metaData(response);

    String metaData = response.getContentAsString();

    assertFalse(metaData.contains("%"));
    return metaData;
  }

}
