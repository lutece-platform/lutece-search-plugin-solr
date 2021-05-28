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
package fr.paris.lutece.plugins.search.solr.business.field;

import java.util.List;

import fr.paris.lutece.test.LuteceTestCase;

public class FieldBusinessTest extends LuteceTestCase
{

    public void testCRUD( )
    {
        Field field = new Field( );
        field.setName( "strName" );
        field.setDescription( "strDescription" );
        field.setLabel( "strLabel" );
        field.setEnableFacet( true );
        field.setEnableSort( true );
        field.setFacetMincount( 22 );
        field.setIsFacet( true );
        field.setIsSort( true );
        field.setOperator( "strOperator" );
        field.setWeight( 5D );
        field.setDefaultSort( true );

        FieldHome.create( field );
        Field loaded = FieldHome.findByPrimaryKey( field.getIdField( ) );
        assertEquals( field.getName( ), loaded.getName( ) );
        assertEquals( field.getDescription( ), loaded.getDescription( ) );
        assertEquals( field.getLabel( ), loaded.getLabel( ) );
        assertEquals( field.getEnableFacet( ), loaded.getEnableFacet( ) );
        assertEquals( field.getEnableSort( ), loaded.getEnableSort( ) );
        assertEquals( field.getFacetMincount( ), loaded.getFacetMincount( ) );
        assertEquals( field.getIsFacet( ), loaded.getIsFacet( ) );
        assertEquals( field.getIsSort( ), loaded.getIsSort( ) );
        assertEquals( field.getOperator( ), loaded.getOperator( ) );
        assertEquals( field.getWeight( ), loaded.getWeight( ) );
        assertEquals( field.getDefaultSort( ), loaded.getDefaultSort( ) );

        field.setName( "strName2" );
        field.setDescription( "strDescription2" );
        field.setLabel( "strLabel2" );
        field.setEnableFacet( false );
        field.setEnableSort( false );
        field.setFacetMincount( 20 );
        field.setIsFacet( false );
        field.setIsSort( false );
        field.setOperator( "strOperator" );
        field.setWeight( 3D );
        field.setDefaultSort( false );

        FieldHome.update( field );
        loaded = FieldHome.findByPrimaryKey( field.getIdField( ) );
        assertEquals( field.getName( ), loaded.getName( ) );
        assertEquals( field.getDescription( ), loaded.getDescription( ) );
        assertEquals( field.getLabel( ), loaded.getLabel( ) );
        assertEquals( field.getEnableFacet( ), loaded.getEnableFacet( ) );
        assertEquals( field.getEnableSort( ), loaded.getEnableSort( ) );
        assertEquals( field.getFacetMincount( ), loaded.getFacetMincount( ) );
        assertEquals( field.getIsFacet( ), loaded.getIsFacet( ) );
        assertEquals( field.getIsSort( ), loaded.getIsSort( ) );
        assertEquals( field.getOperator( ), loaded.getOperator( ) );
        assertEquals( field.getWeight( ), loaded.getWeight( ) );
        assertEquals( field.getDefaultSort( ), loaded.getDefaultSort( ) );

        List<Field> list = FieldHome.getFieldsList( );
        // 8 fields in the init db script
        assertEquals( 9, list.size( ) );

        FieldHome.remove( field.getIdField( ) );

        loaded = FieldHome.findByPrimaryKey( field.getIdField( ) );
        assertNull( loaded );
    }
}
