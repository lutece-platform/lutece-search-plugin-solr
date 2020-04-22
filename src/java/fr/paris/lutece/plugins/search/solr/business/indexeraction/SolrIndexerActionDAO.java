/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.search.solr.business.indexeraction;

import fr.paris.lutece.portal.business.indexeraction.IndexerActionFilter;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Indexer Action objects
 */
public final class SolrIndexerActionDAO implements ISolrIndexerActionDAO
{
    // Constants
    public static final String CONSTANT_WHERE = " WHERE ";
    public static final String CONSTANT_AND = " AND ";
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_action ) FROM solr_indexer_action";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_action,id_document,id_task,type_ressource, id_portlet"
            + " FROM solr_indexer_action WHERE id_action = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO solr_indexer_action( id_action,id_document,id_task ,type_ressource,id_portlet)"
            + " VALUES(?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM solr_indexer_action WHERE id_action = ? ";
    private static final String SQL_QUERY_TRUNCATE = "DELETE FROM solr_indexer_action  ";
    private static final String SQL_QUERY_UPDATE = "UPDATE solr_indexer_action SET id_action=?,id_document=?,id_task=?,type_ressource=?,id_portlet=? WHERE id_action = ? ";
    private static final String SQL_QUERY_SELECT = "SELECT id_action,id_document,id_task,type_ressource,id_portlet" + " FROM solr_indexer_action  ";
    private static final String SQL_FILTER_ID_TASK = " id_task = ? ";

    /**
     * {@inheritDoc}
     */
    public int newPrimaryKey( Plugin plugin )
    {
        int nKey = 1;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                nKey = daoUtil.getInt( 1 ) + 1;
            }
        }
        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void insert( SolrIndexerAction indexerAction, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            int i = 0;
            indexerAction.setIdAction( newPrimaryKey( plugin ) );
            daoUtil.setInt( ++i, indexerAction.getIdAction( ) );
            daoUtil.setString( ++i, indexerAction.getIdDocument( ) );
            daoUtil.setInt( ++i, indexerAction.getIdTask( ) );
            daoUtil.setString( ++i, indexerAction.getTypeResource( ) );
            daoUtil.setInt( ++i, indexerAction.getIdPortlet( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    public SolrIndexerAction load( int nId, Plugin plugin )
    {
        SolrIndexerAction indexerAction = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int i = 0;
                indexerAction = new SolrIndexerAction( );
                indexerAction.setIdAction( daoUtil.getInt( ++i ) );
                indexerAction.setIdDocument( daoUtil.getString( ++i ) );
                indexerAction.setIdTask( daoUtil.getInt( ++i ) );
                indexerAction.setTypeResource( daoUtil.getString( ++i ) );
                indexerAction.setIdPortlet( daoUtil.getInt( ++i ) );
            }
        }
        return indexerAction;
    }

    /**
     * {@inheritDoc}
     */
    public void delete( int nId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteAll( Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_TRUNCATE, plugin ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void store( SolrIndexerAction indexerAction, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int i = 0;
            daoUtil.setInt( ++i, indexerAction.getIdAction( ) );
            daoUtil.setString( ++i, indexerAction.getIdDocument( ) );
            daoUtil.setInt( ++i, indexerAction.getIdTask( ) );
            daoUtil.setString( ++i, indexerAction.getTypeResource( ) );
            daoUtil.setInt( ++i, indexerAction.getIdPortlet( ) );
            daoUtil.setInt( ++i, indexerAction.getIdAction( ) );
            
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<SolrIndexerAction> selectList( IndexerActionFilter filter, Plugin plugin )
    {
        List<SolrIndexerAction> indexerActionList = new ArrayList<>( );
        List<String> listStrFilter = new ArrayList<>( );

        if ( filter.containsIdTask( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_TASK );
        }

        String strSQL = buildRequestWithFilter( SQL_QUERY_SELECT, listStrFilter, null );

        try ( DAOUtil daoUtil = new DAOUtil( strSQL, plugin ) )
        {
            int nIndex = 1;

            if ( filter.containsIdTask( ) )
            {
                daoUtil.setInt( nIndex++, filter.getIdTask( ) );
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int i = 0;
                SolrIndexerAction indexerAction = new SolrIndexerAction( );
                indexerAction.setIdAction( daoUtil.getInt( ++i ) );
                indexerAction.setIdDocument( daoUtil.getString( ++i ) );
                indexerAction.setIdTask( daoUtil.getInt( ++i ) );
                indexerAction.setTypeResource( daoUtil.getString( ++i ) );
                indexerAction.setIdPortlet( daoUtil.getInt( ++i ) );
                indexerActionList.add( indexerAction );
            }
        }
        return indexerActionList;
    }

    /**
     * {@inheritDoc}
     */
    public List<SolrIndexerAction> selectList( Plugin plugin )
    {
        List<SolrIndexerAction> indexerActionList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                int i = 0;
                SolrIndexerAction indexerAction = new SolrIndexerAction( );
                indexerAction.setIdAction( daoUtil.getInt( ++i ) );
                indexerAction.setIdDocument( daoUtil.getString( ++i ) );
                indexerAction.setIdTask( daoUtil.getInt( ++i ) );
                indexerAction.setTypeResource( daoUtil.getString( ++i ) );
                indexerAction.setIdPortlet( daoUtil.getInt( ++i ) );
                indexerActionList.add( indexerAction );
            }
        }
        return indexerActionList;
    }

    /**
     * Builds a query with filters placed in parameters
     * 
     * @param strSelect
     *            the select of the query
     * @param listStrFilter
     *            the list of filter to add in the query
     * @param strOrder
     *            the order by of the query
     * @return a query
     */
    public static String buildRequestWithFilter( String strSelect, List<String> listStrFilter, String strOrder )
    {
        StringBuilder strBuffer = new StringBuilder( );
        strBuffer.append( strSelect );

        int nCount = 0;

        for ( String strFilter : listStrFilter )
        {
            if ( ++nCount == 1 )
            {
                strBuffer.append( CONSTANT_WHERE );
            }

            strBuffer.append( strFilter );

            if ( nCount != listStrFilter.size( ) )
            {
                strBuffer.append( CONSTANT_AND );
            }
        }

        if ( strOrder != null )
        {
            strBuffer.append( strOrder );
        }

        return strBuffer.toString( );
    }
}
