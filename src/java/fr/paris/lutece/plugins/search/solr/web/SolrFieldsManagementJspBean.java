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

import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.plugins.search.solr.business.field.FieldHome;
import fr.paris.lutece.plugins.search.solr.business.field.SolrFieldManager;
import fr.paris.lutece.plugins.search.solr.indexer.SolrIndexerService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * SolrIndexerJspBean
 *
 */
public class SolrFieldsManagementJspBean extends PluginAdminPageJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constantes
    public static final String SOLR_FIELDS_MANAGEMENT = "SOLR_FIELDS_MANAGEMENT";
    private static final String TEMPLATE_DISPLAY_SOLR_FIELDS = "admin/search/solr_manage_fields_display.html";
    private static final String TEMPLATE_SOLR_FIELDS_FORM = "admin/search/solr_manage_fields_form.html";
    private static final String MARK_FIELD = "field_list";
    private static final String MARK_WEIGHT_HIGH = "weight_high";
    private static final String MARK_WEIGHT_NORMAL = "weight_normal";
    private static final String MARK_WEIGHT_LOW = "weight_low";

    private static final String PROPERTY_WEIGHT_HIGH = "solr.weights.high";
    private static final String DEFAULT_WEIGHT_HIGH = "10";
    private static final String PROPERTY_WEIGHT_NORMAL = "solr.weights.normal";
    private static final String DEFAULT_WEIGHT_NORMAL = "1";
    private static final String PROPERTY_WEIGHT_LOW = "solr.weights.low";
    private static final String DEFAULT_WEIGHT_LOW = "0.1";

    /**
     * Displays the indexing parameters
     *
     * @param request the http request
     * @return the html code which displays the parameters page
     */
    public String getFieldList( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FIELD, SolrFieldManager.getFieldList(  ) );

        model.put( MARK_WEIGHT_HIGH, Double.parseDouble( AppPropertiesService.getProperty( PROPERTY_WEIGHT_HIGH, DEFAULT_WEIGHT_HIGH ) ) );
        model.put( MARK_WEIGHT_NORMAL, Double.parseDouble( AppPropertiesService.getProperty( PROPERTY_WEIGHT_NORMAL, DEFAULT_WEIGHT_NORMAL ) ) );
        model.put( MARK_WEIGHT_LOW, Double.parseDouble( AppPropertiesService.getProperty( PROPERTY_WEIGHT_LOW, DEFAULT_WEIGHT_LOW ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DISPLAY_SOLR_FIELDS, getLocale(  ), model );

        return template.getHtml(  );
    }

    public String getPage( HttpServletRequest request )
    {
        StringBuffer htmlBuffer = new StringBuffer(  );
        htmlBuffer.append( this.getFieldList( request ) );

        return getAdminPage( htmlBuffer.toString(  ) );
    }

    public String getForm( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        //UPDATE
        if ( request.getParameter( "update" ) != null )
        {
            model.put( "create", false );

            //load Field
            Field updateField = FieldHome.findByPrimaryKey( Integer.parseInt( request.getParameter( "id" ) ) );
            model.put( "field", updateField );
        }
        else if ( request.getParameter( "create" ) != null )
        {
            //CREATE
            Field createField = new Field(  );
            model.put( "create", true );
            model.put( "field", createField );
        }

        model.put( MARK_WEIGHT_HIGH, Double.parseDouble( AppPropertiesService.getProperty( PROPERTY_WEIGHT_HIGH, DEFAULT_WEIGHT_HIGH ) ) );
        model.put( MARK_WEIGHT_NORMAL, Double.parseDouble( AppPropertiesService.getProperty( PROPERTY_WEIGHT_NORMAL, DEFAULT_WEIGHT_NORMAL ) ) );
        model.put( MARK_WEIGHT_LOW, Double.parseDouble( AppPropertiesService.getProperty( PROPERTY_WEIGHT_LOW, DEFAULT_WEIGHT_LOW ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SOLR_FIELDS_FORM, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    //Traitement
    public String doFields( HttpServletRequest request )
    {
        if ( request.getParameter( "delete" ) != null )
        {
            return this.doDeleteField( request );
        }
        else if ( request.getParameter( "update" ) != null )
        {
            return this.doUpdateField( request );
        }
        else if ( request.getParameter( "create" ) != null )
        {
            return this.doCreateField( request );
        }
        else if ( request.getParameter( "refresh" ) != null )
        {
            return this.doRefreshFields( request );
        }
        else
        {
            return null;
        }
    }

    public String doRefreshFields ( HttpServletRequest request )
    {
        // Update fields to add dynamic fields of indexers
        SolrFieldManager.updateFields( SolrIndexerService.getAdditionalFields(  ) );
        SolrFieldManager.reloadField(  );

        return getPage( request );
    }

    public String doDeleteField( HttpServletRequest request )
    {
        String id = request.getParameter( "id" );
        FieldHome.remove( Integer.parseInt( id ) );
        SolrFieldManager.reloadField(  );

        return getPage( request );
    }

    public String doUpdateField( HttpServletRequest request )
    {
        Field field = FieldHome.findByPrimaryKey( Integer.parseInt( request.getParameter( "id" ) ) );
        field.setName( request.getParameter( "name" ) );

        try
        {
            field.setLabel( URLDecoder.decode( request.getParameter( "label" ), "UTF-8" ) );
            field.setDescription( URLDecoder.decode( request.getParameter( "description" ), "UTF-8" ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        field.setIsFacet( request.getParameter( "isfacet" ) != null );
        field.setIsSort( request.getParameter( "issort" ) != null );
        field.setWeight( Double.parseDouble(request.getParameter( "weight" ) ));
        field.setFacetMincount(Integer.parseInt(request.getParameter( "facetMincount" ) ));
        field.setOperator(request.getParameter( "operator" ) );
        field.setFacetOrder( Integer.parseInt( request.getParameter( "order" ) ) );
        FieldHome.update( field );
        SolrFieldManager.reloadField(  );

        return getPage( request );
    }

    public String doCreateField( HttpServletRequest request )
    {
        Field field = new Field(  );
        field.setName( request.getParameter( "name" ) );

        try
        {
            field.setLabel( URLDecoder.decode( request.getParameter( "label" ), "UTF-8" ) );
            field.setDescription( URLDecoder.decode( request.getParameter( "description" ), "UTF-8" ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        field.setIsFacet( request.getParameter( "isfacet" ) != null );
        field.setEnableFacet( false );
        field.setIsSort( request.getParameter( "issort" ) != null );
        field.setEnableSort( false );
        field.setDefaultSort( false );
        field.setWeight( Double.parseDouble(request.getParameter( "weight" ) ));
        field.setFacetMincount(Integer.parseInt(request.getParameter( "facetMincount" ) ));
        field.setFacetOrder( Integer.parseInt( request.getParameter( "order" ) ) );
        
        FieldHome.create( field );
        SolrFieldManager.reloadField(  );

        return getPage( request );
    }
}
