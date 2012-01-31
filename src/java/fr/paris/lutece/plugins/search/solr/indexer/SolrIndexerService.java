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
package fr.paris.lutece.plugins.search.solr.indexer;

import fr.paris.lutece.plugins.search.solr.business.SolrServerService;
import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.plugins.search.solr.business.indexeraction.SolrIndexerAction;
import fr.paris.lutece.plugins.search.solr.business.indexeraction.SolrIndexerActionHome;
import fr.paris.lutece.plugins.search.solr.service.SolrPlugin;
import fr.paris.lutece.plugins.search.solr.util.SolrConstants;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *
 * SolrIndexerService
 *
 */
public final class SolrIndexerService
{
    private static final SolrServer SOLR_SERVER = SolrServerService.getInstance(  ).getSolrServer(  );
    private static final List<SolrIndexer> INDEXERS = initIndexersList(  );
    private static final String PARAM_TYPE_PAGE = "Page";
    private static StringBuffer _sbLogs;
    private static final String PROPERTY_SITE = "lutece.name";
    private static final String PROPERTY_PROD_URL = "lutece.prod.url";
    private static final String PROPERTY_BASE_URL = "lutece.base.url";

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
     * Index one document, called by plugin indexers
     * @param doc the document to index
     * @throws CorruptIndexException corruptIndexException
     * @throws IOException i/o exception
     */
    public static void write( SolrItem solrItem ) throws CorruptIndexException, IOException
    {
        try
        {
            SolrInputDocument solrInputDocument = solrItem2SolrInputDocument( solrItem );
            SOLR_SERVER.add( solrInputDocument );
            SOLR_SERVER.commit(  );

            _sbLogs.append( "Indexing " );
            _sbLogs.append( solrItem.getType(  ) );
            _sbLogs.append( " #" );
            _sbLogs.append( solrItem.getUid(  ) );
            _sbLogs.append( " - " );
            _sbLogs.append( solrItem.getTitle(  ) );
            _sbLogs.append( "\r\n" );
        }
        catch ( Exception e )
        {
            printIndexMessage( e );
        }
    }

