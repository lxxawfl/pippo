/*
 * Copyright 2016 Herman Barrantes.
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
package ro.pippo.session.spymemcached;

import java.io.IOException;
import java.util.List;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.PippoRuntimeException;
import ro.pippo.core.PippoSettings;

/**
 *
 * @author Herman Barrantes
 * @since 11/05/2016
 */
public class SpymemcachedFactory {

    private static final Logger log = LoggerFactory.getLogger(SpymemcachedFactory.class);
    private static final String HOST = "memcached.hosts";
    private static final String PROT = "memcached.protocol";
    private static final String USER = "memcached.user";
    private static final String PASS = "memcached.password";
    private static final String AUTM = "memcached.authMechanisms";

    private SpymemcachedFactory() {
        throw new PippoRuntimeException("You can't make a instance of factory.");
    }

    /**
     * Create a memcached client with pippo settings.
     *
     * @param settings pippo settings
     * @return memcached client
     */
    public static final MemcachedClient create(final PippoSettings settings) {
        String host = settings.getString(HOST, "localhost:11211");
        String prot = settings.getString(PROT, "BINARY");
        Protocol protocol = Protocol.valueOf(prot);
        String user = settings.getString(USER, "");
        String pass = settings.getString(PASS, "");
        List<String> autM = settings.getStrings(AUTM);
        String[] mechanisms = autM.toArray(new String[autM.size()]);
        return create(host, protocol, user, pass, mechanisms);
    }

    /**
     * Create a memcached client with params.
     *
     * @param hosts whitespace or comma separated host or IP addresses and port
     * numbers of the form "host:port host2:port" or "host:port, host2:port"
     * @param protocol opcional, BINARY or TEXT
     * @param user opcional, user name o null
     * @param pass opcional, password o null
     * @param authMechanisms opcional, CRAM-MD5 and/or PLAIN
     * @return memcached client
     */
    public static final MemcachedClient create(
            String hosts,
            Protocol protocol,
            String user,
            String pass,
            String[] authMechanisms) {

        MemcachedClient client = null;
        try {
            ConnectionFactory connectionFactory;
            if (isNotNullOrEmpty(user)) {
                AuthDescriptor authDescriptor = new AuthDescriptor(
                        authMechanisms,
                        new PlainCallbackHandler(user, pass));
                connectionFactory = new ConnectionFactoryBuilder()
                        .setProtocol(protocol)
                        .setAuthDescriptor(authDescriptor)
                        .build();
            } else {
                connectionFactory = new ConnectionFactoryBuilder()
                        .setProtocol(protocol)
                        .build();
            }
            client = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(hosts));
        } catch (IOException ex) {
            log.error("An error occurred when creating the MemcachedClient.", ex);
            throw new PippoRuntimeException(ex);
        }
        return client;
    }

    /**
     * Validate if String is not null and not empty
     *
     * @param value String to validate
     * @return true if is not null or not empty, otherwise false
     */
    private static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
