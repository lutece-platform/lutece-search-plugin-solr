/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.search.solr.indexer.Authentification;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.ContextAwareAuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

public class SolrPreemptiveAuthInterceptor implements HttpRequestInterceptor {

    protected ContextAwareAuthScheme authScheme = new BasicScheme();

    @Override
    public void process(final HttpRequest request, final HttpContext context)
            throws HttpException, IOException {
        final AuthState authState = (AuthState) context
                .getAttribute(HttpClientContext.TARGET_AUTH_STATE);

        if (authState != null && authState.getAuthScheme() == null) {
            final CredentialsProvider credsProvider = (CredentialsProvider) context
                    .getAttribute(HttpClientContext.CREDS_PROVIDER);
            final HttpHost targetHost = (HttpHost) context
                    .getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            final Credentials creds = credsProvider
                    .getCredentials(new AuthScope(targetHost.getHostName(),
                            targetHost.getPort()));
            if (creds == null) {
                throw new HttpException(
                        "No credentials for preemptive authentication");
            }
            request.addHeader(authScheme.authenticate(creds, request, context));
        }
    }

    public ContextAwareAuthScheme getAuthScheme() {
        return authScheme;
    }

    public void setAuthScheme(final ContextAwareAuthScheme authScheme) {
        this.authScheme = authScheme;
    }

}