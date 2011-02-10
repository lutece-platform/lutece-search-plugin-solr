/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.search.solr.indexer;

import fr.paris.lutece.portal.service.search.SearchItem;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *
 * An item that is easy to add to a SolrServer.
 *
 */
public class SolrItem
{
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_SITE = "site";
    public static final String FIELD_XML_CONTENT = "xml_content";
    public static final String FIELD_CATEGORIE = "categorie";
    public static final String FIELD_HIERATCHY_DATE = "hiedate";
    @Field( SearchItem.FIELD_URL )
    private String _strUrl;
    @Field( SearchItem.FIELD_DATE )
    private Date _lDate;
    @Field( SearchItem.FIELD_TITLE )
    private String _strTitle;
    @Field( SearchItem.FIELD_UID )
    private String _strUid;
    @Field( FIELD_CONTENT )
    private String _strContent;
    @Field( FIELD_SITE )
    private String _strSite;
    @Field( SearchItem.FIELD_SUMMARY )
    private String _strSummary;
    @Field( SearchItem.FIELD_TYPE )
    private String _strType;
    @Field( SearchItem.FIELD_ROLE )
    private String _strRole;
    @Field( FIELD_XML_CONTENT )
    private String _strXmlContent;
    @Field( FIELD_CATEGORIE )
    private List<String> _strCategorie;
    @Field( FIELD_HIERATCHY_DATE )
    private String _strHieDate;

    //DynamicField
    @Field( "*_listbox" )
    private Map<String, List<String>> dfListBox;
    @Field( "*_multiline" )
    private Map<String, String> dfMultiLine;
    @Field( "*_richtext" )
    private Map<String, String> dfRichText;
    @Field( "*_text" )
    private Map<String, String> dfText;
    @Field( "*_url" )
    private Map<String, String> dfUrl;
    @Field( "*_date" )
    private Map<String, Date> dfDate;
    @Field( "*_numerictext" )
    private Map<String, Long> dfNumericText;
    @Field( "*_internallink" )
    private Map<String, String> dfInternalLink;

    /**
     * Creates a new SolrItem
     */
    public SolrItem(  )
    {
    }

    /*******************/
    /** DYNAMIC FIELD **/
    /*******************/
    public Map<String, List<String>> getDfListBox(  )
    {
        return dfListBox;
    }

    public void setDfListBox( Map<String, List<String>> dfListBox )
    {
        this.dfListBox = dfListBox;
    }

    public Map<String, String> getDfMultiLine(  )
    {
        return dfMultiLine;
    }

    public void setDfMultiLine( Map<String, String> dfMultiLine )
    {
        this.dfMultiLine = dfMultiLine;
    }

    public Map<String, String> getDfRichText(  )
    {
        return dfRichText;
    }

    public void setDfRichText( Map<String, String> dfRichText )
    {
        this.dfRichText = dfRichText;
    }

    public Map<String, String> getDfText(  )
    {
        return dfText;
    }

    public void setDfText( Map<String, String> dfText )
    {
        this.dfText = dfText;
    }

    public Map<String, String> getDfUrl(  )
    {
        return dfUrl;
    }

    public void setDfUrl( Map<String, String> dfUrl )
    {
        this.dfUrl = dfUrl;
    }

    public Map<String, Date> getDfDate(  )
    {
        return dfDate;
    }

    public void setDfDate( Map<String, Date> dfDate )
    {
        this.dfDate = dfDate;
    }

    public Map<String, Long> getDfNumericText(  )
    {
        return dfNumericText;
    }

    public void setDfNumericText( Map<String, Long> dfNumericText )
    {
        this.dfNumericText = dfNumericText;
    }

    public Map<String, String> getDfInternalLink(  )
    {
        return dfInternalLink;
    }

    public void setDfInternalLink( Map<String, String> dfInternalLink )
    {
        this.dfInternalLink = dfInternalLink;
    }

    public void addDynamiqueField( String name, String value, String Type )
    {
        /*if (this.dfString == null) {
                this.dfString = new HashMap<String, String>();
        }
        this.dfString.put(name, value);*/
    }

    /**
    * Gets the url
    * @return the url
    */
    public String getUrl(  )
    {
        return _strUrl;
    }

    /**
     * Sets the url
     * @param strUrl the url
     */
    public void setUrl( String strUrl )
    {
        _strUrl = strUrl;
    }

    /**
     * Return the date
     * @return the date
     */
    public Date getDate(  )
    {
        return _lDate;
    }

    /**
     * Sets the date
     * @param lDate the date
     */
    public void setDate( Date lDate )
    {
        _lDate = lDate;
    }

    /**
     * Gets the title
     * @return the title
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Sets the title
     * @param strTitle the title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Gets the id
     * @return the id
     */
    public String getUid(  )
    {
        return _strUid;
    }

    /**
     * Sets the id
     * @param strUid the id
     */
    public void setUid( String strUid )
    {
        _strUid = strUid;
    }

    /**
     * Gets the content
     * @return the content
     */
    public String getContent(  )
    {
        return _strContent;
    }

    /**
     * Sets the content
     * @param strContent the content
     */
    public void setContent( String strContent )
    {
        _strContent = strContent;
    }

    /**
     * Gets the site
     * @return the site name
     */
    public String getSite(  )
    {
        return _strSite;
    }

    /**
     * Sets the site name
     * @param strSite the site name
     */
    public void setSite( String strSite )
    {
        _strSite = strSite;
    }

    /**
     * Gets the summary
     * @return the summary
     */
    public String getSummary(  )
    {
        return _strSummary;
    }

    /**
     * Sets the summary
     * @param strSummary the summary
     */
    public void setSummary( String strSummary )
    {
        _strSummary = strSummary;
    }

    /**
     * Gets the type
     * @return the type
     */
    public String getType(  )
    {
        return _strType;
    }

    /**
     * Sets the type
     * @param strType the type
     */
    public void setType( String strType )
    {
        _strType = strType;
    }

    /**
     * Gets the role
     * @return the role
     */
    public String getRole(  )
    {
        return _strRole;
    }

    /**
     * Sets the role
     * @param strRole the role
     */
    public void setRole( String strRole )
    {
        _strRole = strRole;
    }

    public String getXmlContent(  )
    {
        return _strXmlContent;
    }

    public void setXmlContent( String strXmlContent )
    {
        _strXmlContent = strXmlContent;
    }

    /*    public String getCategorie( )
        {
                return _strCategorie;
        }
    
        public void setCategorie( String strCategorie )
        {
                _strCategorie = strCategorie;
        }
     */
    public List<String> getCategorie(  )
    {
        return _strCategorie;
    }

    public void setCategorie( List<String> strCategorie )
    {
        _strCategorie = strCategorie;
    }

    public String getHieDate(  )
    {
        return _strHieDate;
    }

    public void setHieDate( String strHieDate )
    {
        _strHieDate = strHieDate;
    }
}
