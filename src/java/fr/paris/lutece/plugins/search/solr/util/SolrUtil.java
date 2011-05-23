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
package fr.paris.lutece.plugins.search.solr.util;

import fr.paris.lutece.plugins.search.solr.business.SolrHighlights;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchItem;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchResult;
import fr.paris.lutece.plugins.search.solr.indexer.SolrItem;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * Util class for Solr search.
 *
 */
public final class SolrUtil
{
	private static final String MESSAGE_ENCODING_ERROR = "portal.search.message.encodingError";
    private static final String PROPERTY_ENCODE_URI_ENCODING = "search.encode.uri.encoding";
    private static final String DEFAULT_URI_ENCODING = "ISO-8859-1";
    
    /**
     * Empty private constructor
     */
    private SolrUtil(  )
    {
    }

    /**
     * Transform a DocumentList to SearchResult List.
     * @param documentList the document list to transform
     * @return a SearchResult List.
     */
    public static List<SearchResult> transformSolrDocumentList( SolrDocumentList documentList )
    {
        Map<String, SearchResult> mapResults = new HashMap<String, SearchResult>(  );

        for ( SolrDocument document : documentList )
        {
            SolrSearchItem searchItem = new SolrSearchItem( document );
            SearchResult searchResult = new SearchResult(  );
            searchResult.setId( searchItem.getId(  ) );
            searchResult.setSummary( searchItem.getSummary(  ) );
            searchResult.setTitle( searchItem.getTitle(  ) );
            searchResult.setType( searchItem.getType(  ) );
            searchResult.setUrl( searchItem.getUrl(  ) );
            searchResult.setDate( searchItem.getDate(  ) );
            searchResult.setRole( new ArrayList<String>(  ) );
            mapResults.put( searchResult.getUrl(  ), searchResult );
        }

        return new ArrayList<SearchResult>( mapResults.values(  ) );
    }

    /**
     * Transforms a list of SolrItems to SolrSearchResults List.
     * @param itemList the SolrItem List.
     * @param highlights the SolrHighlights.
     * @return SolrSearchResult List.
     */
    public static List<SolrSearchResult> transformSolrItemsToSolrSearchResults( List<SolrItem> itemList,
        SolrHighlights highlights )
    {
        List<SolrSearchResult> resultList = new ArrayList<SolrSearchResult>( itemList.size(  ) );

        for ( SolrItem item : itemList )
        {
            SolrSearchResult searchResult = new SolrSearchResult(  );
            searchResult.setDate( item.getDate(  ) );
            searchResult.setId( item.getUid(  ) );
            searchResult.setRole( new ArrayList<String>(  ) );
            searchResult.setSummary( item.getSummary(  ) );
            searchResult.setSite( item.getSite(  ) );
            searchResult.setTitle( item.getTitle(  ) );
            searchResult.setType( item.getType(  ) );
            searchResult.setUrl( item.getUrl(  ) );
            searchResult.setCategorie( item.getCategorie(  ) );
            searchResult.setXmlContent( item.getXmlContent(  ) );
            searchResult.setMetadata( item.getMetadata(  ) );
            searchResult.setDocPortletId( item.getDocPortletId(  ) );
            searchResult.setHieDate( item.getHieDate() );
            
            // The name of the dynamic fields is like NAME_XXX where XXX is a SolrItem dynamic field type
            searchResult.setDynamicFields( item.getDynamicFields() );
            
            if ( highlights != null )
            {
                searchResult.setHighlight( highlights.getHighlights( searchResult.getId(  ) ) );
            }

            resultList.add( searchResult );
        }

        return resultList;
    }
    
    /**
     * encode url
     * @param strSource source
     * @return encoded url, null otherwise
     */
    public static String encodeUrl( String strSource )
    {
    	try
    	{
    		return encodeUrl( null, strSource);
    	}
    	catch ( SiteMessageException sme )
    	{
    		return null;
    	}
    }
    
    /**
     * Encode an url string
     * @param strSource The string to encode
     * @param request the http request (can be null)
     * @return The encoded string
     * @throws SiteMessageException exception and request != null
     */
    public static String encodeUrl( HttpServletRequest request, String strSource )
        throws SiteMessageException
    {
        String strEncoded = SolrConstants.CONSTANT_EMPTY_STRING;

        try
        {
            strEncoded = URLEncoder.encode( strSource, getEncoding(  ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            AppLogService.error( e.getMessage(  ), e );
            if ( request != null )
            {
            	SiteMessageService.setMessage( request, MESSAGE_ENCODING_ERROR, SiteMessage.TYPE_ERROR );
            }
        }

        return strEncoded;
    }
    
    /**
     * Return the encoding define in the properties
     * @return the encoding define in the properties
     */
    public static String getEncoding(  )
    {
    	String strURIEncoding = AppPropertiesService.getProperty( PROPERTY_ENCODE_URI_ENCODING, DEFAULT_URI_ENCODING );
    	
    	return strURIEncoding;
    }
}
