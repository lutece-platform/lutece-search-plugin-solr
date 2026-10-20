/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.search.solr.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.search.solr.business.SolrHighlight;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchResult;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Renders the plugin-solr pages with HTML auto-escaping (LUT-33153): the searched term and the other request parameters
 * written back into the search results page are escaped in the text and the attributes and URL-encoded in the links; a
 * result URL goes through safeUrl(); the highlighted extracts keep only the highlighting markers as HTML, the text of the
 * document is escaped; the map points are attributes read by the script; the indexing logs are text. The core macros the
 * templates call are replaced by stand-ins: only the FreeMarker engine is needed.
 */
public class SolrTemplatesRenderTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE_RESULTS = "skin/search/solr_search_results.html";
    private static final String TEMPLATE_HEADER = "skin/site/solr_page_header_home.html";
    private static final String TEMPLATE_LOGS = "admin/search/solr_search_indexation_logs.html";

    /**
     * Stand-ins of the core macros and functions the templates call. safeUrl keeps a relative or http(s) URL and returns
     * the fallback for any other scheme, as the core function does.
     */
    private static final String MACROS = "macros.ftl";
    private static final String MACROS_SOURCE = "<#macro cPagination rest...></#macro>"
            + "<#macro pageContainer rest...><#nested></#macro><#macro pageColumn rest...><#nested></#macro>"
            + "<#macro pageHeader title='' rest...><h1>${title}</h1><#nested></#macro>"
            + "<#macro p rest...><p><#nested></p></#macro><#macro icon rest...></#macro>"
            + "<#function safeUrl url fallback='#'><#if url?matches( '(?i)^\\\\s*(https?:|/|[^:/?#]*([/?#]|$)).*', 's' )><#return url /></#if><#return fallback /></#function>";

    private static final String HOSTILE_QUERY = "l'école & <script>alert(1)</script>\"x";
    private static final String HOSTILE_SORT = "title\"><script>alert(5)</script>";
    private static final String HOSTILE_CONF = "x\"><script>alert(7)</script>&y=1";
    private static final String HOSTILE_FACET = "categorie:Actualités <b>x</b>";

    /**
     * Renders a template of the plugin with the core configuration of the output format.
     *
     * @param strTemplate
     *            the template path
     * @param model
     *            the model
     * @return the HTML
     * @throws IOException
     *             if the template cannot be read
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String render( String strTemplate, Map<String, Object> model ) throws IOException, TemplateException
    {
        StringTemplateLoader macros = new StringTemplateLoader( );
        macros.putTemplate( MACROS, MACROS_SOURCE );
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                macros, new FileTemplateLoader( new File( TEMPLATES_ROOT ) )
        } ) );
        cfg.addAutoInclude( MACROS );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( strTemplate ).process( model, writer );

        return writer.toString( );
    }

    /**
     * A search result.
     *
     * @param strUrl
     *            the URL of the document
     * @param highlight
     *            the highlighted extracts, or null
     * @return the result
     */
    private static SolrSearchResult result( String strUrl, SolrHighlight highlight )
    {
        SolrSearchResult result = new SolrSearchResult( );
        result.setId( "site_12_PAGE" );
        result.setUrl( strUrl );
        result.setTitle( "Piscine <i>Pontoise</i>" );
        result.setSummary( "Résumé" );
        result.setHighlight( highlight );

        return result;
    }

    /**
     * The model of the search results page as SolrSearchApp builds it, the request parameters given.
     *
     * @param strQuery
     *            the searched term
     * @param listResults
     *            the results
     * @return the model
     */
    private static Map<String, Object> model( String strQuery, List<SolrSearchResult> listResults )
    {
        Map<String, Object> paginator = new HashMap<>( );
        paginator.put( "itemsCount", listResults.size( ) );
        paginator.put( "rangeMin", 1 );
        paginator.put( "rangeMax", listResults.size( ) );

        Map<String, Object> conf = new HashMap<>( );
        conf.put( "extraMappingQuery", Boolean.FALSE );

        Map<String, Object> model = new HashMap<>( );
        model.put( "fullUrl", "http://localhost:8080/lutece/jsp/site/Portal.jsp" );
        model.put( "nb_items_per_page", 10 );
        model.put( "query", strQuery );
        model.put( "sort_name", "title" );
        model.put( "sort_order", "asc" );
        model.put( "facets_list", new ArrayList<String>( ) );
        model.put( "conf", conf );
        model.put( "error", "" );
        model.put( "paginator", paginator );
        model.put( "results_list", listResults );

        return model;
    }

    /**
     * The searched term, the sort, the configuration code and the facet queries come from the request: they are escaped
     * in the form fields and URL-encoded in the links, never written as HTML.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testRequestParametersAreEscaped( ) throws Exception
    {
        Map<String, Object> model = model( HOSTILE_QUERY, List.of( result( "jsp/site/Portal.jsp?page_id=12", null ) ) );
        model.put( "sort_name", HOSTILE_SORT );
        model.put( "conf_user_query", HOSTILE_CONF );
        model.put( "facets_list", List.of( HOSTILE_FACET ) );

        String strHtml = render( TEMPLATE_RESULTS, model );

        assertFalse( strHtml.contains( "<script>alert" ), strHtml );
        assertFalse( strHtml.contains( "<b>x</b>" ), strHtml );
        assertTrue( strHtml.contains( "name=\"query\" size=\"35\" value=\"l&#39;école &amp; &lt;script&gt;alert(1)&lt;/script&gt;&quot;x\"" ), strHtml );
        assertTrue( strHtml.contains( "name=\"sort_name\" value=\"title&quot;&gt;&lt;script&gt;alert(5)&lt;/script&gt;\"" ), strHtml );
        assertTrue( strHtml.contains( "name=\"conf\" value=\"x&quot;&gt;&lt;script&gt;alert(7)&lt;/script&gt;&amp;y=1\"" ), strHtml );
        assertTrue( strHtml.contains( "name=\"fq\" value=\"categorie:Actualités &lt;b&gt;x&lt;/b&gt;\"" ), strHtml );
        // in the links: URL-encoded, then HTML-escaped (the ' that ?url keeps)
        assertTrue( strHtml.contains( "&query=l&#39;%C3%A9cole%20%26%20%3Cscript%3Ealert(1)%3C%2Fscript%3E%22x" ), strHtml );
        assertTrue( strHtml.contains( "&sort_name=title%22%3E%3Cscript%3Ealert(5)%3C%2Fscript%3E" ), strHtml );
        assertTrue( strHtml.contains( "&amp;conf=x%22%3E%3Cscript%3Ealert(7)%3C%2Fscript%3E%26y%3D1" ), strHtml );
        assertTrue( strHtml.contains( "&amp;fq=categorie%3AActualit%C3%A9s%20%3Cb%3Ex%3C%2Fb%3E" ), strHtml );
    }

    /**
     * The base URL of the autocomplete script is a JavaScript string: a quote cannot end it.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testAutocompleteUrlIsAJavaScriptString( ) throws Exception
    {
        Map<String, Object> model = model( "horaires", List.of( ) );
        model.put( "fullUrl", "http://localhost:8080/lutece/jsp/site/Portal.jsp';alert(1);'" );

        String strHtml = render( TEMPLATE_RESULTS, model );

        assertTrue( strHtml.contains( "const url = 'http://localhost:8080/lutece/jsp/site/Portal.jsp\\';alert(1);\\'?page=search-solr" ), strHtml );
    }

    /**
     * The URL of a result is data of the index: a javascript: URL is replaced by safeUrl(), a relative one is kept; the
     * title is escaped.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testResultUrlAndTitle( ) throws Exception
    {
        String strHtml = render( TEMPLATE_RESULTS, model( "horaires", List.of( result( "javascript:alert(4)//?a=1", null ),
                result( "jsp/site/Portal.jsp?page_id=13", null ) ) ) );

        assertFalse( strHtml.contains( "javascript:" ), strHtml );
        assertTrue( strHtml.contains( "<a href=\"#&terms=horaires" ), strHtml );
        assertTrue( strHtml.contains( "<a href=\"jsp/site/Portal.jsp?page_id=13&amp;terms=horaires" ) || strHtml.contains( "<a href=\"jsp/site/Portal.jsp?page_id=13&terms=horaires" ),
                strHtml );
        assertTrue( strHtml.contains( ">Piscine &lt;i&gt;Pontoise&lt;/i&gt;</a>" ), strHtml );
    }

    /**
     * A highlighted extract keeps its highlighting markers as HTML; the text of the document around them is escaped.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testHighlightKeepsOnlyTheMarkers( ) throws Exception
    {
        Map<String, List<String>> mapHighlight = new LinkedHashMap<>( );
        mapHighlight.put( "summary", List.of( "Les <b>horaires</b> de la piscine <img src=x onerror=alert(2)> & 'q'" ) );
        mapHighlight.put( "content", List.of( "Ouverture : <b>horaires</b> d'été", "Fermeture </p><script>alert(3)</script>" ) );

        String strHtml = render( TEMPLATE_RESULTS,
                model( "horaires", List.of( result( "jsp/site/Portal.jsp?page_id=12", new SolrHighlight( mapHighlight, "<b>", "</b>" ) ) ) ) );

        assertTrue( strHtml.contains( "Les <b>horaires</b> de la piscine &lt;img src=x onerror=alert(2)&gt; &amp; &#39;q&#39;" ), strHtml );
        assertTrue( strHtml.contains( "Ouverture : <b>horaires</b> d&#39;été" ), strHtml );
        assertTrue( strHtml.contains( "Fermeture &lt;/p&gt;&lt;script&gt;alert(3)&lt;/script&gt;" ), strHtml );
        assertFalse( strHtml.contains( "<img" ), strHtml );
        assertFalse( strHtml.contains( "<script>alert" ), strHtml );
    }

    /**
     * Without an extract the summary is written, as text.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testSummaryWithoutHighlight( ) throws Exception
    {
        SolrSearchResult result = result( "jsp/site/Portal.jsp?page_id=12", null );
        result.setSummary( "Résumé <b>x</b>" );

        String strHtml = render( TEMPLATE_RESULTS, model( "horaires", List.of( result ) ) );

        assertTrue( strHtml.contains( "Résumé &lt;b&gt;x&lt;/b&gt;" ), strHtml );
    }

    /**
     * The map points are attributes of the page, read by the script with JSON.parse: a value of the index cannot end the
     * script element.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testMapPointsAreAttributes( ) throws Exception
    {
        Map<String, Object> point = new HashMap<>( );
        point.put( "type", "PAGE" );
        point.put( "code", "geoloc\"x" );
        point.put( "id", "12" );
        point.put( "geojson", "{\"type\":\"Feature\",\"properties\":{\"layer\":\"parcs </script><script>alert(8)</script>\"}}" );
        Map<String, Object> conf = new HashMap<>( );
        conf.put( "extraMappingQuery", Boolean.TRUE );
        Map<String, Object> model = model( "horaires", List.of( ) );
        model.put( "conf", conf );
        model.put( "points", List.of( point ) );

        String strHtml = render( TEMPLATE_RESULTS, model );

        assertFalse( strHtml.contains( "<script>alert(8)" ), strHtml );
        assertTrue( strHtml.contains( "data-code=\"geoloc&quot;x\"" ), strHtml );
        assertTrue( strHtml.contains( "data-geojson=\"{&quot;type&quot;:&quot;Feature&quot;,&quot;properties&quot;:{&quot;layer&quot;:&quot;parcs &lt;/script&gt;" ),
                strHtml );
        assertTrue( strHtml.contains( "JSON.parse( el.dataset.geojson )" ), strHtml );
    }

    /**
     * The user menu of mylutece is HTML put in the model as markup: the header writes it as is, and its comment when it
     * is missing.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testHeaderWritesThePageInclude( ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "theme", Map.of( "pathImages", "images/local/skin/" ) );

        String strHtml = render( TEMPLATE_HEADER, model );
        assertTrue( strHtml.contains( "<!-- no myLutece plugin installed -->" ), strHtml );

        model.put( "pageinclude_userlogin", HtmlMarkup.of( "<ul class=\"menu\"><li>Connexion</li></ul>" ) );
        strHtml = render( TEMPLATE_HEADER, model );
        assertTrue( strHtml.contains( "<ul class=\"menu\"><li>Connexion</li></ul>" ), strHtml );
    }

    /**
     * The indexing logs are text: a title of the index is escaped, the line breaks stay in the pre element.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testIndexingLogsAreText( ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "command", "incremental" );
        model.put( "running", Boolean.FALSE );
        model.put( "logs", "\r\nIndexer : SolrPageIndexer - Pages\r\nAdding PAGE #12 - <script>alert(9)</script>\r\n" );

        String strHtml = render( TEMPLATE_LOGS, model );

        assertFalse( strHtml.contains( "<script>alert(9)" ), strHtml );
        assertTrue( strHtml.contains( "Adding PAGE #12 - &lt;script&gt;alert(9)&lt;/script&gt;" ), strHtml );
    }

    /**
     * The extract of a snippet: escaped, the configured markers restored, whatever they hold.
     */
    @Test
    public void testSnippetToHtml( )
    {
        assertEquals( "a <em class=\"hl\">b</em> &lt;i&gt;c&lt;/i&gt; &amp; &#39;d&#39;",
                SolrHighlight.toHtml( "a <em class=\"hl\">b</em> <i>c</i> & 'd'", "<em class=\"hl\">", "</em>" ) );
        assertEquals( "&lt;b&gt;x&lt;/b&gt;", SolrHighlight.toHtml( "<b>x</b>", "", "" ) );
        assertEquals( "", SolrHighlight.toHtml( null, "<b>", "</b>" ) );
        assertTrue( new SolrHighlight( null, "<b>", "</b>" ).getMarkupMap( ).isEmpty( ) );
    }
}
