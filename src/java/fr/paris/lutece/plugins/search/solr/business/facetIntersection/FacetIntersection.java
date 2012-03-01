/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.search.solr.business.facetIntersection;

import fr.paris.lutece.plugins.search.solr.business.field.Field;


/**
 * This is the business class for the object facetIntersection
 */
public class FacetIntersection
{
    // Variables declarations 
    private Field _field1;
    private Field _field2;

    /**
     * Returns the IdField1
     * @return The IdField1
     */
    public Field getField1(  )
    {
        return _field1;
    }

    /**
     * Sets the IdField1
     * @param nIdField1 The IdField1
     */
    public void setField1( Field field1 )
    {
        _field1 = field1;
    }

    /**
     * Returns the IdField2
     * @return The IdField2
     */
    public Field getField2(  )
    {
        return _field2;
    }

    /**
     * Sets the IdField2
     * @param nIdField2 The IdField2
     */
    public void setField2( Field field2 )
    {
        _field2 = field2;
    }
}
