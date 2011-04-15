/*
 * Copyright (c) 2002-2008, Mairie de Paris
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

import fr.paris.lutece.plugins.search.solr.business.facetIntersection.FacetIntersection;
import fr.paris.lutece.plugins.search.solr.business.facetIntersection.FacetIntersectionHome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SolrFieldManager
{
    //Static
    private static List<Field> fieldList = loadFieldList(  );
    private static Map<String, Field> facetList = loadFacetList(  );
    private static List<Field> sortList = loadSortList(  );
    private static List<FacetIntersection> intersectionList = loadIntersectionList(  );

    //attributes
    private List<FacetHistorique> currentFacet;

    public SolrFieldManager(  )
    {
        this.currentFacet = new ArrayList<FacetHistorique>(  );
    }

    public void addFacet( String key )
    {
        FacetHistorique facet = new FacetHistorique( key, "" );

        for ( FacetHistorique f : this.currentFacet )
        {
            if ( f.name.equals( key ) )
            {
                return;
            }
        }

        for ( FacetHistorique f : this.currentFacet )
        {
            f.query += ( "&fq=" + key );
            facet.query += ( "&fq=" + f.name );
        }

        this.currentFacet.add( facet );
    }

    public List<FacetHistorique> getCurrentFacet(  )
    {
        return currentFacet;
    }

    //STATIC FUNCTIONS
    public static void reloadField(  )
    {
        fieldList = loadFieldList(  );
        facetList = loadFacetList(  );
        sortList = loadSortList(  );
    }

    public static void reloadIntersection(  )
    {
        intersectionList = loadIntersectionList(  );
    }

    /**
     * Load the solr fields in database
     * @return
     */
    private static List<Field> loadFieldList(  )
    {
        return FieldHome.getFieldsList(  );
    }

    private static Map<String, Field> loadFacetList(  )
    {
        Map<String, Field> result = new HashMap<String, Field>(  );

        for ( Field field : getFieldList(  ) )
        {
            result.put( field.getSolrName(  ), field );
        }

        return result;
    }

    private static List<Field> loadSortList(  )
    {
        List<Field> result = new ArrayList<Field>(  );

        for ( Field field : getFieldList(  ) )
        {
            if ( field.getIsSort(  ) )
            {
                result.add( field );
            }
        }

        return result;
    }

    private static List<FacetIntersection> loadIntersectionList(  )
    {
        List<FacetIntersection> list = FacetIntersectionHome.getFacetIntersectionsList(  );

        return list;
    }

    //Getters & setters
    public static List<Field> getFieldList(  )
    {
        return fieldList;
    }

    public static Map<String, Field> getFacetList(  )
    {
        return facetList;
    }

    public static List<Field> getSortList(  )
    {
        return sortList;
    }

    public static List<FacetIntersection> getIntersectionlist(  )
    {
        return intersectionList;
    }

    /**
     * Adds all fields of the given parameter list if they are not already in databse. Two fields with the same name are equals 
     * @param lstFields the fields list to add
     */
    public static void updateFields( List<Field> lstFields )
    {
    	List<Field> lstAllFields = FieldHome.getFieldsList();
    	for( Field field : lstFields )
    	{
    		boolean bInsert = true;
    		// Search the field with its name
    		for( Field actualField : lstAllFields )
    		{
    			if( actualField.getName() != null && actualField.getName().equals( field.getName() ) )
    			{
    				// A field with the same name already exists => we don't insert it
    				bInsert = false;
    				break;
    			}
    		}
    		if( bInsert )
    		{
    			FieldHome.create( field );
    		}
    	}
    }
    
    public class FacetHistorique
    {
        public String name;
        public String query;

        public FacetHistorique( String name, String query )
        {
            this.name = name;
            this.query = query;
        }

        public String getName(  )
        {
            return name;
        }

        public String getQuery(  )
        {
            return query;
        }
    }
}
