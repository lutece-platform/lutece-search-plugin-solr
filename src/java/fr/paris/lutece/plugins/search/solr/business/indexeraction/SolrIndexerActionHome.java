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
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for IndexerAction objects
 */
public final class SolrIndexerActionHome
{
    // Static variable pointed at the DAO instance
    private static ISolrIndexerActionDAO _dao = SpringContextService.getBean( "solrIndexerActionDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private SolrIndexerActionHome( )
    {
    }

    /**
     * Creation of an instance of Indexer Action
     *
     * @param indexerAction
     *            The instance of the indexer action which contains the informations to store
     * @param plugin
     *            The plugin
     */
    public static void create( SolrIndexerAction indexerAction, Plugin plugin )
    {
        _dao.insert( indexerAction, plugin );
    }

    /**
     * Update of the indexerAction which is specified in parameter
     *
     * @param indexerAction
     *            The instance of the indexerAction which contains the informations to update
     * @param plugin
     *            The plugin
     */
    public static void update( SolrIndexerAction indexerAction, Plugin plugin )
    {
        _dao.store( indexerAction, plugin );
    }

    /**
     * Remove the indexerAction whose identifier is specified in parameter
     *
     * @param nId
     *            The IndexerActionId
     * @param plugin
     *            The plugin
     */
    public static void remove( int nId, Plugin plugin )
    {
        _dao.delete( nId, plugin );
    }

    /**
     * Remove the indexerAction whose identifier is specified in parameter
     *
     * @param plugin
     *            The plugin
     */
    public static void removeAll( Plugin plugin )
    {
        _dao.deleteAll( plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a IndexerAction whose identifier is specified in parameter
     *
     * @param nKey
     *            The indexerAction primary key
     * @param plugin
     *            The plugin
     * @return an instance of IndexerAction
     */
    public static SolrIndexerAction findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Loads the data of all the IndexerAction who verify the filter and returns them in a list
     *
     * @param filter
     *            the filter
     * @param plugin
     *            The plugin
     * @return the list which contains the data of all the indexerAction
     */
    public static List<SolrIndexerAction> getList( IndexerActionFilter filter, Plugin plugin )
    {
        return _dao.selectList( filter, plugin );
    }

    /**
     * Loads the data of all the IndexerAction
     *
     * @param plugin
     *            The plugin
     * @return the list which contains the data of all the indexerAction
     */
    public static List<SolrIndexerAction> getList( Plugin plugin )
    {
        return _dao.selectList( plugin );
    }
}
