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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import fr.paris.lutece.plugins.search.solr.business.facetintersection.FacetIntersection;
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

/**
 *
 * SolrSearchEngine
 *
 */
public class SolrSearchEngine implements SearchEngine
{
    private static final String PROPERTY_SOLR_AUTOCOMPLETE_HANDLER = "solr.autocomplete.handler";
    private static final String PROPERTY_SOLR_SPELLCHECK_HANDLER = "solr.spellcheck.handler";
    private static final String PROPERTY_SOLR_HIGHLIGHT_PRE = "solr.highlight.pre";
    private static final String PROPERTY_SOLR_HIGHLIGHT_POST = "solr.highlight.post";
    private static final String PROPERTY_SOLR_HIGHLIGHT_SNIPPETS = "solr.highlight.snippets";
    private static final String PROPERTY_SOLR_HIGHLIGHT_FRAGSIZE = "solr.highlight.fragsize";
    private static final String PROPERTY_SOLR_HIGHLIGHT_SUMMARY_FRAGSIZE = "solr.highlight.summary.fragsize";
    private static final String PROPERTY_SOLR_FACET_DATE_START = "solr.facet.date.start";
    private static final String PROPERTY_FIELD_OR = "solr.field.or";
    private static final String PROPERTY_FIELD_SWITCH = "solr.field.switch";
    private static final String PROPERTY_FIELD_AND = "solr.field.and";
    private static final boolean SOLR_FACET_COUNT_EXCLUDE = AppPropertiesService.getPropertyBoolean( "solr.facet.count.exclude", false );

    // To allow the phrase to boost even with 2 words in between
    // For example "Lutece is a Framework" would match for the search "Lutece Framework"
    private static final String PROPERTY_SOLR_SEARCH_PS = "solr.search.ps";
    private static final String DEFAULT_SOLR_SEARCH_PS = "3";

    // According to Apache Solr Enterprise Search Server - Third Edition
    // This should be a good default value
    private static final String PROPERTY_SOLR_SEARCH_TIE = "solr.search.tie";
    private static final String DEFAULT_SOLR_SEARCH_TIE = "0.1";

    // see https://issues.apache.org/jira/browse/SOLR-3085
    // Like one of the usecases described in the bug report,
    // a stopword is very unlikely to match a tag (there is no tag named "le" or "la"). So it makes sense
    // to autorelax mm by default.
    private static final String PROPERTY_SOLR_SEARCH_AUTORELAXMM = "solr.search.autorelaxmm";
    private static final String DEFAULT_SOLR_SEARCH_AUTORELAXMM = "true";

    private static final String PROPERTY_SOLR_SEARCH_BOOSTRECENT = "solr.search.boostrecent";
    private static final String DEFAULT_SOLR_SEARCH_BOOSTRECENT = "3.16e-12"; // Pretty soft: After 10 years, the score is divided by 2.

    private static final String SOLR_AUTOCOMPLETE_HANDLER = AppPropertiesService.getProperty( PROPERTY_SOLR_AUTOCOMPLETE_HANDLER );
    private static final String SOLR_SPELLCHECK_HANDLER = AppPropertiesService.getProperty( PROPERTY_SOLR_SPELLCHECK_HANDLER );
    private static final String SOLR_HIGHLIGHT_PRE = AppPropertiesService.getProperty( PROPERTY_SOLR_HIGHLIGHT_PRE );
    private static final String SOLR_HIGHLIGHT_POST = AppPropertiesService.getProperty( PROPERTY_SOLR_HIGHLIGHT_POST );
    private static final int SOLR_HIGHLIGHT_SNIPPETS = AppPropertiesService.getPropertyInt( PROPERTY_SOLR_HIGHLIGHT_SNIPPETS, 5 );
    private static final int SOLR_HIGHLIGHT_FRAGSIZE = AppPropertiesService.getPropertyInt( PROPERTY_SOLR_HIGHLIGHT_FRAGSIZE, 100 );
    private static final int SOLR_HIGHLIGHT_SUMMARY_FRAGSIZE = AppPropertiesService.getPropertyInt( PROPERTY_SOLR_HIGHLIGHT_SUMMARY_FRAGSIZE, 0 );
    private static final String SOLR_FACET_DATE_START = AppPropertiesService.getProperty( PROPERTY_SOLR_FACET_DATE_START );

