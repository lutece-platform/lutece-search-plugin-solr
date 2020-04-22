/*
 * Copyright (c) 2002-2020, City of Paris
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

import fr.paris.lutece.portal.service.search.SearchItem;

import org.apache.solr.common.SolrDocument;

import java.util.Date;

/**
 *
 * A SearchItem
 *
 */
public class SolrSearchItem
{
    private String _strId;
    private String _strType;
    private String _strUrl;
    private String _strSummary;
    private String _strTitle;
    private Date _date;
    private String _strRole;
    private String _strSite;

    /**
     * Empty constructor
     */
    public SolrSearchItem( )
    {
    }

    /**
     * Creates and fills a SolrSearchItem form a SolrDocument
     * 
     * @param document
     *            the document
     */
    public SolrSearchItem( SolrDocument document )
    {
        Object o;

        o = document.getFieldValue( SearchItem.FIELD_UID );

        if ( o == null )
        {
            _strId = "";
        }
        else
        {
            _strId = o.toString( );
        }

        o = document.getFieldValue( SearchItem.FIELD_SUMMARY );

        if ( o == null )
        {
            _strSummary = "";
        }
        else
        {
            _strSummary = o.toString( );
        }

        o = document.getFieldValue( SearchItem.FIELD_TITLE );

        if ( o == null )
        {
            _strTitle = "";
        }
        else
        {
            _strTitle = o.toString( );
        }

        o = document.getFieldValue( SearchItem.FIELD_TYPE );

        if ( o == null )
        {
            _strType = "";
        }
        else
        {
            _strType = o.toString( );
        }

        o = document.getFieldValue( SearchItem.FIELD_URL );

        if ( o == null )
        {
            _strUrl = "";
        }
        else
        {
            _strUrl = o.toString( );
        }

        o = document.getFieldValue( SearchItem.FIELD_DATE );

        if ( o != null )
        {
            // System.out.println(o.getClass());
            _date = new Date( );
        }
    }

    /**
     * Return the id
     * 
     * @return the id
     */
    public String getId( )
    {
        return _strId;
    }

    /**
     * Sets the id
     * 
     * @param strId
     *            the id
     */
    public void setId( String strId )
    {
        _strId = strId;
    }

    /**
     * Return the type
     * 
     * @return the type
     */
    public String getType( )
    {
        return _strType;
    }

    /**
     * Set the type
     * 
     * @param strType
     *            the type
     */
    public void setType( String strType )
    {
        _strType = strType;
    }

    /**
     * Return the title
     * 
     * @return the title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Set the title
     * 
     * @param strTitle
     *            the title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Return the summary
     * 
     * @return the summary
     */
    public String getSummary( )
    {
        return _strSummary;
    }

    /**
     * Set the summary
     * 
     * @param strSummary
     *            the summary
     */
    public void setSummary( String strSummary )
    {
        _strSummary = strSummary;
    }

    /**
     * Return the url
     * 
     * @return the url
     */
    public String getUrl( )
    {
        return _strUrl;
    }

    /***
     * Set the url
     * 
     * @param strUrl
     *            the url
     */
    public void setUrl( String strUrl )
    {
        _strUrl = strUrl;
    }

    /**
     * Return the date
     * 
     * @return the date
     */
    public Date getDate( )
    {
        return _date;
    }

    /**
     * Set the date
     * 
     * @param date
     *            the date.
     */
    public void setDate( Date date )
    {
        _date = date;
    }

    /**
     * Return the role
     * 
     * @return the role
     */
    public String getRole( )
    {
        return _strRole;
    }

    /**
     * Set the role
     * 
     * @param strRole
     *            the role
     */
    public void setRole( String strRole )
    {
        _strRole = strRole;
    }

    /**
     * Return the site name.
     * 
     * @return the site name.
     */
    public String getSite( )
    {
        return _strSite;
    }

    /**
     * Sets the site name.
     * 
     * @param strSite
     *            the new site name.
     */
    public void setSite( String strSite )
    {
        _strSite = strSite;
    }
}
