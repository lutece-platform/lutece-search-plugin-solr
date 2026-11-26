/*
 * Copyright (c) 2002-2026, City of Paris
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
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
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

import java.lang.reflect.Field;

import jakarta.enterprise.inject.Vetoed;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.BaseHttpSolrClient;
import org.apache.solr.common.util.NamedList;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * A query refused by Solr (for example a query field unknown to the schema: 400 « undefined field ») must not escape as a
 * runtime exception: the front office search answered 500 to every user.
 */
public class SolrSearchEngineRemoteErrorTest extends LuteceTestCase
{
    /** Solr client refusing every request, as Solr does for a query field missing from the schema */
    private static final class RefusingSolrClient extends SolrClient
    {
        private static final long serialVersionUID = 1L;

        @Override
        public NamedList<Object> request( SolrRequest<?> request, String collection )
        {
            throw new BaseHttpSolrClient.RemoteSolrException( "http://solr/solr/lutece", 400, "undefined field e2e_unknown_s", null );
        }

        @Override
        public void close( )
        {
            // nothing to close
        }
    }

    /** Server service handing the refusing client (not a CDI bean: the test sets it by hand) */
    @Vetoed
    private static final class RefusingSolrServerService extends SolrServerService
    {
        @Override
        public SolrClient getSolrServer( )
        {
            return new RefusingSolrClient( );
        }
    }

    private SolrSearchEngine engineWithRefusingSolr( ) throws Exception
    {
        SolrSearchEngine engine = new SolrSearchEngine( );
        Field field = SolrSearchEngine.class.getDeclaredField( "_solrServerService" );
        field.setAccessible( true );
        field.set( engine, new RefusingSolrServerService( ) );
        return engine;
    }

    /**
     * Faceted search (the front office search page): empty result instead of an exception
     */
    @Test
    public void testFacetedSearchRefusedBySolrGivesAnEmptyResult( ) throws Exception
    {
        SolrFacetedResult result = engineWithRefusingSolr( ).getFacetedSearchResults( "test", null, null, null, 100, 1, 10, false );

        assertNotNull( result );
        assertTrue( result.getSolrSearchResults( ).isEmpty( ) );
    }

    /**
     * Site search (SearchEngine of the core, page=search): empty list instead of an exception
     */
    @Test
    public void testSiteSearchRefusedBySolrGivesAnEmptyList( ) throws Exception
    {
        assertTrue( engineWithRefusingSolr( ).getSearchResults( "test", new MockHttpServletRequest( ) ).isEmpty( ) );
    }

    /**
     * Suggestions (autocomplete of the search box): empty list instead of an exception
     */
    @Test
    public void testSuggestionsRefusedBySolrGiveAnEmptyList( ) throws Exception
    {
        assertTrue( engineWithRefusingSolr( ).getSuggestions( "test" ).isEmpty( ) );
    }
}
