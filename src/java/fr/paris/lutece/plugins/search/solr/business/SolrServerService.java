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
package fr.paris.lutece.plugins.search.solr.business;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;

/**
 * This service provides an instance of SolrServer.
 *
 */
public final class SolrServerService
{
    private static final String PROPERTY_SOLR_SERVER_URL = "solr.server.address";
    private static final String PROPERTY_SOLR_TIMEOUT = "solr.server.timeout";
    private static final String PROPERTY_SOLR_IDLE_TIMEOUT = "solr.idle.timeout";
    private static final String PROPERTY_SOLR_HTTP_BASIC_AUTH_USER = "solr.httpBasicAuthUser";
    private static final String PROPERTY_SOLR_HTTP_BASIC_AUTH_PASSWORD = "solr.httpBasicAuthPassword";
        
    private static final String SOLR_SERVER_URL = AppPropertiesService.getProperty( PROPERTY_SOLR_SERVER_URL );
    private static final int SOLR_CONNECTION_TIMEOUT = AppPropertiesService.getPropertyInt( PROPERTY_SOLR_TIMEOUT, 60000 );
    private static final int SOLR_IDLE_TIMEOUT = AppPropertiesService.getPropertyInt( PROPERTY_SOLR_IDLE_TIMEOUT, 600000 );

    private static final String SOLR_HTTP_BASIC_AUTH_USER = AppPropertiesService.getProperty( PROPERTY_SOLR_HTTP_BASIC_AUTH_USER );
    private static final String SOLR_HTTP_BASIC_AUTH_PASSWORD = AppPropertiesService.getProperty( PROPERTY_SOLR_HTTP_BASIC_AUTH_PASSWORD );
    private static SolrServerService _instance;
    private SolrClient _solrServer;

    /**
     * Private constructor that creates the SolrServer.
     */
    private SolrServerService( )
    {
        _solrServer = createSolrServer( SOLR_SERVER_URL );
    }

    /**
     * Return the instance.
     * 
     * @return the instance.
     */
    public static SolrServerService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new SolrServerService( );
        }

        return _instance;
    }

    /**
     * Returns the SolrServer.
     * 
     * @return the SolrServer
     */
    public SolrClient getSolrServer( )
    {
        if ( _solrServer == null )
        {
            _solrServer = createSolrServer( SOLR_SERVER_URL );
        }

        return _solrServer;
    }

    /**
     * Creates the SolrServer.
     * 
     * @param strServerUrl
     *            the Solr server url
     * @return the SolrServer.
     */
    private SolrClient createSolrServer( String strServerUrl )
    {    	
    	return new Http2SolrClient.Builder(strServerUrl).connectionTimeout(SOLR_CONNECTION_TIMEOUT).idleTimeout(SOLR_IDLE_TIMEOUT).withBasicAuthCredentials(SOLR_HTTP_BASIC_AUTH_USER, SOLR_HTTP_BASIC_AUTH_PASSWORD).build();
    }
}