    private static final String SOLR_SPELLFIELD_OR = AppPropertiesService.getProperty( PROPERTY_FIELD_OR );
    private static final String SOLR_SPELLFIELD_SWITCH = AppPropertiesService.getProperty( PROPERTY_FIELD_SWITCH );
    private static final String SOLR_SPELLFIELD_AND = AppPropertiesService.getProperty( PROPERTY_FIELD_AND );

    public static final String SOLR_FACET_DATE_GAP = AppPropertiesService.getProperty( "solr.facet.date.gap", "+1YEAR" );
    public static final String SOLR_FACET_DATE_END = AppPropertiesService.getProperty( "solr.facet.date.end", "NOW" );
    public static final int SOLR_FACET_LIMIT = AppPropertiesService.getPropertyInt( "solr.facet.limit", 100 );
    private static SolrSearchEngine _instance;
    private static final String DEF_TYPE = "edismax";

    /**
     * Return search results
     * 
     * @param strQuery
     *            The search query
     * @param request
     *            The HTTP request
     * @return Results as a collection of SearchResult
     */
    public List<SearchResult> getSearchResults( String strQuery, HttpServletRequest request )
    {
        List<SearchResult> results = new ArrayList<>( );

        SolrClient solrServer = SolrServerService.getInstance( ).getSolrServer( );

        if ( ( solrServer != null ) && StringUtils.isNotEmpty( strQuery ) )
        {
            SolrQuery query = new SolrQuery( );
            query.setQuery( strQuery );

            String [ ] userRoles;
            String [ ] roles;

            if ( SecurityService.isAuthenticationEnable( ) )
            {
                // authentification on, check roles
                LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

                if ( user != null )
                {
                    userRoles = SecurityService.getInstance( ).getRolesByUser( user );
                }
                else
                {
                    userRoles = new String [ 0];
                }

                roles = new String [ userRoles.length + 1];
                System.arraycopy( userRoles, 0, roles, 0, userRoles.length );
                roles [roles.length - 1] = Page.ROLE_NONE;

                String filterRole = buildFilter( SearchItem.FIELD_ROLE, roles );
                // portlets roles
                query = query.addFilterQuery( filterRole );
            }

            try
            {
                QueryResponse response = solrServer.query( query );
                SolrDocumentList documentList = response.getResults( );
                results = SolrUtil.transformSolrDocumentList( documentList );
            }
            catch( SolrServerException | IOException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
        }

        return results;
    }

    /**
     * Return the result with facets. Does NOT support authentification yet.
     * 
     * @param strQuery
     *            the query
     * @param facetQueries
     *            The selected facets
     * @param sortName
     *            The facet name to sort by
     * @param sortOrder
     *            "asc" or "desc"
     * @param nLimit
     *            Maximal number of results.
     * @return the result with facets
     */
    public SolrFacetedResult getFacetedSearchResults( String strQuery, String [ ] facetQueries, String sortName, String sortOrder, int nLimit,
            int nCurrentPageIndex, int nItemsPerPage, Boolean bSpellCheck )
    {
        SolrFacetedResult facetedResult = new SolrFacetedResult( );

        SolrClient solrServer = SolrServerService.getInstance( ).getSolrServer( );
        List<SolrSearchResult> results = new ArrayList<>( );
        Map<Field, List<String>> myValuesList = new HashMap<>( );

        if ( solrServer == null )
        {
            facetedResult.setFacetFields( new ArrayList<>( ) );
            facetedResult.setSolrSearchResults( results );
            return facetedResult;
        }

        SolrQuery query = new SolrQuery( strQuery );
        query.setHighlight( true );
        query.setHighlightSimplePre( SOLR_HIGHLIGHT_PRE );
        query.setHighlightSimplePost( SOLR_HIGHLIGHT_POST );
        query.setHighlightSnippets( SOLR_HIGHLIGHT_SNIPPETS );
        query.setHighlightFragsize( SOLR_HIGHLIGHT_FRAGSIZE );
        query.setParam( "f.summary.hl.fragsize", Integer.toString( SOLR_HIGHLIGHT_SUMMARY_FRAGSIZE ) );
        query.setFacet( true );
        query.setFacetLimit( SOLR_FACET_LIMIT );

        for ( Field field : SolrFieldManager.getFacetList( ).values( ) )
        {
            // Add facet Field
            if ( field.getEnableFacet( ) )
            {
                StringBuilder exclusionTag = new StringBuilder( );
                // If exclusion enabled, ignore the fq for the current facet count results.
                if ( SOLR_FACET_COUNT_EXCLUDE )
                {
                    exclusionTag.append( "{!ex=t" ).append( field.getName( ) ).append( "}" );
                }

                if ( isFieldDate( field.getName( ) ) )
                {
                    query.setParam( "facet.range", exclusionTag + field.getName( ) );
                    query.setParam( "facet.range.start", SOLR_FACET_DATE_START );
                    query.setParam( "facet.range.gap", SOLR_FACET_DATE_GAP );
                    query.setParam( "facet.range.end", SOLR_FACET_DATE_END );
                    query.setParam( "facet.range.mincount", "0" );
                }
                else
                {
                    query.addFacetField( exclusionTag + field.getSolrName( ) );
                    query.setParam( "f." + field.getSolrName( ) + ".facet.mincount", String.valueOf( field.getFacetMincount( ) ) );
                }
                myValuesList.put( field, new ArrayList<String>( ) );
            }
        }

        // Facet intersection
        List<String> treeParam = new ArrayList<>( );

        for ( FacetIntersection intersect : SolrFieldManager.getIntersectionlist( ) )
        {
            treeParam.add( intersect.getField1( ).getSolrName( ) + "," + intersect.getField2( ).getSolrName( ) );
        }

        query.setParam( "facet.tree", (String [ ]) treeParam.toArray( new String [ 0] ) );
        query.setParam( "spellcheck", bSpellCheck );

        // sort order
        if ( StringUtils.isNotEmpty( sortName ) )
        {
            if ( sortOrder.equals( "asc" ) )
            {
                query.setSort( sortName, ORDER.asc );
            }
            else
            {
                query.setSort( sortName, ORDER.desc );
            }
        }
        else
        {
            for ( Field field : SolrFieldManager.getSortList( ) )
            {
                if ( field.getDefaultSort( ) )
                {
                    query.setSort( field.getName( ), ORDER.desc );
                }
            }
        }

        // Treat HttpRequest
        // FacetQuery
        if ( facetQueries != null )
        {
            for ( String strFacetQuery : facetQueries )
            {
                String [ ] myValues = strFacetQuery.split( ":", 2 );
                if ( myValues != null && myValues.length == 2 )
                {
                    myValuesList = getFieldArrange( myValues, myValuesList );
                }
            }

            for ( Entry<Field, List<String>> entry : myValuesList.entrySet( ) )
            {
                Field tmpFieldValue = entry.getKey( );
                List<String> strValues = entry.getValue( );
                String strFacetString = "";
                if ( CollectionUtils.isNotEmpty( strValues ) )
                {
                    strFacetString = extractQuery( strValues, tmpFieldValue.getOperator( ) );
                    if ( isFieldDate( tmpFieldValue.getName( ) ) )
                    {
                        strFacetString = strFacetString.replaceAll( "\"", "" );
                    }

                    StringBuilder facetFilter = new StringBuilder( tmpFieldValue.getName( ) );
                    // If exclusion enabled, add the tag so that it can be excluded.
                    if ( SOLR_FACET_COUNT_EXCLUDE )
                    {
                        StringBuilder tag = new StringBuilder( "{!tag=t" ).append( tmpFieldValue.getName( ) ).append( "}" );
                        facetFilter.insert( 0, tag );
                    }
                    facetFilter.append( ":" );
                    facetFilter.append( strFacetString );
                    query.addFilterQuery( facetFilter.toString( ) );
                }
            }
        }

        try
        {

            // count query
            query.setRows( 0 );
            if ( !strQuery.equals( "*:*" ) )
            {
                query.setParam( "defType", DEF_TYPE );
                String strWeightValue = generateQueryWeightValue( );
                query.setParam( "qf", strWeightValue );
                String strPhraseSlop = AppPropertiesService.getProperty( PROPERTY_SOLR_SEARCH_PS, DEFAULT_SOLR_SEARCH_PS );

                if ( StringUtils.isNotBlank( strPhraseSlop ) )
                {
                    query.setParam( "pf", strWeightValue );
                    query.setParam( "pf2", strWeightValue );
                    query.setParam( "pf3", strWeightValue );

                    query.setParam( "ps", strPhraseSlop );
                    query.setParam( "qs", strPhraseSlop ); // Slop when the user explicitly uses quotes
                }

                String strTie = AppPropertiesService.getProperty( PROPERTY_SOLR_SEARCH_TIE, DEFAULT_SOLR_SEARCH_TIE );
                if ( StringUtils.isNotBlank( strTie ) )
                {
                    query.setParam( "tie", strTie );
                }

                String strAutoRelaxmm = AppPropertiesService.getProperty( PROPERTY_SOLR_SEARCH_AUTORELAXMM, DEFAULT_SOLR_SEARCH_AUTORELAXMM );
                if ( StringUtils.isNotBlank( strAutoRelaxmm ) )
                {
                    query.setParam( "mm.autoRelax", strAutoRelaxmm );
                }

                String strBoostRecent = AppPropertiesService.getProperty( PROPERTY_SOLR_SEARCH_BOOSTRECENT, DEFAULT_SOLR_SEARCH_BOOSTRECENT );
                if ( StringUtils.isNotBlank( strBoostRecent ) )
                {
                    query.setParam( "boost", "recip(ms(NOW/HOUR,date)," + strBoostRecent + ",1,1)" );
                }
            }

            QueryResponse response = solrServer.query( query );

            int nResults = (int) response.getResults( ).getNumFound( );
            facetedResult.setCount( nResults > nLimit ? nLimit : nResults );

            query.setStart( ( nCurrentPageIndex - 1 ) * nItemsPerPage );
            query.setRows( nItemsPerPage > nLimit ? nLimit : nItemsPerPage );

            response = solrServer.query( query );

            // HighLight
            Map<String, Map<String, List<String>>> highlightsMap = response.getHighlighting( );
            SolrHighlights highlights = null;

            if ( highlightsMap != null )
            {
                highlights = new SolrHighlights( highlightsMap );
            }

            // resultList
            List<SolrItem> itemList = response.getBeans( SolrItem.class );
            results = SolrUtil.transformSolrItemsToSolrSearchResults( itemList, highlights );

            // set the spellcheckresult
            facetedResult.setSolrSpellCheckResponse( response.getSpellCheckResponse( ) );

            // Range facet (dates)
            if ( ( response.getFacetRanges( ) != null ) && !response.getFacetRanges( ).isEmpty( ) )
            {
                facetedResult.setFacetDateList( response.getFacetRanges( ) );
            }

            // FacetField
            facetedResult.setFacetFields( response.getFacetFields( ) );

            // Facet intersection (facet tree)
            NamedList<Object> resp = (NamedList<Object>) response.getResponse( ).get( "facet_counts" );

            if ( resp != null )
            {
                NamedList<NamedList<NamedList<Integer>>> trees = (NamedList<NamedList<NamedList<Integer>>>) resp.get( "trees" );
                Map<String, List<FacetField>> treesResult = new HashMap<>( );

                if ( trees != null )
                {
                    for ( Entry<String, NamedList<NamedList<Integer>>> selectedFacet : trees )
                    {
                        // Selected Facet (ex : type,categorie )
                        List<FacetField> facetFields = new ArrayList<>( selectedFacet.getValue( ).size( ) );

                        for ( Entry<String, NamedList<Integer>> facetField : selectedFacet.getValue( ) )
                        {
                            FacetField ff = new FacetField( facetField.getKey( ) );

                            for ( Entry<String, Integer> value : facetField.getValue( ) )
                            {
                                // Second Level
                                ff.add( value.getKey( ), value.getValue( ) );
                            }

                            facetFields.add( ff );
                        }

                        treesResult.put( selectedFacet.getKey( ), facetFields );
                    }
                }

                facetedResult.setFacetIntersection( treesResult );
            }
        }
        catch( SolrServerException | IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        facetedResult.setSolrSearchResults( results );

        return facetedResult;
    }

    /**
     * @param strValues
     * @param strStart
     * @param strEnd
     * @return
     */
    private String extractQuery( List<String> strValues, String strOperator )
    {
        StringBuilder sbFacetString = new StringBuilder( );
        String strStart = strValues.size( ) > 1 ? "(" : "";
        String strEnd = strValues.size( ) > 1 ? ")" : "";

        List<String> notBlankValues = strValues.stream( ).filter( StringUtils::isNotBlank ).collect( Collectors.toList( ) );

        for ( String strTmpSearch : notBlankValues )
        {
            strTmpSearch = "\"" + strTmpSearch.replaceAll( "\"", Matcher.quoteReplacement( "\\\"" ) ) + "\"";
            if ( SOLR_SPELLFIELD_OR.equalsIgnoreCase( strOperator ) || SOLR_SPELLFIELD_AND.equalsIgnoreCase( strOperator ) )
            {
                if ( StringUtils.isBlank( sbFacetString.toString( ) ) )
                {
                    sbFacetString.append( strTmpSearch.trim( ) );
                }
                else
                {
                    sbFacetString.append( " " ).append( strOperator ).append( " " ).append( strTmpSearch.trim( ) );
                }
            }
            else
                if ( SOLR_SPELLFIELD_SWITCH.equalsIgnoreCase( strOperator ) )
                {
                    sbFacetString = new StringBuilder( strTmpSearch.trim( ) );
                }
        }
        return strStart.concat( sbFacetString.toString( ) ).concat( strEnd );
    }

    /**
     * Get a matrice from all the fields with their values
     * 
     * @param myValues
     * @return
     */
    private Map<Field, List<String>> getFieldArrange( String [ ] myValues, Map<Field, List<String>> myTab )
    {
        Map<Field, List<String>> lstReturn = myTab;
        Field tmpField = getField( lstReturn, myValues [0] );
        if ( tmpField != null )
        {
            boolean bAddField = true;
            List<String> getValuesFromFrield = lstReturn.get( tmpField );
            for ( String strField : getValuesFromFrield )
            {
                if ( strField.equalsIgnoreCase( myValues [1] ) )
                {
                    bAddField = false;
                }
            }
            if ( bAddField )
            {
                getValuesFromFrield.add( myValues [1] );
                lstReturn.put( tmpField, getValuesFromFrield );
            }
        }
        return lstReturn;
    }

    /**
     * @return
     */
    private String generateQueryWeightValue( )
    {
        List<Field> fieldList = SolrFieldManager.getFieldList( );
        StringBuilder strQueryWeight = new StringBuilder( );
        for ( Field field : fieldList )
        {
            if ( field.getWeight( ) > 0 )
            {
                strQueryWeight.append( field.getSolrName( ) ).append( "^" ).append( field.getWeight( ) ).append( " " );
            }
        }
        return strQueryWeight.toString( );
    }

    /**
     * Get the field
     * 
     * @param strName
     */
    private Field getField( Map<Field, List<String>> values, String strName )
    {
        Field fieldReturn = null;
        for ( Field tmp : values.keySet( ) )
        {
            if ( tmp.getName( ).equalsIgnoreCase( strName ) )
            {
                fieldReturn = tmp;
            }
        }
        return fieldReturn;
    }

    /**
     * Return the result geojseon and uid
     * 
     * @param strQuery
     *            the query
     * @param facetQueries
     *            The selected facets
     * @param nLimit
     *            Maximal number of results.
     * @return the results geojson and uid
     */
    public List<SolrSearchResult> getGeolocSearchResults( String strQuery, String [ ] facetQueries, int nLimit )
    {
        SolrClient solrServer = SolrServerService.getInstance( ).getSolrServer( );

        Map<Field, List<String>> myValuesList = new HashMap<>( );
        if ( solrServer == null )
        {
            return new ArrayList<>( );
        }

        String strFields = "*" + SolrItem.DYNAMIC_GEOJSON_FIELD_SUFFIX + "," + SearchItem.FIELD_UID;
        SolrQuery query = new SolrQuery( strQuery );
        query.setParam( "fl", strFields );

        for ( Field field : SolrFieldManager.getFacetList( ).values( ) )
        {
            // Add facet Field
            if ( field.getEnableFacet( ) )
            {
                myValuesList.put( field, new ArrayList<String>( ) );
            }
        }

        // Treat HttpRequest
        // FacetQuery
        if ( facetQueries != null )
        {
            for ( String strFacetQuery : facetQueries )
            {
                String [ ] myValues = strFacetQuery.split( ":", 2 );
                if ( myValues != null && myValues.length == 2 )
                {
                    myValuesList = getFieldArrange( myValues, myValuesList );
                }
            }

            for ( Entry<Field, List<String>> entry : myValuesList.entrySet( ) )
            {
                Field tmpFieldValue = entry.getKey( );
                List<String> strValues = entry.getValue( );
                String strFacetString = "";
                if ( CollectionUtils.isNotEmpty( strValues ) )
                {
                    strFacetString = extractQuery( strValues, tmpFieldValue.getOperator( ) );
                    if ( isFieldDate( tmpFieldValue.getName( ) ) )
                    {
                        strFacetString = strFacetString.replaceAll( "\"", "" );
                    }
                    query.addFilterQuery( tmpFieldValue.getName( ) + ":" + strFacetString );
                }
            }
        }

        if ( !strQuery.equals( "*:*" ) )
        {
            query.setParam( "defType", DEF_TYPE );
            query.setParam( "mm.autoRelax", true );
        }

        query.setStart( 0 );
        query.setRows( nLimit );
        QueryResponse response;
        try
        {
            response = solrServer.query( query );
        }
        catch( SolrServerException | IOException e )
        {
            AppLogService.error( "Solr getGeolocSearchResults error: " + e.getMessage( ), e );
            return new ArrayList<>( );
        }
        // resultList
        List<SolrItem> itemList = response.getBeans( SolrItem.class );
        return SolrUtil.transformSolrItemsToSolrSearchResults( itemList, null );
    }

    /**
     * Return the suggestion terms
     * 
     * @param term
     *            the terms of search
     * @return The spell checker response
     */
    public SpellCheckResponse getSpellChecker( String term )
    {
        SpellCheckResponse spellCheck = null;
        SolrClient solrServer = SolrServerService.getInstance( ).getSolrServer( );

        SolrQuery query = new SolrQuery( term );
        // Do not return results (optimization)
        query.setRows( 0 );
        // Activate spellChecker
        query.setParam( "spellcheck", "true" );
        // The request handler used
        query.setRequestHandler( "/" + SOLR_SPELLCHECK_HANDLER );
        // The number of suggest returned

        query.setParam( "spellcheck.count", "1" ); // TODO
                                                   // Returns the frequency of the terms

        query.setParam( "spellcheck.extendedResults", "true" ); // TODO
                                                                // Return the best suggestion combinaison with many words

        query.setParam( "spellcheck.collate", "true" ); // TODO

        try
        {
            QueryResponse response = solrServer.query( query );
            spellCheck = response.getSpellCheckResponse( );
        }
        catch( SolrServerException | IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return spellCheck;
    }

    public QueryResponse getJsonpSuggest( String terms, String callback )
    {
        QueryResponse response = null;
        SolrClient solrServer = SolrServerService.getInstance( ).getSolrServer( );

        SolrQuery query = new SolrQuery( terms );
        query.setParam( "wt", "json" );
        query.setParam( "json.wrf", callback );
        query.setRows( 10 );
        query.setRequestHandler( "/" + SOLR_AUTOCOMPLETE_HANDLER );

        try
        {
            response = solrServer.query( query );
        }
        catch( SolrServerException | IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return response;
    }

    public String getDocumentHighLighting( String strDocumentId, String terms )
    {
        String strDocumentIdPrefixed = SolrIndexerService.getWebAppName( ) + SolrConstants.CONSTANT_UNDERSCORE + strDocumentId;
        String xmlContent = null;
        SolrClient solrServer = SolrServerService.getInstance( ).getSolrServer( );
        SolrQuery query = new SolrQuery( terms );
        query.setHighlight( true );
        query.setHighlightSimplePre( SOLR_HIGHLIGHT_PRE );
        query.setHighlightSimplePost( SOLR_HIGHLIGHT_POST );
        query.setHighlightFragsize( 0 ); // return all the content, not fragments
        query.setParam( "hl.fl", SolrItem.FIELD_XML_CONTENT ); // return only the field xml_content HighLighting
        query.setFields( SearchItem.FIELD_UID ); // return only the field uid
        query.setRows( 1 );
        query.addFilterQuery( SearchItem.FIELD_UID + ":" + strDocumentIdPrefixed );

        try
        {
            QueryResponse response = solrServer.query( query );

            if ( response.getResults( ).size( ) == 1 )
            {
                SolrHighlights highlights = new SolrHighlights( response.getHighlighting( ) );

                if ( highlights.getHighlights( strDocumentIdPrefixed ).getMap( ).size( ) > 0 )
                {
                    xmlContent = highlights.getHighlights( strDocumentIdPrefixed ).getMap( ).get( SolrItem.FIELD_XML_CONTENT ).get( 0 );
                }
            }
        }
        catch( SolrServerException | IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return xmlContent;
    }

    /**
     * Builds a field filter string. field:(value0 value1 ...valueN)
     * 
     * @param strField
     *            the field
     * @param values
     *            the values
     * @return the filter string
     */
    private String buildFilter( String strField, String [ ] values )
    {
        StringBuilder sb = new StringBuilder( );
        sb.append( strField );
        sb.append( ":(" );

        for ( String strValue : values )
        {
            sb.append( strValue );
            sb.append( " " );
        }

        String filterString = sb.substring( 0, sb.length( ) - 1 );
        filterString += ")";

        return filterString;
    }

    /**
     * Returns the instance
     * 
     * @return the instance
     */
    public static SolrSearchEngine getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new SolrSearchEngine( );
        }

        return _instance;
    }

    private boolean isFieldDate( String fieldName )
    {
        return fieldName.equalsIgnoreCase( "date" ) || fieldName.toLowerCase( ).endsWith( "_date" );
    }
}
