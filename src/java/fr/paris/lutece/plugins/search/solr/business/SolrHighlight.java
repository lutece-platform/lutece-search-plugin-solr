/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.search.solr.business;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * SolrHighlight
 *
 */
public class SolrHighlight
{
    private Map<String, List<String>> _map;

    /**
     * Create a new instance of SolrHighlight
     * @param map the highlights from SolrServer
     */
    public SolrHighlight( Map<String, List<String>> map )
    {
        _map = map;
    }

    /**
     * Returns the map
     * @return the map
     */
    public Map<String, List<String>> getMap(  )
    {
        return _map;
    }

    /**
     * Sets the map
     * @param map the map
     */
    public void setMap( Map<String, List<String>> map )
    {
        _map = map;
    }

    /**
     * Returns the fields names
     * @return the fields names
     */
    public Set<String> getFieldsNames(  )
    {
        return _map.keySet(  );
    }

    /**
     * Return the value of the given field name
     * @param strFieldName the field name
     * @return the value of the given field name
     */
    public List<String> getValues( String strFieldName )
    {
        return _map.get( strFieldName );
    }
}
