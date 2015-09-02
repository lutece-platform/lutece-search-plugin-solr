/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.search.solr.business.facetIntersection.FacetIntersection;
import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.plugins.search.solr.business.field.SolrFieldManager;
import fr.paris.lutece.plugins.search.solr.indexer.SolrIndexerService;
import fr.paris.lutece.plugins.search.solr.indexer.SolrItem;
import fr.paris.lutece.plugins.search.solr.util.SolrConstants;
import fr.paris.lutece.plugins.search.solr.util.SolrUtil;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.service.search.SearchEngine;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * SolrSearchEngine
 *
 */
public class SolrSearchEngine implements SearchEngine
{
    private static final String PROPERTY_SOLR_HIGHLIGHT_PRE = "solr.highlight.pre";
    private static final String PROPERTY_SOLR_HIGHLIGHT_POST = "solr.highlight.post";
    private static final String PROPERTY_SOLR_HIGHLIGHT_SNIPPETS = "solr.highlight.snippets";
    private static final String PROPERTY_SOLR_HIGHLIGHT_FRAGSIZE = "solr.highlight.fragsize";
    private static final String PROPERTY_SOLR_FACET_DATE_START = "solr.facet.date.start";
    private static final String PROPERTY_SOLR_FACET_MIN_COUNT = "solr.facet.minCount";
    
    private static final String SOLR_HIGHLIGHT_PRE = AppPropertiesService.getProperty( PROPERTY_SOLR_HIGHLIGHT_PRE );
    private static final String SOLR_HIGHLIGHT_POST = AppPropertiesService.getProperty( PROPERTY_SOLR_HIGHLIGHT_POST );
    private static final int SOLR_HIGHLIGHT_SNIPPETS = AppPropertiesService.getPropertyInt( PROPERTY_SOLR_HIGHLIGHT_SNIPPETS,
            5 );
    private static final int SOLR_HIGHLIGHT_FRAGSIZE = AppPropertiesService.getPropertyInt( PROPERTY_SOLR_HIGHLIGHT_FRAGSIZE,
            100 );
    private static final String SOLR_FACET_DATE_START = AppPropertiesService.getProperty( PROPERTY_SOLR_FACET_DATE_START );
    private static final int SOLR_FACET_MIN_COUNT = AppPropertiesService.getPropertyInt(PROPERTY_SOLR_FACET_MIN_COUNT, 1);
    
    public static final String SOLR_FACET_DATE_GAP = AppPropertiesService.getProperty( "solr.facet.date.gap", "+1YEAR" );
    public static final String SOLR_FACET_DATE_END = AppPropertiesService
            .getProperty( "solr.facet.date.end", "NOW" );
    public static final int SOLR_FACET_LIMIT = AppPropertiesService.getPropertyInt( "solr.facet.limit", 100 );
    private static SolrSearchEngine _instance;
    private static final String COLON_QUOTE = ":\"";
    private static final String DATE_COLON = "date:";

