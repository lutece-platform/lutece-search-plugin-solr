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
package fr.paris.lutece.plugins.search.solr.business.facetintersection;

import java.util.List;

import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.test.LuteceTestCase;

public class FacetIntersectionBusinessTest extends LuteceTestCase
{

    public void testCRUD( )
    {
        Field f1 = new Field( );
        f1.setIdField( 1 );

        Field f2 = new Field( );
        f2.setIdField( 2 );

        FacetIntersection facetIntersection = new FacetIntersection( );
        facetIntersection.setField1( f1 );
        facetIntersection.setField2( f2 );

        FacetIntersectionHome.create( facetIntersection );

        List<FacetIntersection> list = FacetIntersectionHome.getFacetIntersectionsList( );
        assertEquals( 1, list.size( ) );

        FacetIntersectionHome.remove( f1.getIdField( ), f2.getIdField( ) );
        list = FacetIntersectionHome.getFacetIntersectionsList( );
        assertEquals( 0, list.size( ) );
    }
}
