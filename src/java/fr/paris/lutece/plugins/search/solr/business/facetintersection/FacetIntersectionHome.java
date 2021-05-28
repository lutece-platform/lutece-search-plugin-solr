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
package fr.paris.lutece.plugins.search.solr.business.facetintersection;

import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.plugins.search.solr.service.SolrPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for FacetIntersection objects
 */
public final class FacetIntersectionHome
{
    // Static variable pointed at the DAO instance
    private static IFacetIntersectionDAO _dao = SpringContextService.getBean( "facetIntersectionDAO" );
    private static Plugin _plugin = PluginService.getPlugin( SolrPlugin.PLUGIN_NAME );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FacetIntersectionHome( )
    {
    }

    /**
     * Create an instance of the facetIntersection class
     * 
     * @param facetIntersection
     *            The instance of the FacetIntersection which contains the informations to store
     * @return The instance of facetIntersection which has been created with its primary key.
     */
    public static FacetIntersection create( FacetIntersection facetIntersection )
    {
        _dao.insert( facetIntersection, _plugin );

        return facetIntersection;
    }

    public static FacetIntersection create( int idField1, int idField2 )
    {
        Field f1 = new Field( );
        f1.setIdField( idField1 );

        Field f2 = new Field( );
        f2.setIdField( idField2 );

        FacetIntersection fi = new FacetIntersection( );
        fi.setField1( f1 );
        fi.setField2( f2 );
        _dao.insert( fi, _plugin );

        return fi;
    }

    /**
     * Remove the facetIntersection whose identifier is specified in parameter
     * 
     * @param nFacetIntersectionId
     *            The facetIntersection Id
     * @param nFacetIntersectionId2
     *            The other facetIntersection Id
     */
    public static void remove( int nFacetIntersectionId, int nFacetIntersectionId2 )
    {
        _dao.delete( nFacetIntersectionId, nFacetIntersectionId2, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data of all the facetIntersection objects and returns them in form of a list
     * 
     * @return the list which contains the data of all the facetIntersection objects
     */
    public static List<FacetIntersection> getFacetIntersectionsList( )
    {
        return _dao.selectFacetIntersectionsList( _plugin );
    }
}