    /**
     * Process the indexing
     * @param bCreate tell if it's total indexing or total (total = true)
     * @return the result log of the indexing
     */
    public static synchronized String processIndexing( boolean bCreate )
    {
        // String buffer for building the response page;
        _sbLogs = new StringBuffer(  );

        Plugin plugin = PluginService.getPlugin( SolrPlugin.PLUGIN_NAME );

        boolean bCreateIndex = bCreate;
        String strWebappName = getWebAppName(  );

        try
        {
            Directory dir = IndexationService.getDirectoryIndex(  );

            if ( !IndexReader.indexExists( dir ) )
            { //init index
                bCreateIndex = true;
            }

            Date start = new Date(  );

            if ( bCreateIndex )
            {
                _sbLogs.append( "\r\nIndexing all contents ...\r\n" );

                // Remove all indexed values of this site
                SOLR_SERVER.deleteByQuery( SearchItem.FIELD_UID + ":" + strWebappName +
                    SolrConstants.CONSTANT_UNDERSCORE + SolrConstants.CONSTANT_WILDCARD );

                for ( SolrIndexer solrIndexer : INDEXERS )
                {
                    if ( solrIndexer.isEnable(  ) )
                    {
                        _sbLogs.append( "\r\n<strong>Indexer : " );
                        _sbLogs.append( solrIndexer.getName(  ) );
                        _sbLogs.append( " - " );
                        _sbLogs.append( solrIndexer.getDescription(  ) );
                        _sbLogs.append( "</strong>\r\n" );

                        //the indexer will call write(doc)
                        List<String> lstErrors = solrIndexer.indexDocuments(  );

                        if ( lstErrors != null )
                        {
                            for ( String strError : lstErrors )
                            {
                                _sbLogs.append( "<strong>ERROR : " );
                                _sbLogs.append( strError );
                                _sbLogs.append( "</strong>\r\n" );
                            }
                        }
                    }
                }

                // Remove all actions of the database
                SolrIndexerActionHome.removeAll( plugin );
            }
            else
            {
                _sbLogs.append( "\r\nIncremental Indexing ...\r\n" );

                //incremental indexing
                Collection<SolrIndexerAction> actions = SolrIndexerActionHome.getList( plugin );

                for ( SolrIndexerAction action : actions )
                {
                    // catch any exception coming from an indexer to prevent global indexation to fail
                    try
                    {
                        SolrIndexer indexer = findSolrIndexer( action.getTypeResource(  ) );

                        if ( indexer == null )
                        {
                            _sbLogs.append( " - ERROR : " );
                            _sbLogs.append( " No indexer found for the resource name : " + action.getTypeResource(  ) );
                            _sbLogs.append( "</strong>\r\n" );

                            continue;
                        }

                        if ( action.getIdTask(  ) == IndexerAction.TASK_DELETE )
                        {
                            if ( action.getIdPortlet(  ) != IndexationService.ALL_DOCUMENT )
                            {
                                //delete only the index linked to this portlet
                                SOLR_SERVER.deleteByQuery( SearchItem.FIELD_DOCUMENT_PORTLET_ID + ":" +
                                    action.getIdDocument(  ) + "&" + Integer.toString( action.getIdPortlet(  ) ) );
                            }
                            else
                            {
                                //delete all index linked to uid. We get the uid of the resource to prefix it like we do during the indexation 
                                SOLR_SERVER.deleteByQuery( SearchItem.FIELD_UID + ":" + strWebappName +
                                    SolrConstants.CONSTANT_UNDERSCORE +
                                    indexer.getResourceUid( action.getIdDocument(  ), action.getTypeResource(  ) ) );
                            }

                            _sbLogs.append( "Deleting " );
                            _sbLogs.append( " #" );
                            _sbLogs.append( action.getIdDocument(  ) );
                            _sbLogs.append( "\r\n" );
                        }
                        else
                        {
                            List<SolrItem> lstItems = indexer.getDocuments( action.getIdDocument(  ) );

                            if ( ( lstItems != null ) && !lstItems.isEmpty(  ) )
                            {
                                for ( SolrItem item : lstItems )
                                {
                                    if ( ( action.getIdPortlet(  ) == IndexationService.ALL_DOCUMENT ) ||
                                            ( ( item.getDocPortletId(  ) != null ) &&
                                            item.getDocPortletId(  )
                                                    .equals( item.getUid(  ) + "&" + action.getIdPortlet(  ) ) ) )
                                    {
                                        if ( action.getIdTask(  ) == IndexerAction.TASK_CREATE )
                                        {
                                            _sbLogs.append( "Adding " );
                                        }
                                        else if ( action.getIdTask(  ) == IndexerAction.TASK_MODIFY )
                                        {
                                            _sbLogs.append( "Updating " );
                                        }

                                        SOLR_SERVER.add( solrItem2SolrInputDocument( item ) );
                                        SOLR_SERVER.commit(  );

                                        _sbLogs.append( item.getType(  ) );
                                        _sbLogs.append( " #" );
                                        _sbLogs.append( item.getUid(  ) );
                                        _sbLogs.append( " - " );
                                        _sbLogs.append( item.getTitle(  ) );
                                        _sbLogs.append( "\r\n" );
                                    }
                                }
                            }
                        }

                        SolrIndexerActionHome.remove( action.getIdAction(  ), plugin );
                    }
                    catch ( Exception e )
                    {
                        _sbLogs.append( "\r\n<strong>Action from indexer : " );
                        _sbLogs.append( action.getIndexerName(  ) );
                        _sbLogs.append( " Action ID : " + action.getIdAction(  ) + " - Document ID : " +
                            action.getIdDocument(  ) );
                        _sbLogs.append( " - ERROR : " );
                        _sbLogs.append( e.getMessage(  ) +
                            ( ( e.getCause(  ) != null ) ? ( " : " + e.getCause(  ).getMessage(  ) )
                                                         : SolrConstants.CONSTANT_EMPTY_STRING ) );
                        _sbLogs.append( "</strong>\r\n" );
                    }
                }

                //reindexing all pages.
                SOLR_SERVER.deleteByQuery( SearchItem.FIELD_TYPE + ":" + PARAM_TYPE_PAGE );

                for ( SolrIndexer indexer : INDEXERS )
                {
                    if ( SolrPageIndexer.NAME.equals( indexer.getName(  ) ) )
                    {
                        indexer.indexDocuments(  );

                        break;
                    }
                }
            }

            SOLR_SERVER.commit(  );
            SOLR_SERVER.optimize(  );

            Date end = new Date(  );
            _sbLogs.append( "Duration of the treatment : " );
            _sbLogs.append( end.getTime(  ) - start.getTime(  ) );
            _sbLogs.append( " milliseconds\r\n" );
        }
        catch ( Exception e )
        {
            _sbLogs.append( " caught a " );
            _sbLogs.append( e.getClass(  ) );
            _sbLogs.append( "\n with message: " );
            _sbLogs.append( e.getMessage(  ) );
            _sbLogs.append( "\r\n" );
            AppLogService.error( "Indexing error : " + e.getMessage(  ), e );
        }

        return _sbLogs.toString(  );
    }

