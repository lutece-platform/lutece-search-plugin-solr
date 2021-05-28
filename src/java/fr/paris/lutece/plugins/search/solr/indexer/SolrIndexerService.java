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
package fr.paris.lutece.plugins.search.solr.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;

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

/**
 *
 * SolrIndexerService
 *
 */
public final class SolrIndexerService
{
    private static final SolrClient SOLR_SERVER = SolrServerService.getInstance( ).getSolrServer( );
    private static final List<SolrIndexer> INDEXERS = initIndexersList( );
    private static final String PARAM_TYPE_PAGE = "PAGE";
    private static StringBuilder _sbLogs;
    private static final String PROPERTY_SITE = "lutece.name";
    private static final String PROPERTY_PROD_URL = "lutece.prod.url";
    private static final String PROPERTY_BASE_URL = "lutece.base.url";
    private static final String LOG_ERROR = " - ERROR : ";
    private static final String LOG_CLOSE_STRONG = "</strong>\r\n";
    private static final String PROPERTY_COMMIT_SIZE = "solr.indexer.commit.size";

    /**
     * Empty private constructor
     */
    private SolrIndexerService( )
    {
    }

    /**
     * Returns the indexers List
     * 
     * @return the indexers List
     */
    public static List<SolrIndexer> getIndexers( )
    {
        return new ArrayList<>( INDEXERS );
    }

    /**
     * Index one document without committing
     * 
     * @param solrItem
     *            The item
     * @param sbLogs
     *            StringBuilder to write to
     * @throws IOException
     *             i/o exception
     */
    private static void writeNoCommit( SolrItem solrItem, StringBuilder sbLogs ) throws IOException
    {
        try
        {
            sbLogs.append( "Indexing " );
            sbLogs.append( solrItem.getType( ) );
            sbLogs.append( " #" );
            sbLogs.append( solrItem.getUid( ) );
            sbLogs.append( " - " );
            sbLogs.append( solrItem.getTitle( ) );
            SolrInputDocument solrInputDocument = solrItem2SolrInputDocument( solrItem );
            SOLR_SERVER.add( solrInputDocument );
            sbLogs.append( "\r\n" );
        }
        catch( Exception e )
        {
            printIndexMessage( e, sbLogs );
        }
    }

    /**
     * Index one document, called by plugin indexers
     * 
     * @param solrItem
     *            The item
     * @throws IOException
     *             i/o exception
     */
    public static void write( SolrItem solrItem ) throws IOException
    {
        write( solrItem, _sbLogs );
    }

    /**
     * Index one document, called by external code
     * 
     * @param solrItem
     *            The item
     * @param sbLogs
     *            StringBuilder to write to
     * @throws IOException
     *             i/o exception
     */
    public static void write( SolrItem solrItem, StringBuilder sbLogs ) throws IOException
    {
        try
        {
            writeNoCommit( solrItem, sbLogs );
            SOLR_SERVER.commit( );
        }
        catch( Exception e )
        {
            printIndexMessage( e, sbLogs );
        }
    }

    /**
     * Index a collection of documents, called by plugin indexers
     * 
     * @param solrItems
     *            The item
     * @throws IOException
     *             i/o exception
     */
    public static void write( Collection<SolrItem> solrItems ) throws IOException
    {
        write( solrItems, _sbLogs );
    }

