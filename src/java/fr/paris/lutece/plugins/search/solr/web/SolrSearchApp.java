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
package fr.paris.lutece.plugins.search.solr.web;

import fr.paris.lutece.plugins.leaflet.business.GeolocItem;
import fr.paris.lutece.plugins.leaflet.service.IconService;
import fr.paris.lutece.plugins.search.solr.business.SolrFacetedResult;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchAppConf;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchEngine;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchResult;
import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.plugins.search.solr.business.field.SolrFieldManager;
import fr.paris.lutece.plugins.search.solr.indexer.SolrItem;
import fr.paris.lutece.plugins.search.solr.service.ISolrSearchAppAddOn;
import fr.paris.lutece.plugins.search.solr.service.SolrSearchAppConfService;
import fr.paris.lutece.plugins.search.solr.util.SolrConstants;
import fr.paris.lutece.plugins.search.solr.util.SolrUtil;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.QueryEvent;
import fr.paris.lutece.portal.service.search.QueryListenersService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.DelegatePaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.IPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.SpellCheckResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

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
    private static final String PROPERTY_SEARCH_PAGE_URL = "solr.pageSearch.baseUrl";
    private static final String PROPERTY_RESULTS_PER_PAGE = "search.nb.docs.per.page";
    private static final String PROPERTY_PATH_LABEL = "portal.search.search_results.pathLabel";
    private static final String PROPERTY_PAGE_TITLE = "portal.search.search_results.pageTitle";
    private static final String PROPERTY_ONLY_FACTES = "solr.onlyFacets";
    private static final String PROPERTY_SOLR_RESPONSE_MAX = "solr.reponse.max";
    private static final int SOLR_RESPONSE_MAX = Integer.parseInt(AppPropertiesService.getProperty(
            PROPERTY_SOLR_RESPONSE_MAX, "50"));
    private static final String MESSAGE_INVALID_SEARCH_TERMS = "portal.search.message.invalidSearchTerms";
    private static final int DEFAULT_RESULTS_PER_PAGE = 10;
    private static final String DEFAULT_PAGE_INDEX = "1";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_NB_ITEMS_PER_PAGE = "items_per_page";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_CONF = "conf";
    private static final String PARAMETER_FACET_QUERY = "fq";
    private static final String PARAMETER_PREVIOUS_SEARCH = "previous_search";

    private static final String PARAMETER_FACET_LABEL = "facetlabel";
    private static final String PARAMETER_FACET_NAME = "facetname";
    private static final String PARAMETER_SORT_NAME = "sort_name";
    private static final String PARAMETER_SORT_ORDER = "sort_order";
    private static final String PARAMETER_GROUP = "group";
    private static final String PARAMETER_GROUP_MAIN = "group.main";
    private static final String PARAMETER_GROUP_FIELD = "group.field";
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
    private static final String MARK_CONF_QUERY = "conf_user_query";
    private static final String MARK_CONF = "conf";
    private static final String MARK_POINTS = "points";
    private static final String MARK_POINTS_GEOJSON = "geojson";
    private static final String MARK_POINTS_ID = "id";
    private static final String MARK_POINTS_FIELDCODE = "code";
    private static final String MARK_POINTS_TYPE = "type";
    private static final String PROPERTY_ENCODE_URI = "search.encode.uri";
    private static final boolean DEFAULT_ENCODE_URI = false;
    private static final boolean SOLR_SPELLCHECK = AppPropertiesService.getPropertyBoolean("solr.spellchecker", false);

    /**
     * Returns search results
     *
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @param plugin The plugin
     * @return The HTML code of the page.
     * @throws SiteMessageException exception
     */
    @Override
    public XPage getPage(HttpServletRequest request, int nMode, Plugin plugin)
            throws SiteMessageException
    {
        XPage page = new XPage();

        String strConfCode = request.getParameter( PARAMETER_CONF );
        SolrSearchAppConf conf = SolrSearchAppConfService.loadConfiguration( strConfCode );
        if ( conf == null )
        {
            //Use default conf if the requested one doesn't exist
            conf = SolrSearchAppConfService.loadConfiguration( null );
        }

        Map<String, Object> model = getSearchResultModel(request, conf);
        for ( String beanName: conf.getAddonBeanNames() ) {
            ISolrSearchAppAddOn solrSearchAppAddon = SpringContextService.getBean( beanName );
            solrSearchAppAddon.buildPageAddOn( model, request );
        }

        HtmlTemplate template = AppTemplateService.getTemplate(conf.getTemplate(), request.getLocale(), model);

        page.setPathLabel(I18nService.getLocalizedString(PROPERTY_PATH_LABEL, request.getLocale()));
        page.setTitle(I18nService.getLocalizedString(PROPERTY_PAGE_TITLE, request.getLocale()));
        page.setContent(template.getHtml());

        return page;
    }
    /**
     * @param facetQuery
     * @return
     */
    private static String getFacetNameFromIHM( String facetQuery )
    {
    	String strFacet = null;
    	String myValues[] = facetQuery.split(":",2);
        if (myValues != null && myValues.length == 2)
        	strFacet = myValues[0];
        return strFacet;
    }
    
    /**
     * @param facetQuery
     * @return
     */
    private static String getFacetValueFromIHM( String facetQuery )
    {
    	String strFacet = null;
    	String myValues[] = facetQuery.split(":",2);
        if (myValues != null && myValues.length == 2)
        	strFacet = myValues[1];
        return strFacet;
    }
    /**
     * Performs a search and fills the model (useful when a page needs to remind
     * search parameters/results) with the default conf
     *
     * @param request the request
     * @return the model
     * @throws SiteMessageException if an error occurs
     */
    public static Map<String, Object> getSearchResultModel(HttpServletRequest request)
            throws SiteMessageException
    {
        return getSearchResultModel(request, null);
    }

    /**
     * Performs a search and fills the model (useful when a page needs to remind
     * search parameters/results)
     *
     * @param request the request
     * @param conf the configuration
     * @return the model
     * @throws SiteMessageException if an error occurs
     */
    public static Map<String, Object> getSearchResultModel(HttpServletRequest request, SolrSearchAppConf conf)
            throws SiteMessageException
    {
        String strQuery = request.getParameter(PARAMETER_QUERY);
        String[] facetQuery = request.getParameterValues(PARAMETER_FACET_QUERY);
        String sort = request.getParameter(PARAMETER_SORT_NAME);
        String order = request.getParameter(PARAMETER_SORT_ORDER);
        String strCurrentPageIndex = request.getParameter(PARAMETER_PAGE_INDEX);

        String fname = StringUtils.isBlank(request.getParameter(PARAMETER_FACET_NAME)) ? null : request.getParameter(PARAMETER_FACET_LABEL).trim();
        String flabel = StringUtils.isBlank(request.getParameter(PARAMETER_FACET_LABEL)) ? null : request.getParameter(PARAMETER_FACET_LABEL).trim();
        String strConfCode = request.getParameter( PARAMETER_CONF );

        Boolean group = StringUtils.isBlank(request.getParameter(PARAMETER_GROUP)) ? null : BooleanUtils.toBooleanObject(request.getParameter(PARAMETER_GROUP));
        String groupField = StringUtils.isBlank(request.getParameter(PARAMETER_GROUP_FIELD)) ? null : request.getParameter(PARAMETER_GROUP_FIELD).trim();


        Locale locale = request.getLocale();

        if ( conf == null )
        {
            //Use default conf if not provided
            conf = SolrSearchAppConfService.loadConfiguration( null );
        }

        StringBuilder sbFacetQueryUrl = new StringBuilder();
        SolrFieldManager sfm = new SolrFieldManager();

        List<String> lstSingleFacetQueries = new ArrayList<String>();
        Hashtable<String,Boolean> switchType = getSwitched ();
        ArrayList<String> facetQueryTmp = new ArrayList<String>();
         if (facetQuery != null)
        {
        	 for (String fq : facetQuery)
        	 {
	        	 if (sbFacetQueryUrl.indexOf(fq) == -1)
	             {	
	        		 String strFqNameIHM = getFacetNameFromIHM ( fq );
	                 String strFqValueIHM = getFacetValueFromIHM ( fq );
	                 if ( fname == null || !switchType.containsKey( fname ) ||
	                	   (strFqNameIHM != null && strFqValueIHM != null && 
	                	    strFqValueIHM.equalsIgnoreCase(flabel) && 
	                	    strFqNameIHM.equalsIgnoreCase( fname ) ))
	                {
	                	sbFacetQueryUrl.append("&fq=" + fq);
	                	sfm.addFacet(fq);
	                	facetQueryTmp.add(fq);
	                	lstSingleFacetQueries.add(fq);
	                }
		        }
            }
//             for (String fq : facetQuery)
//            {
//                if (sbFacetQueryUrl.indexOf(fq) == -1)
//                {	
//                	//	sbFacetQueryUrl.append("&fq=" + fq);
//	                    sfm.addFacet(fq);
//	                    lstSingleFacetQueries.add(fq);
//                }
//            }
        }
         facetQuery = new String[facetQueryTmp.size()];
         facetQuery =  facetQueryTmp.toArray(facetQuery);

        if ( StringUtils.isNotBlank( conf.getFilterQuery(  ) ) )
        {
            int nNewLength = ( facetQuery == null ) ? 1 : ( facetQuery.length + 1 );
            String[] newFacetQuery = new String[nNewLength];

            for ( int i = 0; i < ( nNewLength - 1 ); i++ )
            {
                newFacetQuery[i] = facetQuery[i];
            }

            newFacetQuery[newFacetQuery.length - 1] = conf.getFilterQuery(  );
            facetQuery = newFacetQuery;
        }

        boolean bEncodeUri = Boolean.parseBoolean(AppPropertiesService.getProperty(PROPERTY_ENCODE_URI,
                Boolean.toString(DEFAULT_ENCODE_URI)));

        String strSearchPageUrl = AppPropertiesService.getProperty(PROPERTY_SEARCH_PAGE_URL);
        String strError = SolrConstants.CONSTANT_EMPTY_STRING;

        int nLimit = SOLR_RESPONSE_MAX;

        // Check XSS characters
        if ((strQuery != null) && (StringUtil.containsXssCharacters(strQuery)))
        {
            strError = I18nService.getLocalizedString(MESSAGE_INVALID_SEARCH_TERMS, locale);
        }

        if (StringUtils.isNotBlank(strError) || StringUtils.isBlank(strQuery))
        {
            strQuery = ALL_SEARCH_QUERY;

            String strOnlyFacets = AppPropertiesService.getProperty(PROPERTY_ONLY_FACTES);

            if (StringUtils.isNotBlank(strError)
                    || (((facetQuery == null) || (facetQuery.length <= 0))
                    && StringUtils.isNotBlank(strOnlyFacets) && SolrConstants.CONSTANT_TRUE.equals(strOnlyFacets)))
            {
                //no request and no facet selected : we show the facets but no result
                nLimit = 0;
            }
        }

        // paginator & session related elements
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt(PROPERTY_RESULTS_PER_PAGE, DEFAULT_RESULTS_PER_PAGE);
        String strCurrentItemsPerPage = request.getParameter(PARAMETER_NB_ITEMS_PER_PAGE);
        int nCurrentItemsPerPage = strCurrentItemsPerPage != null ? Integer.parseInt(strCurrentItemsPerPage) : 0;
        int nItemsPerPage = Paginator.getItemsPerPage(request, Paginator.PARAMETER_ITEMS_PER_PAGE, nCurrentItemsPerPage,
                nDefaultItemsPerPage);

        strCurrentPageIndex = (strCurrentPageIndex != null) ? strCurrentPageIndex : DEFAULT_PAGE_INDEX;

        SolrSearchEngine engine = SolrSearchEngine.getInstance();

        SolrFacetedResult facetedResult = engine.getFacetedSearchResults(strQuery, facetQuery, sort, order, nLimit, Integer.parseInt(strCurrentPageIndex), nItemsPerPage, SOLR_SPELLCHECK, group, groupField);
        List<SolrSearchResult> listResults = facetedResult.getSolrSearchResults();

        List<HashMap<String, Object>> points = null;
        if ( conf.getExtraMappingQuery(  ) )
        {
            List<SolrSearchResult> listResultsGeoloc = engine.getGeolocSearchResults( strQuery, facetQuery, nLimit );
            points = getGeolocModel(listResultsGeoloc);
        }

        // The page should not be added to the cache
        // Notify results infos to QueryEventListeners 
        notifyQueryListeners(strQuery, listResults.size(), request);

        UrlItem url = new UrlItem(strSearchPageUrl);
        String strQueryForPaginator = strQuery;

        if (bEncodeUri)
        {
            strQueryForPaginator = SolrUtil.encodeUrl(request, strQuery);
        }

        url.addParameter(PARAMETER_QUERY, strQueryForPaginator);
        url.addParameter(PARAMETER_NB_ITEMS_PER_PAGE, nItemsPerPage);

        if ( strConfCode != null )
        {
            url.addParameter( PARAMETER_CONF, strConfCode );
        }

        for (String strFacetName : lstSingleFacetQueries)
        {
            url.addParameter(PARAMETER_FACET_QUERY, SolrUtil.encodeUrl(strFacetName));
        }

        // nb items per page
        IPaginator<SolrSearchResult> paginator = new DelegatePaginator<SolrSearchResult>(listResults, nItemsPerPage,
                url.getUrl(), PARAMETER_PAGE_INDEX, strCurrentPageIndex, facetedResult.getCount());

        Map<String, Object> model = new HashMap<String, Object>();
        model.put(MARK_RESULTS_LIST, paginator.getPageItems());
        // put the query only if it's not *.*
        model.put(MARK_QUERY, ALL_SEARCH_QUERY.equals(strQuery) ? SolrConstants.CONSTANT_EMPTY_STRING : strQuery);
        model.put(MARK_FACET_QUERY, sbFacetQueryUrl.toString());
        model.put(MARK_PAGINATOR, paginator);
        model.put(MARK_NB_ITEMS_PER_PAGE, nItemsPerPage);
        model.put(MARK_ERROR, strError);
        model.put(MARK_FACETS, facetedResult.getFacetFields());
        model.put(MARK_SOLR_FIELDS, SolrFieldManager.getFacetList());
        model.put(MARK_FACETS_DATE, facetedResult.getFacetDateList());
        model.put(MARK_HISTORIQUE, sfm.getCurrentFacet());
        model.put(MARK_FACETS_LIST, lstSingleFacetQueries);
        model.put( MARK_CONF_QUERY, strConfCode );
        model.put( MARK_CONF, conf );
        model.put( MARK_POINTS, points );

        if (SOLR_SPELLCHECK && (strQuery != null) && (strQuery.compareToIgnoreCase(ALL_SEARCH_QUERY) != 0))
        {
            SpellCheckResponse checkResponse = engine.getSpellChecker(strQuery);

            if (checkResponse != null)
            {
            	model.put(MARK_SUGGESTION, checkResponse.getCollatedResults());
                //model.put(MARK_SUGGESTION, facetedResult.getSolrSpellCheckResponse().getCollatedResults());
            }
        }

        model.put(MARK_SORT_NAME, sort);
        model.put(MARK_SORT_ORDER, order);
        model.put(MARK_SORT_LIST, SolrFieldManager.getSortList());
        model.put(MARK_FACET_TREE, facetedResult.getFacetIntersection());
        model.put(MARK_ENCODING, SolrUtil.getEncoding());

        String strRequestUrl = request.getRequestURL().toString();
        model.put(FULL_URL, strRequestUrl);
        model.put(SOLR_FACET_DATE_GAP, SolrSearchEngine.SOLR_FACET_DATE_GAP);

        return model;
    }

    private static Hashtable<String, Boolean> getSwitched ()
    {
    	Hashtable <String,Boolean>tabFromSwitch = new Hashtable<String,Boolean>();
    	for (Field tmpField : SolrFieldManager.getFacetList(  ).values(  ))
    	{
    		if ( tmpField.getEnableFacet(  ) && 
    			"SWITCH".equalsIgnoreCase(tmpField.getOperator()))
    		{
    			tabFromSwitch.put(tmpField.getName(),Boolean.FALSE);
    		}
    	}
    	return tabFromSwitch;
    }
    
    private static void isSwitched( String strFacetQuery, Hashtable<String, Boolean> tabType)
    {
          if (strFacetQuery != null && tabType!=null &&
        	tabType.containsKey( strFacetQuery ) &&
        	tabType.get( strFacetQuery ) == Boolean.FALSE)
        {
        	tabType.remove(strFacetQuery);
        	tabType.put(strFacetQuery, Boolean.TRUE);
        }
    }
    /**
     * Returns a model with points data from a geoloc search
     * @param listResultsGeoloc the result of a search
     * @return the model
     */
    private static List<HashMap<String, Object>> getGeolocModel( List<SolrSearchResult> listResultsGeoloc ) {
        List<HashMap<String, Object>> points = new ArrayList<HashMap<String, Object>>( listResultsGeoloc.size(  ) );
        HashMap<String, String> iconKeysCache = new HashMap<String, String>(  );

        for ( SolrSearchResult result : listResultsGeoloc )
        {
            Map<String, Object> dynamicFields = result.getDynamicFields(  );

            for ( String key : dynamicFields.keySet(  ) )
            {
                if ( key.endsWith( SolrItem.DYNAMIC_GEOJSON_FIELD_SUFFIX ) )
                {
                    HashMap<String, Object> h = new HashMap<String, Object>(  );
                    String strJson = (String) dynamicFields.get( key );
                    GeolocItem geolocItem = null;

                    try
                    {
                        geolocItem = GeolocItem.fromJSON( strJson );
                    }
                    catch ( IOException e )
                    {
                        AppLogService.error( "SolrSearchApp: error parsing geoloc JSON: " + strJson +
                            ", exception " + e );
                    }

                    if ( geolocItem != null )
                    {
                        String strType = result.getId(  ).substring( result.getId(  ).lastIndexOf( "_" ) + 1 );
                        String strIcon;

                        if ( iconKeysCache.containsKey( geolocItem.getIcon(  ) ) )
                        {
                            strIcon = iconKeysCache.get( geolocItem.getIcon(  ) );
                        }
                        else
                        {
                            strIcon = IconService.getIcon( strType, geolocItem.getIcon(  ) );
                            iconKeysCache.put( geolocItem.getIcon(  ), strIcon );
                        }

                        geolocItem.setIcon( strIcon );
                        h.put( MARK_POINTS_GEOJSON, geolocItem.toJSON(  ) );
                        h.put( MARK_POINTS_ID,
                            result.getId(  )
                                  .substring( result.getId(  ).indexOf( "_" ) + 1,
                                result.getId(  ).lastIndexOf( "_" ) ) );
                        h.put( MARK_POINTS_FIELDCODE, key.substring( 0, key.lastIndexOf( "_" ) ) );
                        h.put( MARK_POINTS_TYPE, strType );
                        points.add( h );
                    }
                }
            }
        }
        return points;
    }

    /**
     * Notify all query Listeners
     *
     * @param strQuery The query
     * @param nResultsCount The results count
     * @param request The request
     */
    private static void notifyQueryListeners(String strQuery, int nResultsCount, HttpServletRequest request)
    {
        QueryEvent event = new QueryEvent();
        event.setQuery(strQuery);
        event.setResultsCount(nResultsCount);
        event.setRequest(request);
        QueryListenersService.getInstance().notifyListeners(event);
    }

    /**
     * Return the model used during the last search
     *
     * @param request The HTTP request.
     * @return the model used during the last search
     * @deprecated model is not stored in session anymore
     */
    @Deprecated
    public static Map<String, Object> getLastSearchModel(HttpServletRequest request)
    {
        AppLogService.error("calling deprecated code : SolrSearchApp.getLastSearchModel( HttpServletRequest request )");
        return (Map<String, Object>) request.getSession().getAttribute(PARAMETER_PREVIOUS_SEARCH);
    }
}
