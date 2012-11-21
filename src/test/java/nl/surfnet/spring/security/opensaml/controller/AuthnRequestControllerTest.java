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

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

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
    //just test for non %
    getMetaData(null);
  }

  @Test
  public void test_metadata_with_wrong_configured_metadata() throws IOException {
    //just test for non %
    getMetaData("metadata.does.not.exists.properties");
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