    /**
     * Build the message error of an exception
     * @param exception The exception
     * @return the message error of the exception
     */
    public static String buildErrorMessage( Exception exception )
    {
        StringBuffer sb = new StringBuffer(  );
        sb.append( exception.getMessage(  ) );

        if ( exception.getCause(  ) != null )
        {
            sb.append( SolrConstants.CONSTANT_SPACE ).append( SolrConstants.CONSTANT_COLON )
              .append( SolrConstants.CONSTANT_SPACE );
            sb.append( exception.getCause(  ).getMessage(  ) );
        }

        return sb.toString(  );
    }

    /**
     * Returns the list of all dynamic fields.
     * @return the list of all additional fields
     */
    public static List<Field> getAdditionalFields(  )
    {
        List<Field> lstFields = new ArrayList<Field>(  );

        for ( SolrIndexer solrIndexer : INDEXERS )
        {
            List<Field> lstAdditionalFields = solrIndexer.getAdditionalFields(  );

            if ( lstAdditionalFields != null )
            {
                lstFields.addAll( lstAdditionalFields );
            }
        }

        return lstFields;
    }

    /**
     * Adds the exception into the buffer and the logs
     * @param exception Exception to report
     */
    private static void printIndexMessage( Exception exception )
    {
        _sbLogs.append( " - ERROR : " );
        _sbLogs.append( exception.getMessage(  ) );

        if ( exception.getCause(  ) != null )
        {
            _sbLogs.append( " : " );
            _sbLogs.append( exception.getCause(  ).getMessage(  ) );
        }

        _sbLogs.append( "</strong>\r\n" );
        AppLogService.error( exception.getMessage(  ), exception );
    }

    /**
     * Index resources of the indexer and add formated logs into the buffer
     */

    /*
    private static void index( SolrIndexer solrIndexer, StringBuilder sbLogs )
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
                                printIndexMessage( sbLogs, e );
                        }
                        catch ( SolrServerException e )
                        {
                                printIndexMessage( sbLogs, e );
                        }
        }
        sbLogs.append( SolrConstants.CONSTANT_BR_TAG );
    }
    */

    /**
     * Find the indexer of the resource parameter
     * @param strResourceName the name of the resource to index
     * @return the indexer of the resource
     */
    private static SolrIndexer findSolrIndexer( String strResourceName )
    {
        for ( SolrIndexer indexer : INDEXERS )
        {
            List<String> lstResources = indexer.getResourcesName(  );

            if ( ( lstResources != null ) && lstResources.contains( strResourceName ) )
            {
                return indexer;
            }
        }

        return null;
    }

