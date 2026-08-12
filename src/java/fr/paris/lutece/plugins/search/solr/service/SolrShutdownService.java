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
package fr.paris.lutece.plugins.search.solr.service;

import fr.paris.lutece.plugins.search.solr.business.SolrServerService;
import fr.paris.lutece.portal.service.init.ShutdownService;

/**
 * Closes the Solr client when the web application is undeployed.
 *
 * SolrServerService holds an Http2SolrClient, which owns a Jetty HttpClient and its own thread pool. Those threads are not managed by the servlet container : if
 * the client is never closed, they survive the undeployment and keep the stopped web application classloader alive, leaking it on every hot redeployment.
 *
 * Registered as a Spring bean so that ShutdownServiceManager picks it up through SpringContextService.getBeansOfType( ShutdownService.class ). The core runs it
 * from AppInitListener.contextDestroyed, before the Spring context itself is closed.
 */
public class SolrShutdownService implements ShutdownService
{
    private static final String SERVICE_NAME = "Solr shutdown service";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process( )
    {
        SolrServerService.shutdown( );
    }
}