    /**
     * Index a collection of documents, called by external code
     * 
     * @param solrItems
     *            The item
     * @param sbLogs
     *            StringBuilder to write to
     * @throws IOException
     *             i/o exception
     */
    public static void write( Collection<SolrItem> solrItems, StringBuilder sbLogs ) throws IOException
    {
        try
        {
            int commitSize = Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_COMMIT_SIZE ) );
            int count = 0;
            for ( SolrItem solrItem : solrItems )
            {
                count++;
                writeNoCommit( solrItem, sbLogs );
                if ( count % commitSize == 0 )
                {
                    SOLR_SERVER.commit( );
                }
            }
            SOLR_SERVER.commit( );
        }
        catch( Exception e )
        {
            printIndexMessage( e, sbLogs );
        }
    }

    /**
     * Process the indexing
     * 
     * @param bCreate
     *            tell if it's total indexing or total (total = true)
     * @return the result log of the indexing
     */
    public static synchronized String processIndexing( boolean bCreate )
    {
        // String buffer for building the response page
        _sbLogs = new StringBuilder( );
        boolean bCreateIndex = bCreate;
        String strWebappNameEscaped = ClientUtils.escapeQueryChars( getWebAppName( ) );

        try
        {
            Date start = new Date( );

            if ( bCreateIndex )
            {
                processFullIndexing( strWebappNameEscaped );
            }
            else
            {
                processIncrementalIndexing( strWebappNameEscaped );
            }

            SOLR_SERVER.commit( );
            SOLR_SERVER.optimize( );

            Date end = new Date( );
            _sbLogs.append( "Duration of the treatment : " );
            _sbLogs.append( end.getTime( ) - start.getTime( ) );
            _sbLogs.append( " milliseconds\r\n" );
        }
        catch( Exception e )
        {
            _sbLogs.append( " caught a " );
            _sbLogs.append( e.getClass( ) );
            _sbLogs.append( "\r\n with message: " );
            _sbLogs.append( e.getMessage( ) );
            _sbLogs.append( "\r\n See error logs for the stacktrace.\r\n" );
            AppLogService.error( "Indexing error : " + e.getMessage( ), e );
        }

        return _sbLogs.toString( );
    }

    private static void processFullIndexing( String strWebappNameEscaped ) throws SolrServerException, IOException
    {
        Plugin plugin = PluginService.getPlugin( SolrPlugin.PLUGIN_NAME );

        _sbLogs.append( "\r\nIndexing all contents ...\r\n" );

        // Remove all indexed values of this site
        SOLR_SERVER.deleteByQuery( SearchItem.FIELD_UID + ":" + strWebappNameEscaped + SolrConstants.CONSTANT_UNDERSCORE + SolrConstants.CONSTANT_WILDCARD );

        for ( SolrIndexer solrIndexer : INDEXERS )
        {
            if ( solrIndexer.isEnable( ) )
            {
                _sbLogs.append( "\r\n<strong>Indexer : " );
                _sbLogs.append( solrIndexer.getName( ) );
                _sbLogs.append( " - " );
                _sbLogs.append( solrIndexer.getDescription( ) );
                _sbLogs.append( LOG_CLOSE_STRONG );

                // the indexer will call write(doc)
                List<String> lstErrors = solrIndexer.indexDocuments( );

                if ( lstErrors != null )
                {
                    for ( String strError : lstErrors )
                    {
                        _sbLogs.append( "<strong>ERROR : " );
                        _sbLogs.append( strError );
                        _sbLogs.append( LOG_CLOSE_STRONG );
                    }
                }
            }
        }

        // Remove all actions of the database
        SolrIndexerActionHome.removeAll( plugin );
    }

    private static void processIncrementalIndexing( String strWebappNameEscaped ) throws SolrServerException, IOException
    {
        Plugin plugin = PluginService.getPlugin( SolrPlugin.PLUGIN_NAME );

        _sbLogs.append( "\r\nIncremental Indexing ...\r\n" );

        // incremental indexing
        Collection<SolrIndexerAction> actions = SolrIndexerActionHome.getList( plugin );

        for ( SolrIndexerAction action : actions )
        {
            processIndexingAction( action, strWebappNameEscaped );
        }

        // reindexing all pages.
        SOLR_SERVER.deleteByQuery( SearchItem.FIELD_TYPE + ":" + PARAM_TYPE_PAGE + " AND " + SearchItem.FIELD_UID + ":" + strWebappNameEscaped
                + SolrConstants.CONSTANT_UNDERSCORE + SolrConstants.CONSTANT_WILDCARD );

        for ( SolrIndexer indexer : INDEXERS )
        {
            if ( indexer.isEnable( ) && SolrPageIndexer.NAME.equals( indexer.getName( ) ) )
            {
                indexer.indexDocuments( );

                break;
            }
        }
    }

    private static void processIndexingAction( SolrIndexerAction action, String strWebappNameEscaped )
    {
        Plugin plugin = PluginService.getPlugin( SolrPlugin.PLUGIN_NAME );

        // catch any exception coming from an indexer to prevent global indexation to fail
        try
        {
            SolrIndexer indexer = findSolrIndexer( action.getTypeResource( ) );

            if ( indexer == null )
            {
                _sbLogs.append( LOG_ERROR );
                _sbLogs.append( " No indexer found for the resource name : " ).append( action.getTypeResource( ) );
                _sbLogs.append( LOG_CLOSE_STRONG );

                return;
            }

            if ( action.getIdTask( ) == IndexerAction.TASK_DELETE )
            {
                if ( action.getIdPortlet( ) != IndexationService.ALL_DOCUMENT )
                {
                    // delete only the index linked to this portlet
                    SOLR_SERVER.deleteByQuery(
                            SearchItem.FIELD_DOCUMENT_PORTLET_ID + ":" + action.getIdDocument( ) + "&" + Integer.toString( action.getIdPortlet( ) ) + " AND "
                                    + SearchItem.FIELD_UID + ":" + strWebappNameEscaped + SolrConstants.CONSTANT_UNDERSCORE + SolrConstants.CONSTANT_WILDCARD );
                }
                else
                {
                    // delete all index linked to uid. We get the uid of the resource to prefix it like we do during the indexation
                    SOLR_SERVER.deleteByQuery( SearchItem.FIELD_UID + ":" + strWebappNameEscaped + SolrConstants.CONSTANT_UNDERSCORE
                            + indexer.getResourceUid( action.getIdDocument( ), action.getTypeResource( ) ) );
                }

                _sbLogs.append( "Deleting " );
                _sbLogs.append( " #" );
                _sbLogs.append( action.getIdDocument( ) );
                _sbLogs.append( "\r\n" );
            }
            else
            {
                List<SolrItem> lstItems = indexer.getDocuments( action.getIdDocument( ) );

                if ( CollectionUtils.isNotEmpty( lstItems ) )
                {
                    for ( SolrItem item : lstItems )
                    {
                        indexSolrItem( action, item );
                    }
                }
            }

            SolrIndexerActionHome.remove( action.getIdAction( ), plugin );
        }
        catch( Exception e )
        {
            _sbLogs.append( "\r\n<strong>Action from indexer : " );
            _sbLogs.append( action.getIndexerName( ) );
            _sbLogs.append( " Action ID : " ).append( action.getIdAction( ) ).append( " - Document ID : " ).append( action.getIdDocument( ) );
            _sbLogs.append( LOG_ERROR );
            _sbLogs.append( e.getMessage( ) )
                    .append( ( e.getCause( ) != null ) ? ( " : " + e.getCause( ).getMessage( ) ) : SolrConstants.CONSTANT_EMPTY_STRING );
            _sbLogs.append( LOG_CLOSE_STRONG );
        }
    }

    private static void indexSolrItem( SolrIndexerAction action, SolrItem item ) throws SolrServerException, IOException
    {
        if ( ( action.getIdPortlet( ) == IndexationService.ALL_DOCUMENT )
                || ( ( item.getDocPortletId( ) != null ) && item.getDocPortletId( ).equals( item.getUid( ) + "&" + action.getIdPortlet( ) ) ) )
        {
            if ( action.getIdTask( ) == IndexerAction.TASK_CREATE )
            {
                _sbLogs.append( "Adding " );
            }
            else
                if ( action.getIdTask( ) == IndexerAction.TASK_MODIFY )
                {
                    _sbLogs.append( "Updating " );
                }

            SOLR_SERVER.add( solrItem2SolrInputDocument( item ) );
            SOLR_SERVER.commit( );

            _sbLogs.append( item.getType( ) );
            _sbLogs.append( " #" );
            _sbLogs.append( item.getUid( ) );
            _sbLogs.append( " - " );
            _sbLogs.append( item.getTitle( ) );
            _sbLogs.append( "\r\n" );
        }
    }

    /**
     * Build the message error of an exception
     * 
     * @param exception
     *            The exception
     * @return the message error of the exception
     */
    public static String buildErrorMessage( Exception exception )
    {
        StringBuilder sb = new StringBuilder( );
        sb.append( exception.getMessage( ) );

        if ( exception.getCause( ) != null )
        {
            sb.append( SolrConstants.CONSTANT_SPACE ).append( SolrConstants.CONSTANT_COLON ).append( SolrConstants.CONSTANT_SPACE );
            sb.append( exception.getCause( ).getMessage( ) );
        }

        return sb.toString( );
    }

    /**
     * Returns the list of all dynamic fields.
     * 
     * @return the list of all additional fields
     */
    public static List<Field> getAdditionalFields( )
    {
        List<Field> lstFields = new ArrayList<>( );
        for ( SolrIndexer solrIndexer : INDEXERS )
        {
            List<Field> lstAdditionalFields = solrIndexer.getAdditionalFields( );

            if ( lstAdditionalFields != null )
            {
                lstFields.addAll( lstAdditionalFields );
            }
        }

        return lstFields;
    }

    /**
     * Adds the exception into the buffer and the StringBuffer
     * 
     * @param exception
     *            Exception to report
     * @param sbLogs
     *            StringBuilder to write to
     */
    private static void printIndexMessage( Exception exception, StringBuilder sbLogs )
    {
        sbLogs.append( LOG_ERROR );
        sbLogs.append( exception.getMessage( ) );

        if ( exception.getCause( ) != null )
        {
            sbLogs.append( " : " );
            sbLogs.append( exception.getCause( ).getMessage( ) );
        }

        sbLogs.append( LOG_CLOSE_STRONG );
        AppLogService.error( exception.getMessage( ), exception );
    }

    /**
     * Find the indexer of the resource parameter
     * 
     * @param strResourceName
     *            the name of the resource to index
     * @return the indexer of the resource
     */
    private static SolrIndexer findSolrIndexer( String strResourceName )
    {
        for ( SolrIndexer indexer : INDEXERS )
        {
            List<String> lstResources = indexer.getResourcesName( );

            if ( ( lstResources != null ) && lstResources.contains( strResourceName ) )
            {
                return indexer;
            }
        }

        return null;
    }

    /**
     * Convert a {@link SolrItem} object into a {@link SolrInputDocument} object
     * 
     * @param solrItem
     *            the item to convert
     * @return A {@link SolrInputDocument} object corresponding to the item parameter
     */
    private static SolrInputDocument solrItem2SolrInputDocument( SolrItem solrItem )
    {
        SolrInputDocument solrInputDocument = new SolrInputDocument( );
        String strWebappName = getWebAppName( );

        // Prefix the uid by the name of the site. Without that, it is necessary imposible to index two resources of two different sites with the same
        // identifier
        solrInputDocument.addField( SearchItem.FIELD_UID, strWebappName + SolrConstants.CONSTANT_UNDERSCORE + solrItem.getUid( ) );
        solrInputDocument.addField( SearchItem.FIELD_DATE, solrItem.getDate( ) );
        solrInputDocument.addField( SearchItem.FIELD_TYPE, solrItem.getType( ) );
        solrInputDocument.addField( SearchItem.FIELD_SUMMARY, solrItem.getSummary( ) );
        solrInputDocument.addField( SearchItem.FIELD_TITLE, solrItem.getTitle( ) );
        solrInputDocument.addField( SolrItem.FIELD_SITE, solrItem.getSite( ) );
        solrInputDocument.addField( SearchItem.FIELD_ROLE, solrItem.getRole( ) );
        solrInputDocument.addField( SolrItem.FIELD_XML_CONTENT, solrItem.getXmlContent( ) );
        solrInputDocument.addField( SearchItem.FIELD_URL, solrItem.getUrl( ) );
        solrInputDocument.addField( SolrItem.FIELD_HIERATCHY_DATE, solrItem.getHieDate( ) );
        solrInputDocument.addField( SolrItem.FIELD_CATEGORIE, solrItem.getCategorie( ) );
        solrInputDocument.addField( SolrItem.FIELD_CONTENT, solrItem.getContent( ) );
        solrInputDocument.addField( SolrItem.FIELD_FILE_CONTENT, solrItem.getFileContent( ) );
        solrInputDocument.addField( SearchItem.FIELD_DOCUMENT_PORTLET_ID, solrItem.getDocPortletId( ) );

        // Add the dynamic fields
        // They must be declared into the schema.xml of the solr server
        Map<String, Object> mapDynamicFields = solrItem.getDynamicFields( );

        for ( Entry<String, Object> dynamicField : mapDynamicFields.entrySet( ) )
        {
            solrInputDocument.addField( dynamicField.getKey( ), dynamicField.getValue( ) );
        }

        return solrInputDocument;
    }

    /**
     * Initialize the indexers List.
     * 
     * @return the indexers List
     */
    private static List<SolrIndexer> initIndexersList( )
    {
        return SpringContextService.getBeansOfType( SolrIndexer.class );
    }

    /**
     * Return the url of the Root of the webapp
     * 
     * @return strBase the webapp url
     */
    public static String getRootUrl( )
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
        strBaseUrl = StringUtils.isBlank( strBaseUrl ) ? "" : strBaseUrl;
        return strBaseUrl;
    }

    /**
     * Return the url of the Portal of the webapp (eg http://host/WEBAPP/jsp/site/Portal.jsp)
     * 
     * @return strBase the webapp url
     */
    public static String getBaseUrl( )
    {
        String strBaseUrl = getRootUrl( );

        return strBaseUrl + AppPathService.getPortalUrl( );
    }

    /**
     * Return the name of the webapp
     * 
     * @return the name of the webapp
     */
    public static String getWebAppName( )
    {
        return AppPropertiesService.getProperty( PROPERTY_SITE );
    }

    /**
     * Del sorl index related to this site.
     * 
     * @return log of what appended
     */
    public static synchronized String processDel( )
    {
        // String buffer for building the response page
        _sbLogs = new StringBuilder( );
        String strSite = AppPropertiesService.getProperty( PROPERTY_SITE );

        _sbLogs.append( "Delete site : " + strSite + "\r\n" );
        AppLogService.info( _sbLogs.toString( ) );

        try
        {
            SOLR_SERVER.deleteByQuery( SolrItem.FIELD_SITE + ":\"" + strSite + "\"" );
            SOLR_SERVER.commit( );
            SOLR_SERVER.optimize( );
        }
        catch( Exception e )
        {
            AppLogService.error( "Erreur lors de la suppression de l'index solr", e );
            _sbLogs.append( ( e.getCause( ) != null ? e.getCause( ).toString( ) : e.toString( ) ) + "\r\n" );
        }

        return _sbLogs.toString( );
    }

    public static StringBuilder getSbLogs( )
    {
        return _sbLogs;
    }
}
