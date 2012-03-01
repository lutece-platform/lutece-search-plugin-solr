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
package fr.paris.lutece.plugins.search.solr.web;

import fr.paris.lutece.plugins.search.solr.indexer.SolrIndexerService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * SolrIndexerJspBean
 *
 */
public class SolrIndexerJspBean extends PluginAdminPageJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constantes
    public static final String RIGHT_INDEXER = "SOLR_INDEX_MANAGEMENT";
    private static final String TEMPLATE_MANAGE_INDEXER = "admin/search/solr_manage_search_indexation.html";
    private static final String TEMPLATE_INDEXER_LOGS = "admin/search/solr_search_indexation_logs.html";
    private static final String MARK_LOGS = "logs";
    private static final String MARK_INDEXERS_LIST = "indexers_list";

    /**
     * Displays the indexing parameters
     *
     * @param request the http request
     * @return the html code which displays the parameters page
     */
    public String getIndexingProperties( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_INDEXERS_LIST, SolrIndexerService.getIndexers(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_INDEXER, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Calls the indexing process
     *
     * @param request the http request
     * @return the result of the indexing process
     */
    public String doIndexing( HttpServletRequest request )
    {
        HashMap<String, String> model = new HashMap<String, String>(  );
        String strLogs = "";

        if ( request.getParameter( "incremental" ) != null )
        {
            strLogs = SolrIndexerService.processIndexing( false );
        }
        else if ( request.getParameter( "total" ) != null )
        {
            strLogs = SolrIndexerService.processIndexing( true );
        }
        else if ( request.getParameter( "del" ) != null )
        {
            strLogs = SolrIndexerService.processDel(  );
        }

        model.put( MARK_LOGS, strLogs );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_INDEXER_LOGS, null, model );

        return getAdminPage( template.getHtml(  ) );
    }
}
