/*
 * Copyright (c) 2002-2014, Mairie de Paris
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


/**
 *
 * A SolrHighlight is a sample sentence which highlights searched words.
 *
 */
public class SolrHighlights
{
    private Map<String, Map<String, List<String>>> _map;

    /**
     * Creates a new SolrHighlights
     * @param map the highlight map from SolrServer.
     */
    public SolrHighlights( Map<String, Map<String, List<String>>> map )
    {
        _map = map;
    }

    /**
     * Gets the map.
     * @return the map
     */
    public Map<String, Map<String, List<String>>> getMap(  )
    {
        return _map;
    }

    /**
     * Sets the map.
     * @param map the map.
     */
    public void setMap( Map<String, Map<String, List<String>>> map )
    {
        _map = map;
    }

    /**
     * Returns a SolrHighlight matching the searched strId.
     * @param strId the searched id
     * @return the SolrHighlight that matches the searched id, otherwise null.
     */
    public SolrHighlight getHighlights( String strId )
    {
        Map<String, List<String>> highlightMap = _map.get( strId );

        if ( highlightMap != null )
        {
            return new SolrHighlight( highlightMap );
        }

        return null;
    }
}
