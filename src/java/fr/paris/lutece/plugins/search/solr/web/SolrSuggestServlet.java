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

import fr.paris.lutece.plugins.search.solr.business.SolrSearchEngine;
import fr.paris.lutece.plugins.search.solr.util.SolrUtil;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * SolrIndexerJspBean
 *
 */
public class SolrSuggestServlet extends HttpServlet
{
    private static final long serialVersionUID = -3273825949482572338L;

     
    public void init(  )
    {
    }

    /**
     * Displays the indexing parameters
     *
     * @param request the http request
     * @return the html code which displays the parameters page
     */
    public String getSuggest( HttpServletRequest request )
    {
        String callback = request.getParameter( "callback" );
        String terms = request.getParameter( "q" );

        SolrSearchEngine engine = SolrSearchEngine.getInstance(  );
        StringBuffer result = new StringBuffer(  );

        // XSS control
        if ( !SolrUtil.isValidJavascriptFunctionName( callback ) )
        {
            return AppPropertiesService.getProperty( SolrUtil.PROPERTY_CALLBACK_FUNCTION_NAME_ERROR_MESSAGE, "Invalid function name" ) ;
        }

        result.append( callback );

        result.append( "({\"response\":{\"docs\":[" );

        QueryResponse response = engine.getJsonpSuggest( terms, callback );
        Collation solr = null;
        //String fieldName = null;
        //int i= 1;

        //Iterate on all document
        for ( Iterator<Collation> ite = response.getSpellCheckResponse().getCollatedResults().iterator(); ite.hasNext(  ); )
        {
        	if (ite.hasNext(  ))
        	{
	        	solr = ite.next(  );
	           
	            String collation=solr.getCollationQueryString();
	            
	
	            //iterate on each field
	            //for ( String suggest : suggestions )
	            //{
	            	result.append( "{" );
	                result.append( "\"" ).append( "title" ).append( "\":\"" ).append( collation )
	                      .append( "\"" );
	
	
		            if ( ite.hasNext(  ) )
		            {
		                result.append( "}," );
		            }
		            else
		            {
		                result.append( "}" );
		            }
        	}
	           // i++;
            //}
        }

        //Close result
        result.append( "]}})" );

        return result.toString(  );
    }

    public void doGet( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        resp.reset(  );
        resp.resetBuffer(  );
        resp.setContentType( "application/x-javascript; charset=utf-8" );

        PrintWriter out = resp.getWriter(  );
        out.print( this.getSuggest( req ) );
        out.flush(  );
        out.close(  );
    }

    public void doPost( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        resp.reset(  );
        resp.resetBuffer(  );
        resp.setContentType( "application/x-javascript; charset=utf-8" );

        PrintWriter out = resp.getWriter(  );
        out.print( this.getSuggest( req ) );
        out.flush(  );
        out.close(  );
    }
}
