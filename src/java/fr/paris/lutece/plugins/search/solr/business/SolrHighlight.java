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
package fr.paris.lutece.plugins.search.solr.business;

import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import freemarker.core.HTMLOutputFormat;
import freemarker.core.TemplateHTMLOutputModel;

/**
 *
 * SolrHighlight
 *
 */
public class SolrHighlight
{
    /** Property of the marker Solr writes before a highlighted term (hl.simple.pre) */
    public static final String PROPERTY_HIGHLIGHT_PRE = "solr.highlight.pre";
    /** Property of the marker Solr writes after a highlighted term (hl.simple.post) */
    public static final String PROPERTY_HIGHLIGHT_POST = "solr.highlight.post";
    // Solr's own markers, used when the properties are not set
    private static final String DEFAULT_HIGHLIGHT_PRE = "<em>";
    private static final String DEFAULT_HIGHLIGHT_POST = "</em>";

    private Map<String, List<String>> _map;
    private final String _strPre;
    private final String _strPost;

    /**
     * Create a new instance of SolrHighlight
     * 
     * @param map
     *            the highlights from SolrServer
     */
    public SolrHighlight( Map<String, List<String>> map )
    {
        this( map, null, null );
    }

    /**
     * Create a new instance of SolrHighlight whose highlighting markers are known
     * 
     * @param map
     *            the highlights from SolrServer
     * @param strPre
     *            the marker Solr writes before a highlighted term, null for the property solr.highlight.pre
     * @param strPost
     *            the marker Solr writes after a highlighted term, null for the property solr.highlight.post
     */
    public SolrHighlight( Map<String, List<String>> map, String strPre, String strPost )
    {
        _map = map;
        _strPre = strPre;
        _strPost = strPost;
    }

    /**
     * Returns the map
     * 
     * @return the map
     */
    public Map<String, List<String>> getMap( )
    {
        return _map;
    }

    /**
     * Sets the map
     * 
     * @param map
     *            the map
     */
    public void setMap( Map<String, List<String>> map )
    {
        _map = map;
    }

    /**
     * Returns the fields names
     * 
     * @return the fields names
     */
    public Set<String> getFieldsNames( )
    {
        return _map.keySet( );
    }

    /**
     * Return the value of the given field name
     * 
     * @param strFieldName
     *            the field name
     * @return the value of the given field name
     */
    public List<String> getValues( String strFieldName )
    {
        return _map.get( strFieldName );
    }

    /**
     * Returns the highlighted extracts as HTML markup, for the templates: the text of the document is escaped, only the
     * highlighting markers configured by the properties solr.highlight.pre and solr.highlight.post stay HTML. The raw
     * Solr snippets of {@link #getMap( )} are not HTML-safe: they hold the text of the indexed document as is. The markup
     * is built on each call, never kept: it is not Serializable.
     * 
     * @return the extracts of each field, as markup
     */
    public Map<String, List<TemplateHTMLOutputModel>> getMarkupMap( )
    {
        String strPre = ( _strPre != null ) ? _strPre : AppPropertiesService.getProperty( PROPERTY_HIGHLIGHT_PRE, DEFAULT_HIGHLIGHT_PRE );
        String strPost = ( _strPost != null ) ? _strPost : AppPropertiesService.getProperty( PROPERTY_HIGHLIGHT_POST, DEFAULT_HIGHLIGHT_POST );

        return getMarkupMap( strPre, strPost );
    }

    /**
     * Returns the highlighted extracts as HTML markup, the text of the document escaped and the given markers kept.
     * 
     * @param strPre
     *            the marker written before a highlighted term
     * @param strPost
     *            the marker written after a highlighted term
     * @return the extracts of each field, as markup
     */
    public Map<String, List<TemplateHTMLOutputModel>> getMarkupMap( String strPre, String strPost )
    {
        Map<String, List<TemplateHTMLOutputModel>> mapMarkup = new LinkedHashMap<>( );

        if ( _map != null )
        {
            for ( Map.Entry<String, List<String>> entry : _map.entrySet( ) )
            {
                List<TemplateHTMLOutputModel> listMarkup = new ArrayList<>( );

                if ( entry.getValue( ) != null )
                {
                    entry.getValue( ).forEach( strSnippet -> listMarkup.add( HtmlMarkup.of( toHtml( strSnippet, strPre, strPost ) ) ) );
                }

                mapMarkup.put( entry.getKey( ), listMarkup );
            }
        }

        return mapMarkup;
    }

    /**
     * Escapes a Solr snippet as HTML text, then restores the highlighting markers.
     * 
     * @param strSnippet
     *            the snippet returned by Solr: the text of the document with the markers around the matched terms
     * @param strPre
     *            the marker written before a highlighted term
     * @param strPost
     *            the marker written after a highlighted term
     * @return the HTML of the snippet
     */
    public static String toHtml( String strSnippet, String strPre, String strPost )
    {
        if ( strSnippet == null )
        {
            return StringUtils.EMPTY;
        }

        String strHtml = HTMLOutputFormat.INSTANCE.escapePlainText( strSnippet );

        if ( StringUtils.isNotEmpty( strPre ) )
        {
            strHtml = strHtml.replace( HTMLOutputFormat.INSTANCE.escapePlainText( strPre ), strPre );
        }

        if ( StringUtils.isNotEmpty( strPost ) )
        {
            strHtml = strHtml.replace( HTMLOutputFormat.INSTANCE.escapePlainText( strPost ), strPost );
        }

        return strHtml;
    }
}
