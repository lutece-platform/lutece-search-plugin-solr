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
package fr.paris.lutece.plugins.search.solr.business.field;

import fr.paris.lutece.plugins.search.solr.business.facetintersection.FacetIntersection;
import fr.paris.lutece.plugins.search.solr.business.facetintersection.FacetIntersectionHome;
import fr.paris.lutece.plugins.search.solr.util.SolrUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolrFieldManager
{
    // Static
    private static List<Field> _fieldList = loadFieldList( );
    private static Map<String, Field> _facetList = loadFacetList( );
    private static List<Field> _sortList = loadSortList( );
    private static List<FacetIntersection> _intersectionList = loadIntersectionList( );

    // attributes
    private List<FacetHistorique> _currentFacet;

    public SolrFieldManager( )
    {
        this._currentFacet = new ArrayList<>( );
    }

    public void addFacet( String key )
    {
        FacetHistorique facet = new FacetHistorique( key, "" );

        for ( FacetHistorique f : this._currentFacet )
        {
            if ( f._name.equals( key ) )
            {
                return;
            }
        }
        StringBuilder facetQuery = new StringBuilder( );
        for ( FacetHistorique f : this._currentFacet )
        {
            // encode key
            String strEncodedKey = SolrUtil.encodeUrl( key );
            f._query += ( "&fq=" + strEncodedKey );

            String strEncodedName = SolrUtil.encodeUrl( f._name );
            facetQuery.append( "&fq=" ).append( strEncodedName );
        }
        facet._query = facetQuery.toString( );
        this._currentFacet.add( facet );
    }

    public List<FacetHistorique> getCurrentFacet( )
    {
        return _currentFacet;
    }

    // STATIC FUNCTIONS
    public static void reloadField( )
    {
        _fieldList = loadFieldList( );
        _facetList = loadFacetList( );
        _sortList = loadSortList( );
    }

    public static void reloadIntersection( )
    {
        _intersectionList = loadIntersectionList( );
    }

    /**
     * Load the solr fields in database
     * 
     * @return
     */
    private static List<Field> loadFieldList( )
    {
        return FieldHome.getFieldsList( );
    }

    private static Map<String, Field> loadFacetList( )
    {
        Map<String, Field> result = new HashMap<>( );

        for ( Field field : getFieldList( ) )
        {
            result.put( field.getSolrName( ), field );
        }

        return result;
    }

    private static List<Field> loadSortList( )
    {
        List<Field> result = new ArrayList<>( );

        for ( Field field : getFieldList( ) )
        {
            if ( field.getIsSort( ) )
            {
                result.add( field );
            }
        }

        return result;
    }

    private static List<FacetIntersection> loadIntersectionList( )
    {
        return FacetIntersectionHome.getFacetIntersectionsList( );
    }

    // Getters & setters
    public static List<Field> getFieldList( )
    {
        return _fieldList;
    }

    public static Map<String, Field> getFacetList( )
    {
        return _facetList;
    }

    public static List<Field> getSortList( )
    {
        return _sortList;
    }

    public static List<FacetIntersection> getIntersectionlist( )
    {
        return _intersectionList;
    }

    /**
     * Adds all fields of the given parameter list if they are not already in databse. Two fields with the same name are equals
     * 
     * @param lstFields
     *            the fields list to add
     */
    public static void updateFields( List<Field> lstFields )
    {
        List<Field> lstAllFields = FieldHome.getFieldsList( );

        for ( Field field : lstFields )
        {
            boolean bInsert = true;

            // Search the field with its name
            for ( Field actualField : lstAllFields )
            {
                if ( ( actualField.getName( ) != null ) && actualField.getName( ).equals( field.getName( ) ) )
                {
                    // A field with the same name already exists => we don't insert it
                    bInsert = false;

                    break;
                }
            }

            if ( bInsert )
            {
                FieldHome.create( field );
            }
        }
    }

    public static class FacetHistorique
    {
        private String _name;
        private String _query;

        public FacetHistorique( String name, String query )
        {
            this._name = name;
            this._query = query;
        }

        public String getName( )
        {
            return _name;
        }

        public String getQuery( )
        {
            return _query;
        }
    }

}
