package com.kixeye.chassis.bootstrap.configuration.zookeeper;

/*
 * #%L
 * Chassis Bootstrap
 * %%
 * Copyright (C) 2014 KIXEYE, Inc
 * %%
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
 * #L%
 */

import com.google.common.base.Charsets;
import org.apache.curator.ensemble.exhibitor.ExhibitorRestClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;

/**
 * A {@link ExhibitorRestClient} that uses HttpClient.
 * 
 * @author ebahtijaragic
 */
public class KixeyeExhibitorRestClient implements ExhibitorRestClient {
	private static final Logger logger = LoggerFactory.getLogger(KixeyeExhibitorRestClient.class);
	
	private final boolean useSsl;
	private final ClientConnectionManager connectionManager;
	private final HttpClient client;
	
	public KixeyeExhibitorRestClient(boolean useSsl) {
		this.useSsl = useSsl;
		this.connectionManager = new PoolingClientConnectionManager();
		this.client = new DefaultHttpClient(connectionManager);
	}
	
	/**
	 * @see org.apache.curator.ensemble.exhibitor.ExhibitorRestClient#getRaw(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	public String getRaw(String hostname, int port, String uriPath, String mimeType) throws Exception {
		URI requestUri = new URI(useSsl ? "https" : "http", null, hostname, port, uriPath, null, null);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Calling GET on exhibitor: {}", requestUri);
		}
		
		HttpGet getRequest = new HttpGet(requestUri);
        getRequest.setHeader("Accept", mimeType);
		
		HttpResponse getResponse = client.execute(getRequest);
		
		HttpEntity entity = getResponse.getEntity();
		
		try {
			String responseEntity = EntityUtils.toString(getResponse.getEntity(), Charsets.UTF_8);

            if(logger.isDebugEnabled()){
               logger.debug("Response from request {} returned response {}", requestUri, responseEntity);
            }

            return responseEntity;
		} finally {
			EntityUtils.consume(entity);
		}
	}
}