    /**
    * Return search results
    * @param strQuery The search query
    * @param request The HTTP request
    * @return Results as a collection of SearchResult
    */
    public List<SearchResult> getSearchResults( String strQuery, HttpServletRequest request )
    {
        List<SearchResult> results = new ArrayList<SearchResult>(  );

        SolrServer solrServer = SolrServerService.getInstance(  ).getSolrServer(  );

        if ( ( solrServer != null ) && !strQuery.equals( "" ) )
        {
            SolrQuery query = new SolrQuery(  );

            if ( ( strQuery != null ) && ( strQuery.length(  ) > 0 ) )
            {
                query.setQuery( strQuery );
            }

            String[] userRoles;
            String[] roles;

            if ( SecurityService.isAuthenticationEnable(  ) )
            {
                // authentification on, check roles
                LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

                if ( user != null )
                {
                    userRoles = SecurityService.getInstance(  ).getRolesByUser( user );
                }
                else
                {
                    userRoles = new String[0];
                }

                roles = new String[userRoles.length + 1];
                System.arraycopy( userRoles, 0, roles, 0, userRoles.length );
                roles[roles.length - 1] = Page.ROLE_NONE;

                String filterRole = buildFilter( SearchItem.FIELD_ROLE, roles );
                // portlets roles
                //roles[roles.length - 1] = Portlet.ROLE_NONE;

                //String filterPortletRole = buildFilter( SearchItem.FIELD_PORTLET_ROLE, roles );
                query = query.addFilterQuery( filterRole ); //.addFilterQuery( filterPortletRole );
            }

            try
            {
                QueryResponse response = solrServer.query( query );
                SolrDocumentList documentList = response.getResults(  );
                results = SolrUtil.transformSolrDocumentList( documentList );
            }
            catch ( SolrServerException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        return results;
    }

    /**
     * Return the result with facets. Does NOT support authentification yet.
     * @param strQuery the query
     * @param facetQueries The selected facets
     * @param sortName The facet name to sort by
     * @param sortOrder "asc" or "desc"
     * @param nLimit Maximal number of results.
     * @return the result with facets
     */
    public SolrFacetedResult getFacetedSearchResults( String strQuery, String[] facetQueries, String sortName,
        String sortOrder, int nLimit, int nCurrentPageIndex, int nItemsPerPage )
    {
        SolrFacetedResult facetedResult = new SolrFacetedResult(  );

        SolrServer solrServer = SolrServerService.getInstance(  ).getSolrServer(  );
        List<SolrSearchResult> results = new ArrayList<SolrSearchResult>(  );

        if ( solrServer != null )
        {
            SolrQuery query = new SolrQuery( strQuery );
            query.setHighlight( true );
            query.setHighlightSimplePre( SOLR_HIGHLIGHT_PRE );
            query.setHighlightSimplePost( SOLR_HIGHLIGHT_POST );
            query.setHighlightSnippets( SOLR_HIGHLIGHT_SNIPPETS );
            query.setHighlightFragsize( SOLR_HIGHLIGHT_FRAGSIZE );
            query.setFacet( true );
            query.setFacetLimit( SOLR_FACET_LIMIT );
            query.setFacetMinCount( SOLR_FACET_MIN_COUNT );

            for ( Field field : SolrFieldManager.getFacetList(  ).values(  ) )
            {
                //Add facet Field
                if ( field.getEnableFacet(  ) )
                {
                    if ( field.getName( ).contains( "date" ) )
                    {
                        query.setParam( "facet.date", field.getName( ) );
                        query.setParam( "facet.date.start", SOLR_FACET_DATE_START );
                        query.setParam( "facet.date.gap", SOLR_FACET_DATE_GAP );
                        query.setParam( "facet.date.end", SOLR_FACET_DATE_END );
                        query.setParam( "facet.date.mincount", "0" );
                    }
                    else
                    {
                        query.addFacetField( field.getSolrName(  ) );
                    }
                }
            }

            //Facet intersection
            List<String> treeParam = new ArrayList<String>(  );

            for ( FacetIntersection intersect : SolrFieldManager.getIntersectionlist(  ) )
            {
                treeParam.add( intersect.getField1(  ).getSolrName(  ) + "," + intersect.getField2(  ).getSolrName(  ) );
            }

            //(String []) al.toArray (new String [0]);
            query.setParam( "facet.tree", (String[]) treeParam.toArray( new String[0] ) );

            //sort order
            if ( ( sortName != null ) && !"".equals( sortName ) )
            {
                if ( sortOrder.equals( "asc" ) )
                {
                    query.setSort( sortName, ORDER.asc );
                }
                else if(sortOrder.equals("random"))
                {
                	query.setParam( "sort", sortName+"_random asc" );
                }
                else
                {
                	query.setSort( sortName, ORDER.desc );
                }
            }
            else
            {
                for ( Field field : SolrFieldManager.getSortList(  ) )
                {
                    if ( field.getDefaultSort(  ) )
                    {
                        query.setSort( field.getName(  ), ORDER.desc );
                    }
                }
            }

            //Treat HttpRequest
            //FacetQuery
            if ( facetQueries != null )
            {
                for ( String strFacetQuery : facetQueries )
                {
                    if ( strFacetQuery.contains( DATE_COLON ) )
                    {
                        query.addFilterQuery( strFacetQuery );
                    }
                    else
                    {
                        String strFacetQueryWithColon;
                        strFacetQueryWithColon = strFacetQuery.replaceFirst( SolrConstants.CONSTANT_COLON, COLON_QUOTE );
                        strFacetQueryWithColon += SolrConstants.CONSTANT_QUOTE;
                        query.addFilterQuery( strFacetQueryWithColon );
                    }
                }
            }

            try
            {

            	// count query
            	query.setRows( 0 );
                QueryResponse response = solrServer.query( query );
                
                int nResults = (int) response.getResults().getNumFound( );
                facetedResult.setCount( nResults > nLimit ? nLimit : nResults );
                
                query.setStart( ( nCurrentPageIndex - 1 ) * nItemsPerPage );
            	query.setRows( nItemsPerPage > nLimit ? nLimit : nItemsPerPage );
            	response = solrServer.query( query );
            	
                //HighLight
                Map<String, Map<String, List<String>>> highlightsMap = response.getHighlighting(  );
                SolrHighlights highlights = null;

                if ( highlightsMap != null )
                {
                    highlights = new SolrHighlights( highlightsMap );
                }

                //resultList
                List<SolrItem> itemList = response.getBeans( SolrItem.class );
                results = SolrUtil.transformSolrItemsToSolrSearchResults( itemList, highlights );

                //Date facet
                if ( ( response.getFacetDates(  ) != null ) && !response.getFacetDates(  ).isEmpty(  ) )
                {
                    facetedResult.setFacetDateList( response.getFacetDates(  ) );
                }

                //FacetField
                facetedResult.setFacetFields( response.getFacetFields(  ) );

                //Facet intersection (facet tree)
                NamedList<Object> resp = (NamedList<Object>) response.getResponse(  ).get( "facet_counts" );

                if ( resp != null )
                {
                    NamedList<NamedList<NamedList<Integer>>> trees = (NamedList<NamedList<NamedList<Integer>>>) resp.get( 
                            "trees" );
                    Map<String, ArrayList<FacetField>> treesResult = new HashMap<String, ArrayList<FacetField>>(  );

                    if ( trees != null )
                    {
                        for ( Entry<String, NamedList<NamedList<Integer>>> selectedFacet : trees )
                        { //Selected Facet (ex : type,categorie )
                          //System.out.println(selectedFacet.getKey());

                            ArrayList<FacetField> facetFields = new ArrayList<FacetField>( selectedFacet.getValue(  )
                                                                                                        .size(  ) );

                            for ( Entry<String, NamedList<Integer>> facetField : selectedFacet.getValue(  ) )
                            {
                                FacetField ff = new FacetField( facetField.getKey(  ) );

                                //System.out.println("\t" + facetField.getKey());
                                for ( Entry<String, Integer> value : facetField.getValue(  ) )
                                { // Second Level
                                    ff.add( value.getKey(  ), value.getValue(  ) );

                                    //System.out.println("\t\t" + value.getKey() + " : " + value.getValue());
                                }

                                facetFields.add( ff );
                            }

                            treesResult.put( selectedFacet.getKey(  ), facetFields );
                        }
                    }

                    facetedResult.setFacetIntersection( treesResult );
                }
            }
            catch ( SolrServerException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }
        else
        {
            facetedResult.setFacetFields( new ArrayList<FacetField>(  ) );
        }

        facetedResult.setSolrSearchResults( results );

        return facetedResult;
    }

    /**
     * Return the suggestion terms
     * @param term the terms of search
     * @return The spell checker response
     */
    public SpellCheckResponse getSpellChecker( String term )
    {
        SpellCheckResponse spellCheck = null;
        SolrServer solrServer = SolrServerService.getInstance(  ).getSolrServer(  );

        SolrQuery query = new SolrQuery( term );
        //Do not return results (optimization)
        query.setRows( 0 );
        //Activate spellChecker
        query.setParam( "spellcheck", "true" );
        //The request handler used
        query.setQueryType( "spellCheckText" ); //TODO
                                                //The number of suggest returned

        query.setParam( "spellcheck.count", "1" ); // TODO
                                                   //Returns the frequency of the terms

        query.setParam( "spellcheck.extendedResults", "true" ); // TODO
                                                                //Return the best suggestion combinaison with many words

        query.setParam( "spellcheck.collate", "true" ); // TODO

        try
        {
            QueryResponse response = solrServer.query( query );
            spellCheck = response.getSpellCheckResponse(  );
        }
        catch ( SolrServerException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return spellCheck;
    }

    public QueryResponse getJsonpSuggest( String terms, String callback )
    {
        QueryResponse response = null;
        SolrServer solrServer = SolrServerService.getInstance(  ).getSolrServer(  );

        SolrQuery query = new SolrQuery( terms );
        query.setParam( "wt", "json" );
        query.setParam( "json.wrf", callback );
        query.setRows( 10 );
        query.setRequestHandler("/autoComplete" ); //TODO

        try
        {
            response = solrServer.query( query );
        }
        catch ( SolrServerException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return response;
    }

    public String getDocumentHighLighting( String strDocumentId, String terms )
    {
        String strDocumentIdPrefixed = SolrIndexerService.getWebAppName() + SolrConstants.CONSTANT_UNDERSCORE + strDocumentId;
        String xmlContent = null;
        SolrServer solrServer = SolrServerService.getInstance(  ).getSolrServer(  );
        SolrQuery query = new SolrQuery( terms );
        query.setHighlight( true );
        query.setHighlightSimplePre( SOLR_HIGHLIGHT_PRE );
        query.setHighlightSimplePost( SOLR_HIGHLIGHT_POST );
        query.setHighlightFragsize( 0 ); //return all the content, not fragments
        query.setParam( "hl.fl", SolrItem.FIELD_XML_CONTENT ); //return only the field xml_content HighLighting
        query.setFields( SearchItem.FIELD_UID ); //return only the field uid
        query.setRows( 1 );
        query.addFilterQuery( SearchItem.FIELD_UID + ":" + strDocumentIdPrefixed );

        try
        {
            QueryResponse response = solrServer.query( query );

            if ( response.getResults(  ).size(  ) == 1 )
            {
                SolrHighlights highlights = new SolrHighlights( response.getHighlighting(  ) );

                if ( highlights.getHighlights( strDocumentIdPrefixed ).getMap(  ).size(  ) > 0 )
                {
                    xmlContent = highlights.getHighlights( strDocumentIdPrefixed ).getMap(  ).get( SolrItem.FIELD_XML_CONTENT )
                                           .get( 0 );
                }
            }
        }
        catch ( SolrServerException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return xmlContent;
    }

    /**
     * Builds a field filter string.
     * field:(value0 value1 ...valueN)
     * @param strField the field
     * @param values the values
     * @return the filter string
     */
    private String buildFilter( String strField, String[] values )
    {
        StringBuffer sb = new StringBuffer(  );
        sb.append( strField );
        sb.append( ":(" );

        for ( String strValue : values )
        {
            sb.append( strValue );
            sb.append( " " );
        }

        String filterString = sb.substring( 0, sb.length(  ) - 1 );
        filterString += ")";

        return filterString;
    }

    /**
     * Returns the instance
     * @return the instance
     */
    public static SolrSearchEngine getInstance(  )
    {
        if ( _instance == null )
        {
            _instance = new SolrSearchEngine(  );
        }

        return _instance;
    }
}
