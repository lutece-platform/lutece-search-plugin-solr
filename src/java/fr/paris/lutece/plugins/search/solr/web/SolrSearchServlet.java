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
package fr.paris.lutece.plugins.search.solr.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;

import fr.paris.lutece.plugins.search.solr.business.SolrSearchAppConf;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchEngine;
import fr.paris.lutece.plugins.search.solr.service.ISolrSearchAppAddOn;
import fr.paris.lutece.plugins.search.solr.service.SolrSearchAppConfService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 * SolrIndexerJspBean
 *
 */
public class SolrSearchServlet extends HttpServlet
{
    private static final long serialVersionUID = -3273825949482572338L;

    /**
     * Returns search results
     *
     * @param request
     *            the http request
     * @return Returns search results
     * @throws SiteMessageException 
     */
    public String searchSolr( HttpServletRequest request ) throws SiteMessageException
    {
    	 String strConfCode = request.getParameter( SolrSearchApp.PARAMETER_CONF );
         SolrSearchAppConf conf = SolrSearchAppConfService.loadConfiguration( strConfCode );
         if ( conf == null )
         {
             // Use default conf if the requested one doesn't exist
             conf = SolrSearchAppConfService.loadConfiguration( null );
         }

         Map<String, Object> model = SolrSearchApp.getSearchResultModel( request, conf );
         for ( String beanName : conf.getAddonBeanNames( ) )
         {
             ISolrSearchAppAddOn solrSearchAppAddon = SpringContextService.getBean( beanName );
             solrSearchAppAddon.buildPageAddOn( model, request );
         }
         HtmlTemplate template = AppTemplateService.getTemplate( conf.getTemplate( ), request.getLocale( ), model );

         return template.getHtml( );
    }

    @Override
    public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
    	String strHtml= null;
       try {
			  strHtml= this.searchSolr( req );
		} catch (SiteMessageException e) {
			
			AppLogService.error( e.getMessage(), e );
		}
        PrintWriter out = resp.getWriter( );
        out.println( strHtml );
    }

    @Override
    public void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        doGet( req, resp );
    }
}
