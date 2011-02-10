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
import fr.paris.lutece.portal.service.search.SearchResult;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * Util class for Solr search.
 *
 */
public final class SolrUtil
{
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

            if ( highlights != null )
            {
                searchResult.setHighlight( highlights.getHighlights( searchResult.getId(  ) ) );
            }

            resultList.add( searchResult );
        }

        return resultList;
    }
}
