/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.search.solr.business.indexeraction;

import java.util.List;

import fr.paris.lutece.plugins.search.solr.service.SolrPlugin;
import fr.paris.lutece.test.LuteceTestCase;

public class SolrIndexerActionBusinessTest extends LuteceTestCase
{
    public void testCRUD( )
    {
        SolrIndexerAction action = new SolrIndexerAction( );
        action.setIdDocument( "strIdDocument" );
        action.setIdPortlet( 14 );
        action.setIdTask( 15 );
        action.setTypeResource( "strTypeResource" );

        SolrIndexerActionHome.create( action, SolrPlugin._plugin );
        SolrIndexerAction loaded = SolrIndexerActionHome.findByPrimaryKey( action.getIdAction( ), SolrPlugin._plugin );
        assertEquals( action.getIdDocument( ), loaded.getIdDocument( ) );
        assertEquals( action.getIdPortlet( ), loaded.getIdPortlet( ) );
        assertEquals( action.getIdTask( ), loaded.getIdTask( ) );
        assertEquals( action.getTypeResource( ), loaded.getTypeResource( ) );

        action.setIdDocument( "strIdDocument2" );
        action.setIdPortlet( 114 );
        action.setIdTask( 115 );
        action.setTypeResource( "strTypeResource2" );

        SolrIndexerActionHome.update( action, SolrPlugin._plugin );
        loaded = SolrIndexerActionHome.findByPrimaryKey( action.getIdAction( ), SolrPlugin._plugin );
        assertEquals( action.getIdDocument( ), loaded.getIdDocument( ) );
        assertEquals( action.getIdPortlet( ), loaded.getIdPortlet( ) );
        assertEquals( action.getIdTask( ), loaded.getIdTask( ) );
        assertEquals( action.getTypeResource( ), loaded.getTypeResource( ) );

        List<SolrIndexerAction> list = SolrIndexerActionHome.getList( SolrPlugin._plugin );
        assertEquals( 1, list.size( ) );

        SolrIndexerActionHome.remove( action.getIdAction( ), SolrPlugin._plugin );
        loaded = SolrIndexerActionHome.findByPrimaryKey( action.getIdAction( ), SolrPlugin._plugin );
        assertNull( loaded );
    }
}
