/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.search.solr.business.facetIntersection;

import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.plugins.search.solr.business.field.FieldHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for FacetIntersection objects
 */
public final class FacetIntersectionDAO implements IFacetIntersectionDAO
{
    // Constants
    private static final String SQL_QUERY_INSERT = "INSERT INTO solr_facet_intersection ( id_field1, id_field2 ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM solr_facet_intersection WHERE id_field1 = ? and id_field2 = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_field1, id_field2 FROM solr_facet_intersection";

    /**
     * Insert a new record in the table.
     * @param facetIntersection instance of the FacetIntersection object to insert
     * @param plugin The plugin
     */
    public void insert( FacetIntersection facetIntersection, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setInt( 1, facetIntersection.getField1(  ).getIdField(  ) );
        daoUtil.setInt( 2, facetIntersection.getField2(  ).getIdField(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete a record from the table
     * @param nFacetIntersectionId The identifier of the facetIntersection
     * @param plugin The plugin
     */
    public void delete( int nFacetIntersectionId, int nFacetIntersectionId2, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nFacetIntersectionId );
        daoUtil.setInt( 2, nFacetIntersectionId2 );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the facetIntersections and returns them as a List
     * @param plugin The plugin
     * @return The List which contains the data of all the facetIntersections
     */
    public List<FacetIntersection> selectFacetIntersectionsList( Plugin plugin )
    {
        List<FacetIntersection> facetIntersectionList = new ArrayList<FacetIntersection>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            FacetIntersection facetIntersection = new FacetIntersection(  );
            Field field1 = FieldHome.findByPrimaryKey( daoUtil.getInt( 1 ) );
            Field field2 = FieldHome.findByPrimaryKey( daoUtil.getInt( 2 ) );
            facetIntersection.setField1( field1 );
            facetIntersection.setField2( field2 );

            facetIntersectionList.add( facetIntersection );
        }

        daoUtil.free(  );

        return facetIntersectionList;
    }
}
