/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.plugins.search.solr.business.facetIntersection.FacetIntersectionHome;
import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.plugins.search.solr.business.field.FieldHome;
import fr.paris.lutece.plugins.search.solr.business.field.SolrFieldManager;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * SolrIndexerJspBean
 *
 */
public class SolrConfigurationJspBean extends PluginAdminPageJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constantes
    public static final String RIGHT_CONFIGURATION = "SOLR_CONFIGURATION_MANAGEMENT";
    private static final String TEMPLATE_MANAGE_FACET_CONFIGURATION = "admin/search/solr_manage_search_configuration.html";
    private static final String TEMPLATE_MANAGE_SORT_CONFIGURATION = "admin/search/solr_manage_sort_configuration.html";
    private static final String TEMPLATE_MANAGE_INTERSECTION_CONFIGURATION = "admin/search/solr_manage_intersection_configuration.html";
    private static final String MARK_FIELD = "field_list";
    private static final String MARK_INTERSECTION = "intersections";
    private static final String MESSAGE_VALID = "search.solr.adminFeature.configuration.valid";

    /**
     * Displays the indexing parameters
     *
     * @param request the http request
     * @return the html code which displays the parameters page
     */
    public String getFacet( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FIELD, SolrFieldManager.getFieldList(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_FACET_CONFIGURATION, getLocale(  ),
                model );

        return template.getHtml(  );
    }

    public String getSort( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FIELD, SolrFieldManager.getFieldList(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_SORT_CONFIGURATION, getLocale(  ), model );

        return template.getHtml(  );
    }

    public String getIntersection( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FIELD, SolrFieldManager.getFacetList(  ).values(  ) );

        //load facet intersection
        model.put( MARK_INTERSECTION, FacetIntersectionHome.getFacetIntersectionsList(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_INTERSECTION_CONFIGURATION,
                getLocale(  ), model );

        return template.getHtml(  );
    }

    public String getPage( HttpServletRequest request )
    {
        StringBuffer htmlBuffer = new StringBuffer(  );
        htmlBuffer.append( this.getFacet( request ) );
        htmlBuffer.append( this.getSort( request ) );
        htmlBuffer.append( this.getIntersection( request ) );

        return getAdminPage( htmlBuffer.toString(  ) );
    }

    //Traitement
    public String doFacet( HttpServletRequest request )
    {
        for ( Field field : SolrFieldManager.getFieldList(  ) )
        {
            if ( request.getParameter( Integer.toString( field.getIdField(  ) ) ) != null )
            {
                field.setEnableFacet( true );
            }
            else
            {
                field.setEnableFacet( false );
            }

            FieldHome.update( field );
        }

        return this.validMessage( request );
    }

    public String doSort( HttpServletRequest request )
    {
        for ( Field field : SolrFieldManager.getFieldList(  ) )
        {
            if ( request.getParameter( Integer.toString( field.getIdField(  ) ) ) != null )
            {
                field.setEnableSort( true );
            }
            else
            {
                field.setEnableSort( false );
            }

            FieldHome.update( field );
        }

        return this.validMessage( request );
    }

    public String doIntersect( HttpServletRequest request )
    {
        //CREATE
        if ( request.getParameter( "add" ) != null )
        {
            FacetIntersectionHome.create( Integer.parseInt( request.getParameter( "field1" ) ),
                Integer.parseInt( request.getParameter( "field2" ) ) );
        }

        //DELETE
        else if ( request.getParameter( "delete" ) != null )
        {
            FacetIntersectionHome.remove( Integer.parseInt( request.getParameter( "field1" ) ),
                Integer.parseInt( request.getParameter( "field2" ) ) );
        }

        SolrFieldManager.reloadIntersection(  );

        return AppPathService.getBaseUrl( request ) +
        "jsp/admin/search/solr/ManageSearchConfiguration.jsp?plugin_name=solr";
    }

    private String validMessage( HttpServletRequest request )
    {
        return AdminMessageService.getMessageUrl( request, MESSAGE_VALID, AdminMessage.TYPE_INFO );
    }
}
