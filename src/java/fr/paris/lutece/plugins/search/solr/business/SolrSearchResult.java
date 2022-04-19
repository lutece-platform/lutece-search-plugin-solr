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

import fr.paris.lutece.portal.service.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * SolrSearchResult is more complete than a SearchResult with the highlights and the site name.
 *
 */
public class SolrSearchResult extends SearchResult
{
    private SolrHighlight _highlight;
    private String _strSite;
    private List<String> _lstCategories;
    private String _strMetadata;
    private Map<String, Object> _dynamicFields;
    private String _strDocPortletId;
    private String _strHieDate;
    private String _strXmlContent;
    private Map<String, List<SolrSearchResult>> _childDocuments;

    /**
     * Return the highlight
     * 
     * @return the highlight
     */
    public SolrHighlight getHighlight( )
    {
        return _highlight;
    }

    /**
     * Set the highlight
     * 
     * @param highlight
     *            the highlight
     */
    public void setHighlight( SolrHighlight highlight )
    {
        _highlight = highlight;
    }

    /**
     * Return the site name
     * 
     * @return the site name
     */
    public String getSite( )
    {
        return _strSite;
    }

    /**
     * Set the the site name
     * 
     * @param strSite
     *            the site name
     */
    public void setSite( String strSite )
    {
        _strSite = strSite;
    }

    /**
     * Return the categories
     * 
     * @return the list of categories
     */
    public List<String> getCategorie( )
    {
        return _lstCategories;
    }

    /**
     * Set the categories
     * 
     * @param lstCategories
     *            the list of categories
     */
    public void setCategorie( List<String> lstCategories )
    {
        _lstCategories = lstCategories;
    }

    /**
     * Return the metadata
     * 
     * @return the metadata
     */
    public String getMetadata( )
    {
        return _strMetadata;
    }

    /**
     * Set the metadata
     * 
     * @param strMetadata
     *            the metadata
     */
    public void setMetadata( String strMetadata )
    {
        _strMetadata = strMetadata;
    }

    /**
     * Return all the dynamics fields
     * 
     * @return A map which contains all the dynamics fields. The name of a dynamic field is like NAME_XXX where NAME is the name of the field and XXX a SolrItem
     *         dynamic type
     */
    public Map<String, Object> getDynamicFields( )
    {
        return _dynamicFields;
    }

    /**
     * Set the dynamics fields
     * 
     * @param dynamicFields
     *            the dynamics fields
     */
    public void setDynamicFields( Map<String, Object> dynamicFields )
    {
        _dynamicFields = dynamicFields;
    }

    /**
     * Return the portlet identifier
     * 
     * @return the portlet identifier
     */
    public String getDocPortletId( )
    {
        return _strDocPortletId;
    }

    /**
     * Set the portlet identifier
     * 
     * @param strDocPortletId
     *            the portlet identifier
     */
    public void setDocPortletId( String strDocPortletId )
    {
        _strDocPortletId = strDocPortletId;
    }

    /**
     * Return the hierarchical date
     * 
     * @return the hierarchical date
     */
    public String getHieDate( )
    {
        return _strHieDate;
    }

    /**
     * Set the hierarchical date
     * 
     * @param strHieDate
     *            the hierarchical date
     */
    public void setHieDate( String strHieDate )
    {
        _strHieDate = strHieDate;
    }

    /**
     * Return the XML content
     * 
     * @return the XML content
     */
    public String getXmlContent( )
    {
        return _strXmlContent;
    }

    /**
     * Set the XML content
     * 
     * @param strXmlContent
     *            the XML content
     */
    public void setXmlContent( String strXmlContent )
    {
        _strXmlContent = strXmlContent;
    }
    /** Returns the map of child documents, or null if none. */
    public Map<String, List<SolrSearchResult>> getChildDocuments( ) {
      return _childDocuments;
    }
    /**
     * Set child documents
     * @param children the child document
     */
    public void setChildDocuments( Map<String, List<SolrSearchResult>> children) {
 	   
    	_childDocuments= children;
    }
    
    public void putChildDocuments(String strName, List<SolrSearchResult> children) {
 	   
    	 if (_childDocuments == null) {
     		   
      	   _childDocuments = new HashMap<>( );
     	   }

    	 _childDocuments.put( strName, children );
    }

}
