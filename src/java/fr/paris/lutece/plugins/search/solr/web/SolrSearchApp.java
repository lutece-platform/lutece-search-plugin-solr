/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.search.solr.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.SpellCheckResponse;

import fr.paris.lutece.plugins.search.solr.business.SolrFacetedResult;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchEngine;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchResult;
import fr.paris.lutece.plugins.search.solr.business.field.SolrFieldManager;
import fr.paris.lutece.plugins.search.solr.util.SolrConstants;
import fr.paris.lutece.plugins.search.solr.util.SolrUtil;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.QueryEvent;
import fr.paris.lutece.portal.service.search.QueryListenersService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This page shows some features of Solr like Highlights or Facets.
 *
 */
public class SolrSearchApp implements XPageApplication
{
    private static final String FULL_URL = "fullUrl";
    private static final String SOLR_FACET_DATE_GAP = "facetDateGap";
	private static final String ALL_SEARCH_QUERY = "*:*";
	////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String TEMPLATE_RESULTS = "skin/search/solr_search_results.html";
    private static final String PROPERTY_SEARCH_PAGE_URL = "solr.pageSearch.baseUrl";
    private static final String PROPERTY_RESULTS_PER_PAGE = "search.nb.docs.per.page";
    private static final String PROPERTY_PATH_LABEL = "portal.search.search_results.pathLabel";
    private static final String PROPERTY_PAGE_TITLE = "portal.search.search_results.pageTitle";
    private static final String PROPERTY_ONLY_FACTES = "solr.onlyFacets";
    private static final String PROPERTY_SOLR_RESPONSE_MAX = "solr.reponse.max";
    private static final int SOLR_RESPONSE_MAX = Integer.parseInt( AppPropertiesService.getProperty( 
                PROPERTY_SOLR_RESPONSE_MAX, "50" ) );
    private static final String MESSAGE_INVALID_SEARCH_TERMS = "portal.search.message.invalidSearchTerms";
    private static final String DEFAULT_RESULTS_PER_PAGE = "10";
    private static final String DEFAULT_PAGE_INDEX = "1";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_NB_ITEMS_PER_PAGE = "items_per_page";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_FACET_QUERY = "fq";

    //private static final String PARAMETER_FACET_LABEL = "facetlabel";
    //private static final String PARAMETER_FACET_NAME = "facetname";
    private static final String PARAMETER_SORT_NAME = "sort_name";
    private static final String PARAMETER_SORT_ORDER = "sort_order";
    private static final String MARK_RESULTS_LIST = "results_list";
    private static final String MARK_QUERY = "query";
    private static final String MARK_FACET_QUERY = "facetquery";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ERROR = "error";
    private static final String MARK_FACETS = "facets";
    private static final String MARK_SOLR_FIELDS = "solr_fields";
    private static final String MARK_FACETS_DATE = "facets_date";
    private static final String MARK_HISTORIQUE = "historique";
    private static final String MARK_SUGGESTION = "suggestion";
    private static final String MARK_SORT_NAME = "sort_name";
    private static final String MARK_SORT_ORDER = "sort_order";
    private static final String MARK_SORT_LIST = "sort_list";
    private static final String MARK_FACET_TREE = "facet_tree";
    private static final String MARK_FACETS_LIST = "facets_list";
    private static final String MARK_ENCODING = "encoding";
    private static final String PROPERTY_ENCODE_URI = "search.encode.uri";
    private static final boolean DEFAULT_ENCODE_URI = false;
    private static Map<String, Object> _lastSearchModel;

