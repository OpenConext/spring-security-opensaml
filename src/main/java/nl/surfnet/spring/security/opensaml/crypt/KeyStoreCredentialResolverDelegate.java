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

package nl.surfnet.spring.security.opensaml.crypt;

import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.credential.KeyStoreCredentialResolver;
import org.springframework.beans.factory.annotation.Required;

import nl.surfnet.spring.security.opensaml.KeyStore;

public class KeyStoreCredentialResolverDelegate implements CredentialResolver {

    private KeyStore keyStore;

    @Required
    public void setKeyStore(final KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public Iterable<Credential> resolve(CriteriaSet criteriaSet) throws SecurityException {
        return getKeyStoreCredentialResolver().resolve(criteriaSet);
    }

    public Credential resolveSingle(CriteriaSet criteriaSet) throws SecurityException {
        return getKeyStoreCredentialResolver().resolveSingle(criteriaSet);
    }

    public KeyStoreCredentialResolver getKeyStoreCredentialResolver() {
      return new KeyStoreCredentialResolver(keyStore.getJavaSecurityKeyStore(), keyStore.getPrivateKeyPasswords());
    }
}