    /**
     * Convert a {@link SolrItem} object into a {@link SolrInputDocument} object
     * @param solrItem the item to convert
     * @return A {@link SolrInputDocument} object corresponding to the item parameter
     */
    private static SolrInputDocument solrItem2SolrInputDocument( SolrItem solrItem )
    {
        SolrInputDocument solrInputDocument = new SolrInputDocument(  );
        String strWebappName = getWebAppName(  );

        // Prefix the uid by the name of the site. Without that, it is necessary imposible to index two resources of two different sites with the same identifier
        solrInputDocument.addField( SearchItem.FIELD_UID,
            strWebappName + SolrConstants.CONSTANT_UNDERSCORE + solrItem.getUid(  ) );
        solrInputDocument.addField( SearchItem.FIELD_DATE, solrItem.getDate(  ) );
        solrInputDocument.addField( SearchItem.FIELD_TYPE, solrItem.getType(  ) );
        solrInputDocument.addField( SearchItem.FIELD_SUMMARY, solrItem.getSummary(  ) );
        solrInputDocument.addField( SearchItem.FIELD_TITLE, solrItem.getTitle(  ) );
        solrInputDocument.addField( SolrItem.FIELD_SITE, solrItem.getSite(  ) );
        solrInputDocument.addField( SearchItem.FIELD_ROLE, solrItem.getRole(  ) );
        solrInputDocument.addField( SolrItem.FIELD_XML_CONTENT, solrItem.getXmlContent(  ) );
        solrInputDocument.addField( SearchItem.FIELD_URL, solrItem.getUrl(  ) );
        solrInputDocument.addField( SolrItem.FIELD_HIERATCHY_DATE, solrItem.getHieDate(  ) );
        solrInputDocument.addField( SolrItem.FIELD_CATEGORIE, solrItem.getCategorie(  ) );
        solrInputDocument.addField( SolrItem.FIELD_CONTENT, solrItem.getContent(  ) );
        solrInputDocument.addField( SearchItem.FIELD_DOCUMENT_PORTLET_ID, solrItem.getDocPortletId(  ) );

        // Add the dynamic fields
        // They must be declared into the schema.xml of the solr server
        Map<String, Object> mapDynamicFields = solrItem.getDynamicFields(  );

        for ( String strDynamicField : mapDynamicFields.keySet(  ) )
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

    /**
     * Return the url of the webapp
     * @return strBase the webapp url
     */
    public static String getBaseUrl(  )
    {
        String strBaseUrl = AppPropertiesService.getProperty( PROPERTY_BASE_URL );

        if ( StringUtils.isBlank( strBaseUrl ) )
        {
            strBaseUrl = AppPropertiesService.getProperty( PROPERTY_PROD_URL );
        }

        if ( !strBaseUrl.endsWith( "/" ) )
        {
            strBaseUrl = strBaseUrl + "/";
        }

        return strBaseUrl + AppPathService.getPortalUrl(  );
    }

    /**
     * Return the name of the webapp
     * @return the name of the webapp
     */
    public static String getWebAppName(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_SITE );
    }

    /**
     * Del sorl index related to this site.
     * @return log of what appended
     */
    public static String processDel(  )
    {
        String strLog = "";
        String strSite = AppPropertiesService.getProperty( PROPERTY_SITE );

        try
        {
            SOLR_SERVER.deleteByQuery( SolrItem.FIELD_SITE + ":\"" + strSite + "\"" );
            SOLR_SERVER.commit(  );
            SOLR_SERVER.optimize(  );
            strLog = "Delete site : " + strSite;
            AppLogService.info( strLog );
        }
        catch ( Exception e )
        {
            AppLogService.error( "Erreur lors de la suppression de l'index solr", e );
            strLog = ( e.getCause( ) != null ? e.getCause( ).toString( ) : e.toString( ) );
            // strLog = e.getCause( ).toString( ); FIXME NPE si pas de cause
        }

        return strLog;
    }
}