    /**
     * Returns search results
     *
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @param plugin The plugin
     * @return The HTML code of the page.
     * @throws SiteMessageException exception
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws SiteMessageException
    {
        XPage page = new XPage(  );
        String strQuery = request.getParameter( PARAMETER_QUERY );
        String[] facetQuery = request.getParameterValues( PARAMETER_FACET_QUERY );
        String sort = request.getParameter( PARAMETER_SORT_NAME );
        String order = request.getParameter( PARAMETER_SORT_ORDER );

        //String facetlabel = request.getParameter( PARAMETER_FACET_LABEL );
        //String facetname = request.getParameter( PARAMETER_FACET_NAME );
        String facetQueryUrl = "";
        SolrFieldManager sfm = new SolrFieldManager(  );

        List<String> lstSingleFacetQueries = new ArrayList<String>();
        if ( facetQuery != null )
        {
            for ( String fq : facetQuery )
            {
                if ( facetQueryUrl.indexOf( fq ) == -1 )
                {
                    facetQueryUrl += ( "&fq=" + fq );
                    sfm.addFacet( fq );
                    lstSingleFacetQueries.add( fq );
                }
            }
        }

        boolean bEncodeUri = Boolean.parseBoolean( AppPropertiesService.getProperty( PROPERTY_ENCODE_URI,
                    Boolean.toString( DEFAULT_ENCODE_URI ) ) );

        String strSearchPageUrl = AppPropertiesService.getProperty( PROPERTY_SEARCH_PAGE_URL );
        String strError = SolrConstants.CONSTANT_EMPTY_STRING;
        Locale locale = request.getLocale(  );

        int nLimit = SOLR_RESPONSE_MAX;
        
        // Check XSS characters
        if ( ( strQuery != null ) && ( StringUtil.containsXssCharacters( strQuery ) ) )
        {
            strError = I18nService.getLocalizedString( MESSAGE_INVALID_SEARCH_TERMS, locale );
        }
        if( StringUtils.isNotBlank( strError ) || StringUtils.isBlank( strQuery ) )
        {
        	strQuery = ALL_SEARCH_QUERY;
        	String strOnlyFacets = AppPropertiesService.getProperty( PROPERTY_ONLY_FACTES );
        	if( StringUtils.isNotBlank( strError ) || ( ( facetQuery == null || facetQuery.length <= 0 ) && StringUtils.isNotBlank( strOnlyFacets ) && SolrConstants.CONSTANT_TRUE.equals( strOnlyFacets ) ) )
        	{
        		//no request and no facet selected : we show the facets but no result
        		nLimit = 0;
        	}
        }

        String strNbItemPerPage = request.getParameter( PARAMETER_NB_ITEMS_PER_PAGE );
        String strDefaultNbItemPerPage = AppPropertiesService.getProperty( PROPERTY_RESULTS_PER_PAGE,
                DEFAULT_RESULTS_PER_PAGE );
        strNbItemPerPage = ( strNbItemPerPage != null ) ? strNbItemPerPage : strDefaultNbItemPerPage;

        int nNbItemsPerPage = Integer.parseInt( strNbItemPerPage );
        String strCurrentPageIndex = request.getParameter( PARAMETER_PAGE_INDEX );
        strCurrentPageIndex = ( strCurrentPageIndex != null ) ? strCurrentPageIndex : DEFAULT_PAGE_INDEX;

        SolrSearchEngine engine = SolrSearchEngine.getInstance(  );
        SolrFacetedResult facetedResult = engine.getFacetedSearchResults( strQuery, facetQuery, sort, order, nLimit );
        List<SolrSearchResult> listResults = facetedResult.getSolrSearchResults(  );

        // The page should not be added to the cache
        // Notify results infos to QueryEventListeners 
        notifyQueryListeners( strQuery, listResults.size(  ), request );

        UrlItem url = new UrlItem( strSearchPageUrl );
        String strQueryForPaginator = strQuery;

        if ( bEncodeUri )
        {
            strQueryForPaginator = SolrUtil.encodeUrl( request, strQuery );
        }

        url.addParameter( PARAMETER_QUERY, strQueryForPaginator );
        url.addParameter( PARAMETER_NB_ITEMS_PER_PAGE, nNbItemsPerPage );
        for( String strFacetName : lstSingleFacetQueries )
        {
        	url.addParameter( PARAMETER_FACET_QUERY, SolrUtil.encodeUrl( strFacetName ) );
        }

        Paginator<SolrSearchResult> paginator = new Paginator<SolrSearchResult>( listResults, nNbItemsPerPage, url.getUrl(  ), PARAMETER_PAGE_INDEX,
                strCurrentPageIndex );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_RESULTS_LIST, paginator.getPageItems(  ) );
        // put the query only if it's not *.*
        model.put( MARK_QUERY, ALL_SEARCH_QUERY.equals( strQuery ) ? SolrConstants.CONSTANT_EMPTY_STRING : strQuery );
        model.put( MARK_FACET_QUERY, facetQueryUrl );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, strNbItemPerPage );
        model.put( MARK_ERROR, strError );
        model.put( MARK_FACETS, facetedResult.getFacetFields(  ) );
        model.put( MARK_SOLR_FIELDS, SolrFieldManager.getFacetList(  ) );
        model.put( MARK_FACETS_DATE, facetedResult.getFacetDateList(  ) );
        model.put( MARK_HISTORIQUE, sfm.getCurrentFacet(  ) );
        model.put( MARK_FACETS_LIST, lstSingleFacetQueries );
        if( strQuery != null && strQuery.compareToIgnoreCase( ALL_SEARCH_QUERY )!=0 )
        {
        	SpellCheckResponse checkResponse = engine.getSpellChecker( strQuery );
        	if( checkResponse != null )
        	{
        		model.put( MARK_SUGGESTION, checkResponse.getCollatedResult(  ) );
        	}
        }
        model.put( MARK_SORT_NAME, sort );
        model.put( MARK_SORT_ORDER, order );
        model.put( MARK_SORT_LIST, SolrFieldManager.getSortList(  ) );
        model.put( MARK_FACET_TREE, facetedResult.getFacetIntersection(  ) );
        model.put( MARK_ENCODING, SolrUtil.getEncoding(  ) );
        String strRequestUrl = request.getRequestURL(  ).toString(  );
        model.put( FULL_URL, strRequestUrl );
        model.put( SOLR_FACET_DATE_GAP, SolrSearchEngine.SOLR_FACET_DATE_GAP );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RESULTS, locale, model );

        _lastSearchModel = model;
        
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_PATH_LABEL, locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE, locale ) );
        page.setContent( template.getHtml(  ) );

        return page;
    }

    /**
     * Notify all query Listeners
     * @param strQuery The query
     * @param nResultsCount The results count
     * @param request The request
     */
    private void notifyQueryListeners( String strQuery, int nResultsCount, HttpServletRequest request )
    {
        QueryEvent event = new QueryEvent(  );
        event.setQuery( strQuery );
        event.setResultsCount( nResultsCount );
        event.setRequest( request );
        QueryListenersService.getInstance(  ).notifyListeners( event );
    }
    
    /**
     * Return the model used during the last search
     * @return  the model used during the last search
     */
    public static Map<String, Object> getLastSearchModel()
    {
    	return _lastSearchModel;
    }
}
