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
    private static final String SQL_QUERY_SELECT = "SELECT id_field, name, label, type, description, is_facet, enable_facet, is_sort, enable_sort, default_sort FROM solr_fields WHERE id_field = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO solr_fields ( id_field, name, label, type, description, is_facet, enable_facet, is_sort, enable_sort, default_sort ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM solr_fields WHERE id_field = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE solr_fields SET id_field = ?, name = ?, label = ?, type = ?, description = ?, is_facet = ?, enable_facet = ?, is_sort = ?, enable_sort = ?, default_sort = ? WHERE id_field = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_field, name, label, type, description, is_facet, enable_facet, is_sort, enable_sort, default_sort FROM solr_fields";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     * @param field instance of the Field object to insert
     * @param plugin The plugin
     */
    public void insert( Field field, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        field.setIdField( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, field.getIdField(  ) );
        daoUtil.setString( 2, field.getName(  ) );
        daoUtil.setString( 3, field.getLabel(  ) );
        daoUtil.setString( 4, field.getType(  ) );
        daoUtil.setString( 5, field.getDescription(  ) );
        daoUtil.setBoolean( 6, field.getIsFacet(  ) );
        daoUtil.setBoolean( 7, field.getEnableFacet(  ) );
        daoUtil.setBoolean( 8, field.getIsSort(  ) );
        daoUtil.setBoolean( 9, field.getEnableSort(  ) );
        daoUtil.setBoolean( 10, field.getDefaultSort(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of the field from the table
     * @param nId The identifier of the field
     * @param plugin The plugin
     * @return the instance of the Field
     */
    public Field load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        Field field = null;

        if ( daoUtil.next(  ) )
        {
            field = new Field(  );

            field.setIdField( daoUtil.getInt( 1 ) );
            field.setName( daoUtil.getString( 2 ) );
            field.setLabel( daoUtil.getString( 3 ) );
            field.setType( daoUtil.getString( 4 ) );
            field.setDescription( daoUtil.getString( 5 ) );
            field.setIsFacet( daoUtil.getBoolean( 6 ) );
            field.setEnableFacet( daoUtil.getBoolean( 7 ) );
            field.setIsSort( daoUtil.getBoolean( 8 ) );
            field.setEnableSort( daoUtil.getBoolean( 9 ) );
            field.setDefaultSort( daoUtil.getBoolean( 10 ) );
        }

        daoUtil.free(  );

        return field;
    }

    /**
     * Delete a record from the table
     * @param nFieldId The identifier of the field
     * @param plugin The plugin
     */
    public void delete( int nFieldId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nFieldId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     * @param field The reference of the field
     * @param plugin The plugin
     */
    public void store( Field field, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, field.getIdField(  ) );
        daoUtil.setString( 2, field.getName(  ) );
        daoUtil.setString( 3, field.getLabel(  ) );
        daoUtil.setString( 4, field.getType(  ) );
        daoUtil.setString( 5, field.getDescription(  ) );
        daoUtil.setBoolean( 6, field.getIsFacet(  ) );
        daoUtil.setBoolean( 7, field.getEnableFacet(  ) );
        daoUtil.setBoolean( 8, field.getIsSort(  ) );
        daoUtil.setBoolean( 9, field.getEnableSort(  ) );
        daoUtil.setBoolean( 10, field.getDefaultSort(  ) );
        daoUtil.setInt( 11, field.getIdField(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the fields and returns them as a List
     * @param plugin The plugin
     * @return The List which contains the data of all the fields
     */
    public List<Field> selectFieldsList( Plugin plugin )
    {
        List<Field> fieldList = new ArrayList<Field>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Field field = new Field(  );

            field.setIdField( daoUtil.getInt( 1 ) );
            field.setName( daoUtil.getString( 2 ) );
            field.setLabel( daoUtil.getString( 3 ) );
            field.setType( daoUtil.getString( 4 ) );
            field.setDescription( daoUtil.getString( 5 ) );
            field.setIsFacet( daoUtil.getBoolean( 6 ) );
            field.setEnableFacet( daoUtil.getBoolean( 7 ) );
            field.setIsSort( daoUtil.getBoolean( 8 ) );
            field.setEnableSort( daoUtil.getBoolean( 9 ) );
            field.setDefaultSort( daoUtil.getBoolean( 10 ) );

            fieldList.add( field );
        }

        daoUtil.free(  );

        return fieldList;
    }
}
