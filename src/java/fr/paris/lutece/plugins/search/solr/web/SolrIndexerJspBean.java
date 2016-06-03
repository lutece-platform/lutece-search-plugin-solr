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
package fr.paris.lutece.plugins.search.solr.web;

import fr.paris.lutece.plugins.search.solr.indexer.SolrIndexerService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Map;

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
    private static final String MARK_RUNNING = "running";
    private static final String MARK_COMMAND = "command";
    private static final String MARK_INDEXERS_LIST = "indexers_list";

    private static final String JSP_VIEW_INDEXATION = "ViewSearchIndexation.jsp";

    private static Thread _thread;
    private static String _threadLogs;
    private static String _command;

    private Map<String, Object> getModel() {
        Map<String, Object> model = new HashMap<>();

        String strLogs;
        boolean bRunning;
        if (_thread != null) {
            strLogs = SolrIndexerService.getSbLogs().toString();
            bRunning = true;
        } else {
            strLogs = _threadLogs;
            bRunning = false;
        }

        model.put( MARK_LOGS, strLogs );
        model.put( MARK_RUNNING, bRunning );
        model.put( MARK_COMMAND, _command );
        return model;
    }

    /**
     * Displays the indexing parameters
     *
     * @param request the http request
     * @return the html code which displays the parameters page
     */
    public String getIndexingProperties( HttpServletRequest request )
    {
        Map<String, Object> model = getModel();
        model.put( MARK_INDEXERS_LIST, SolrIndexerService.getIndexers(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_INDEXER, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Displays the indexation progress
     *
     * @param request the http request
     * @return the html code which displays the parameters page
     */
    public String getIndexing( HttpServletRequest request )
    {
        Map<String, Object> model = getModel();

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_INDEXER_LOGS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Calls the indexing process
     *
     * @param request the http request
     * @return the result of the indexing process
     */
    public static synchronized String doIndexing( HttpServletRequest request )
    {
        if (_thread == null) {
            final HttpServletRequest finalRequest = request;
            _thread = new Thread() {
                @Override
                public void run() {
                    try {
                        if ( finalRequest.getParameter( "incremental" ) != null )
                        {
                            _command = "incremental";
                            _threadLogs = SolrIndexerService.processIndexing( false );
                        }
                        else if ( finalRequest.getParameter( "total" ) != null )
                        {
                            _command = "total";
                            _threadLogs = SolrIndexerService.processIndexing( true );
                        }
                        else if ( finalRequest.getParameter( "del" ) != null )
                        {
                            _command = "del";
                            _threadLogs = SolrIndexerService.processDel(  );
                        }
                    } catch (Exception e) {
                        _threadLogs = e.toString();
                        AppLogService.error ("Error during solr indexation", e );
                    } finally {
                        _thread = null;
                    }
                }
            };
            _thread.start();
        }

        return JSP_VIEW_INDEXATION;
    }
}
