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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.search.solr.service.SolrSearchAppConfService;
import fr.paris.lutece.plugins.search.solr.util.SolrConstants;

/**
 *
 * SolrSearchAppConf
 *
 */
public class SolrSearchAppConf
{
    private String _strCode = SolrSearchAppConfService.EMPTY_CODE;
    private String _strFilterQuery = SolrConstants.CONSTANT_DEFAULT_FILTER_QUERY;
    private String _strFieldList = SolrConstants.CONSTANT_WILDCARD;
    private String _strTemplate = SolrConstants.CONSTANT_DEFAULT_TEMPLATE;
    private boolean _bExtraMappingQuery = SolrConstants.CONSTANT_DEFAULT_EXTRA_MAPPING_QUERY;
    private List<String> _listAddonBeanNames = new ArrayList<>( );

    /**
     * Returns the code
     * 
     * @return the code
     */
    public String getCode( )
    {
        return _strCode;
    }

    /**
     * Sets the code
     * 
     * @param code
     *            the code
     */
    public void setCode( String code )
    {
        _strCode = code;
    }

    /**
     * Returns the template
     * 
     * @return the template
     */
    public String getTemplate( )
    {
        return _strTemplate;
    }

    /**
     * Sets the template
     * 
     * @param template
     *            the template
     */
    public void setTemplate( String template )
    {
        _strTemplate = template;
    }

    /**
     * Returns the filter query
     * 
     * @return the filter query
     */
    public String getFilterQuery( )
    {
        return _strFilterQuery;
    }

    /**
     * Sets the filter query
     * 
     * @param filter
     *            query the code
     */
    public void setFilterQuery( String strFilterQuery )
    {
        _strFilterQuery = strFilterQuery;
    }
    /**
     * Returns the FieldList
     * @return The FieldList
     */ 
     public String getFieldList()
     {
         return _strFieldList;
     }
 
    /**
     * Sets the FieldList
     * @param strFieldList The FieldList
     */ 
     public void setFieldList( String strFieldList )
     {
         _strFieldList = strFieldList;
     }

    /**
     * Returns the boolean indicating if we need an extra query for mapping
     * 
     * @return the boolean indicating if we need an extra query for mapping
     */
    public boolean getExtraMappingQuery( )
    {
        return _bExtraMappingQuery;
    }

    /**
     * Sets the boolean indicating if we need an extra query for mapping
     * 
     * @param bExtraMappingQuery
     *            the boolean indicating if we need an extra query for mapping
     */
    public void setExtraMappingQuery( boolean bExtraMappingQuery )
    {
        _bExtraMappingQuery = bExtraMappingQuery;
    }

    /**
     * @return the AddonBeanNames
     */
    public List<String> getAddonBeanNames( )
    {
        return _listAddonBeanNames;
    }

    /**
     * @param listAddonBeanNames
     *            the AddonBeanNames to set
     */
    public void setAddonBeanNames( List<String> listAddonBeanNames )
    {
        this._listAddonBeanNames = listAddonBeanNames;
    }
}
