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
package fr.paris.lutece.plugins.search.solr.business.field;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Field objects
 */
public final class FieldDAO implements IFieldDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_field ) FROM solr_fields";
    private static final String SQL_QUERY_SELECT = "SELECT id_field, name, label, description, is_facet, enable_facet, is_sort, enable_sort, default_sort, weight, facet_mincount, operator_type FROM solr_fields WHERE id_field = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO solr_fields ( id_field, name, label, description, is_facet, enable_facet, is_sort, enable_sort, default_sort, weight, facet_mincount, operator_type ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM solr_fields WHERE id_field = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE solr_fields SET id_field = ?, name = ?, label = ?, description = ?, is_facet = ?, enable_facet = ?, is_sort = ?, enable_sort = ?, default_sort = ?, weight = ?, facet_mincount = ?, operator_type = ? WHERE id_field = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_field, name, label, description, is_facet, enable_facet, is_sort, enable_sort, default_sort, weight, facet_mincount, operator_type  FROM solr_fields";

    /**
     * Generates a new primary key
     * 
     * @param plugin
     *            The Plugin
     * @return The new primary key
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
     * Insert a new record in the table.
     * 
     * @param field
     *            instance of the Field object to insert
     * @param plugin
     *            The plugin
     */
    public void insert( Field field, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            field.setIdField( newPrimaryKey( plugin ) );

            int i = 0;
            daoUtil.setInt( ++i, field.getIdField( ) );
            daoUtil.setString( ++i, field.getName( ) );
            daoUtil.setString( ++i, field.getLabel( ) );
            daoUtil.setString( ++i, field.getDescription( ) );
            daoUtil.setBoolean( ++i, field.getIsFacet( ) );
            daoUtil.setBoolean( ++i, field.getEnableFacet( ) );
            daoUtil.setBoolean( ++i, field.getIsSort( ) );
            daoUtil.setBoolean( ++i, field.getEnableSort( ) );
            daoUtil.setBoolean( ++i, field.getDefaultSort( ) );
            daoUtil.setDouble( ++i, field.getWeight( ) );
            daoUtil.setInt( ++i, field.getFacetMincount( ) );
            daoUtil.setString( ++i, field.getOperator( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the data of the field from the table
     * 
     * @param nId
     *            The identifier of the field
     * @param plugin
     *            The plugin
     * @return the instance of the Field
     */
    public Field load( int nId, Plugin plugin )
    {
        Field field = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                field = new Field( );

                int i = 1;
                field.setIdField( daoUtil.getInt( i++ ) );
                field.setName( daoUtil.getString( i++ ) );
                field.setLabel( daoUtil.getString( i++ ) );
                field.setDescription( daoUtil.getString( i++ ) );
                field.setIsFacet( daoUtil.getBoolean( i++ ) );
                field.setEnableFacet( daoUtil.getBoolean( i++ ) );
                field.setIsSort( daoUtil.getBoolean( i++ ) );
                field.setEnableSort( daoUtil.getBoolean( i++ ) );
                field.setDefaultSort( daoUtil.getBoolean( i++ ) );
                field.setWeight( daoUtil.getDouble( i++ ) );
                field.setFacetMincount( daoUtil.getInt( i++ ) );
                field.setOperator( daoUtil.getString( i ) );

            }

        }

        return field;
    }

    /**
     * Delete a record from the table
     * 
     * @param nFieldId
     *            The identifier of the field
     * @param plugin
     *            The plugin
     */
    public void delete( int nFieldId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nFieldId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the record in the table
     * 
     * @param field
     *            The reference of the field
     * @param plugin
     *            The plugin
     */
    public void store( Field field, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int i = 0;
            daoUtil.setInt( ++i, field.getIdField( ) );
            daoUtil.setString( ++i, field.getName( ) );
            daoUtil.setString( ++i, field.getLabel( ) );
            daoUtil.setString( ++i, field.getDescription( ) );
            daoUtil.setBoolean( ++i, field.getIsFacet( ) );
            daoUtil.setBoolean( ++i, field.getEnableFacet( ) );
            daoUtil.setBoolean( ++i, field.getIsSort( ) );
            daoUtil.setBoolean( ++i, field.getEnableSort( ) );
            daoUtil.setBoolean( ++i, field.getDefaultSort( ) );
            daoUtil.setDouble( ++i, field.getWeight( ) );
            daoUtil.setInt( ++i, field.getFacetMincount( ) );
            daoUtil.setString( ++i, field.getOperator( ) );
            daoUtil.setInt( ++i, field.getIdField( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the data of all the fields and returns them as a List
     * 
     * @param plugin
     *            The plugin
     * @return The List which contains the data of all the fields
     */
    public List<Field> selectFieldsList( Plugin plugin )
    {
        List<Field> fieldList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                Field field = new Field( );

                int i = 0;
                field.setIdField( daoUtil.getInt( ++i ) );
                field.setName( daoUtil.getString( ++i ) );
                field.setLabel( daoUtil.getString( ++i ) );
                field.setDescription( daoUtil.getString( ++i ) );
                field.setIsFacet( daoUtil.getBoolean( ++i ) );
                field.setEnableFacet( daoUtil.getBoolean( ++i ) );
                field.setIsSort( daoUtil.getBoolean( ++i ) );
                field.setEnableSort( daoUtil.getBoolean( ++i ) );
                field.setDefaultSort( daoUtil.getBoolean( ++i ) );
                field.setWeight( daoUtil.getDouble( ++i ) );
                field.setFacetMincount( daoUtil.getInt( ++i ) );
                field.setOperator( daoUtil.getString( i ) );
                fieldList.add( field );
            }
        }
        return fieldList;
    }
}
