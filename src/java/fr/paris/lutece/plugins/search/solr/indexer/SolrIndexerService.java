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
package fr.paris.lutece.plugins.search.solr.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import fr.paris.lutece.plugins.search.solr.business.SolrServerService;
import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.plugins.search.solr.util.SolrConstants;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;


/**
 *
 * SolrIndexerService
 *
 */
public final class SolrIndexerService
{
	private static final SolrServer SOLR_SERVER = SolrServerService.getInstance(  ).getSolrServer(  );
    private static final List<SolrIndexer> INDEXERS = initIndexersList(  );

    /**
     * Empty private constructor
     */
    private SolrIndexerService(  )
    {
    }

    /**
     * Returns the indexers List
     * @return the indexers List
     */
    public static List<SolrIndexer> getIndexers(  )
    {
        return INDEXERS;
    }

    /**
     * Calls all known indexers
     * @return the logs
     */
    public static String processIndexing(  )
    {
        StringBuilder sbLogs = new StringBuilder(  );

        for ( SolrIndexer solrIndexer : INDEXERS )
        {
        	Map<String, SolrItem> mapDatas = solrIndexer.index( );
        	sbLogs.append( solrIndexer.getName(  ) );
            sbLogs.append( SolrConstants.CONSTANT_SPACE );
            sbLogs.append( solrIndexer.getVersion(  ) );
            sbLogs.append( SolrConstants.CONSTANT_BR_TAG );
            for( String strLog : mapDatas.keySet() )
            {
            	try
				{
            		//Converts the SolrItem object to SolrInputDocument
            		SolrInputDocument solrInputDocument = solrItem2SolrInputDocument( mapDatas.get( strLog ) );
					SOLR_SERVER.add( solrInputDocument );
					SOLR_SERVER.commit();
	            	sbLogs.append( strLog );
				}
				catch ( IOException e )
				{
					AppLogService.error( e.getMessage(  ), e );
				}
				catch ( SolrServerException e )
				{
					AppLogService.error( e.getMessage(  ), e );
				}
            }
            sbLogs.append( SolrConstants.CONSTANT_BR_TAG );
        }
        
        return sbLogs.toString(  );
    }

    /**
     * Returns the list of all dynamic fields.
     * @return the list of all additional fields
     */
    public static List<Field> getAdditionalFields(  )
    {
    	List<Field> lstFields = new ArrayList<Field>();
    	for ( SolrIndexer solrIndexer : INDEXERS )
        {
    		lstFields.addAll( solrIndexer.getAdditionalFields() );
        }
    	
    	return lstFields;
    }
    
    /**
     * Convert a {@link SolrItem} object into a {@link SolrInputDocument} object
     * @param solrItem the item to convert
     * @return A {@link SolrInputDocument} object corresponding to the item parameter
     */
    private static SolrInputDocument solrItem2SolrInputDocument( SolrItem solrItem )
    {
    	SolrInputDocument solrInputDocument = new SolrInputDocument();
    	
    	solrInputDocument.addField( SearchItem.FIELD_UID, solrItem.getUid() );
    	solrInputDocument.addField( SearchItem.FIELD_DATE, solrItem.getDate() );
    	solrInputDocument.addField( SearchItem.FIELD_TYPE, solrItem.getType(  ) );
    	solrInputDocument.addField( SearchItem.FIELD_SUMMARY, solrItem.getSummary(  ) );
    	solrInputDocument.addField( SearchItem.FIELD_TITLE, solrItem.getTitle(  ) );
    	solrInputDocument.addField( SolrItem.FIELD_SITE, solrItem.getSite() );
    	solrInputDocument.addField( SearchItem.FIELD_ROLE, solrItem.getRole() );
    	solrInputDocument.addField( SolrItem.FIELD_XML_CONTENT, solrItem.getXmlContent() );
    	solrInputDocument.addField( SearchItem.FIELD_URL, solrItem.getUrl() );
    	solrInputDocument.addField( SolrItem.FIELD_HIERATCHY_DATE, solrItem.getHieDate() );
    	solrInputDocument.addField( SolrItem.FIELD_CATEGORIE, solrItem.getCategorie() );
    	solrInputDocument.addField( SolrItem.FIELD_CONTENT, solrItem.getContent() );

    	// Add the dynamic fields
    	// They must be declared into the schema.xml of the solr server
    	Map<String, Object> mapDynamicFields = solrItem.getDynamicFields(); 
		for( String strDynamicField : mapDynamicFields.keySet() )
		{
			solrInputDocument.addField( strDynamicField, mapDynamicFields.get( strDynamicField ) );
		}
    	
    	return solrInputDocument;
    }
    
    /**
     * Initialize the indexers List.
     * @return the indexers List
     */
    private static List<SolrIndexer> initIndexersList(  )
    {
        return SpringContextService.getBeansOfType( SolrIndexer.class );
    }
}
