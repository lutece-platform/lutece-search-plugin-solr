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
package fr.paris.lutece.plugins.search.solr.indexer;

import fr.paris.lutece.plugins.leaflet.business.GeolocItem;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrInputDocument;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
    public static final String FIELD_FILE_CONTENT = "file_content";
    public static final String FIELD_CATEGORIE = "categorie";
    public static final String FIELD_HIERATCHY_DATE = "hiedate";
    public static final String FIELD_METADATA = "metadata";
    public static final String FIELD_ID_RESOURCE = "id_resource";
    public static final String DYNAMIC_STRING_FIELD_SUFFIX = "_string";
    public static final String DYNAMIC_TEXT_FIELD_SUFFIX = "_text";
    public static final String DYNAMIC_URL_FIELD_SUFFIX = "_url";
    public static final String DYNAMIC_DATE_FIELD_SUFFIX = "_date";
    public static final String DYNAMIC_LIST_DATE_FIELD_SUFFIX = "_list_date";
    public static final String DYNAMIC_LONG_FIELD_SUFFIX = "_long";
    public static final String DYNAMIC_LIST_FIELD_SUFFIX = "_list";
    public static final String DYNAMIC_GEOLOC_FIELD_SUFFIX = "_geoloc";
    public static final String DYNAMIC_GEOJSON_FIELD_SUFFIX = "_geojson";
    public static final String DYNAMIC_GEOJSON_ADDRESS_FIELD_SUFFIX = "_address";
    private static final String GEOLOC_JSON_PATH_GEOMETRY = "geometry";
    private static final String GEOLOC_JSON_PATH_GEOMETRY_COORDINATES = "coordinates";
    public static final String FIELD_CHILD_DOCUMENTS = "_childDocuments";

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
    @Field( FIELD_METADATA )
    private String _strMetadata;
    @Field( SearchItem.FIELD_DOCUMENT_PORTLET_ID )
    private String _strDocPortletId;
    @Field( FIELD_FILE_CONTENT )
    private String _strFileContent;
    @Field( FIELD_ID_RESOURCE )
    private String _strIdresource;
    // DynamicField
    @Field( "*" + DYNAMIC_LIST_FIELD_SUFFIX )
    private Map<String, List<String>> _dfListBox;
    @Field( "*" + DYNAMIC_TEXT_FIELD_SUFFIX )
    private Map<String, String> _dfText;
    @Field( "*" + DYNAMIC_STRING_FIELD_SUFFIX )
    private Map<String, String> _dfString;
    @Field( "*" + DYNAMIC_DATE_FIELD_SUFFIX )
    private Map<String, Date> _dfDate;
    @Field( "*" + DYNAMIC_LONG_FIELD_SUFFIX )
    private Map<String, Long> _dfNumericText;
    @Field( "*" + DYNAMIC_GEOLOC_FIELD_SUFFIX )
    private Map<String, String> _dfGeoloc;
    @Field( "*" + DYNAMIC_GEOJSON_FIELD_SUFFIX )
    private Map<String, String> _dfGeojson;
    @Field( "*" + DYNAMIC_LIST_DATE_FIELD_SUFFIX )
    private Map<String, List<Date>> _dfListDate;
    @Field( "*" + FIELD_CHILD_DOCUMENTS )
    private Map<String, Collection<SolrItem>> _childDocuments;

    /**
     * Returns list of all dynamic fields
     * 
     * @return the list of all dynamic fields
     */
    public Map<String, Object> getDynamicFields( )
    {
        Map<String, Object> mapDynamicFields = new HashMap<>( );

        if ( _dfString != null )
        {
            mapDynamicFields.putAll( _dfString );
        }

        if ( _dfDate != null )
        {
            mapDynamicFields.putAll( _dfDate );
        }

        if ( _dfListBox != null )
        {
            mapDynamicFields.putAll( _dfListBox );
        }

        if ( _dfNumericText != null )
        {
            mapDynamicFields.putAll( _dfNumericText );
        }

        if ( _dfText != null )
        {
            mapDynamicFields.putAll( _dfText );
        }

        if ( _dfGeoloc != null )
        {
            mapDynamicFields.putAll( _dfGeoloc );
        }

        if ( _dfGeojson != null )
        {
            mapDynamicFields.putAll( _dfGeojson );
        }

        if ( _dfListDate != null )
        {
            mapDynamicFields.putAll( _dfListDate );
        }
        return mapDynamicFields;
    }

    /**
     * Add a dynamic field
     * 
     * @param strName
     *            the name of the field
     * @param dValue
     *            the value of the field
     */
    public void addDynamicField( String strName, Date dValue )
    {
        if ( _dfDate == null )
        {
            _dfDate = new HashMap<>( );
        }

        _dfDate.put( strName + DYNAMIC_DATE_FIELD_SUFFIX, dValue );
    }

    /**
     * Add a dynamic field
     * 
     * @param strName
     *            the name of the field
     * @param dValue
     *            the value of the field
     */
    public void addDynamicField( String strName, Long lValue )
    {
        if ( _dfNumericText == null )
        {
            _dfNumericText = new HashMap<>( );
        }

        _dfNumericText.put( strName + DYNAMIC_LONG_FIELD_SUFFIX, lValue );
    }

    /**
     * Add a dynamic field
     * 
     * @param strName
     *            the name of the field
     * @param dValue
     *            the value of the field
     */
    public void addDynamicField( String strName, String strValue )
    {
        if ( _dfText == null )
        {
            _dfText = new HashMap<>( );
        }

        _dfText.put( strName + DYNAMIC_TEXT_FIELD_SUFFIX, strValue );
    }

    /**
     * Add a dynamic field
     * 
     * @param strName
     *            the name of the field
     * @param dValue
     *            the value of the field
     */
    public void addDynamicField( String strName, List<String> strValue )
    {
        if ( _dfListBox == null )
        {
            _dfListBox = new HashMap<>( );
        }

        _dfListBox.put( strName + DYNAMIC_LIST_FIELD_SUFFIX, strValue );
    }

    /**
     * Add a dynamic field
     * 
     * @param strName
     *            the name of the field
     * @param dateValue
     *            the value of the field
     */
    public void addDynamicFieldListDate( String strName, List<Date> dateValue )
    {
        if ( _dfListDate == null )
        {
            _dfListDate = new HashMap<>( );
        }

        _dfListDate.put( strName + DYNAMIC_LIST_DATE_FIELD_SUFFIX, dateValue );
    }

    /**
     * Add a dynamic field
     * 
     * @param strName
     *            the name of the field
     * @param dValue
     *            the value of the field
     */
    public void addDynamicFieldNotAnalysed( String strName, String strValue )
    {
        if ( _dfString == null )
        {
            _dfString = new HashMap<>( );
        }

        _dfString.put( strName + DYNAMIC_STRING_FIELD_SUFFIX, strValue );
    }

    /**
     * Add a dynamic field
     * 
     * @param strName
     *            the name of the field
     * @param geolocItem
     *            a GeolocItem
     * @param codeDocumentType
     *            the codeDocumentType
     */
    public void addDynamicFieldGeoloc( String strName, GeolocItem geolocItem, String codeDocumentType )
    {
        if ( geolocItem.getIcon( ) == null )
        {
            geolocItem.setIcon( codeDocumentType + "-" + strName );
        }

        List<Double> coordinates = geolocItem.getLonLat( );
        if ( coordinates != null )
        {
            if ( _dfGeoloc == null )
            {
                _dfGeoloc = new HashMap<>( );
            }

            if ( _dfGeojson == null )
            {
                _dfGeojson = new HashMap<>( );
            }

            String strCoordinates = String.format( Locale.ENGLISH, "%.6f,%.6f", coordinates.get( 1 ), coordinates.get( 0 ) );
            _dfGeoloc.put( strName + DYNAMIC_GEOLOC_FIELD_SUFFIX, strCoordinates );

            _dfGeojson.put( strName + DYNAMIC_GEOJSON_FIELD_SUFFIX, geolocItem.toJSON( ) );

            if ( geolocItem.getAddress( ) != null )
            {
                addDynamicField( strName + DYNAMIC_GEOJSON_ADDRESS_FIELD_SUFFIX, geolocItem.getAddress( ) );
            }
        }
    }

    /**
     * Add a dynamic field
     * 
     * @param strName
     *            the name of the field
     * @param strAdress
     *            the adress of the field
     * @param dLongitude
     *            the longitude of the field
     * @param dLatitude
     *            the latitude of the field
     * @param codeDocumentType
     *            the codeDocumentType
     */
    public void addDynamicFieldGeoloc( String strName, String strAdress, double dLongitude, double dLatitude, String codeDocumentType )
    {
        GeolocItem geolocItem = new GeolocItem( );
        HashMap<String, Object> properties = new HashMap<>( );
        properties.put( GeolocItem.PATH_PROPERTIES_ADDRESS, strAdress );

        HashMap<String, Object> geometry = new HashMap<>( );
        geometry.put( GeolocItem.PATH_GEOMETRY_COORDINATES, Arrays.asList( dLongitude, dLatitude ) );
        geolocItem.setGeometry( geometry );
        geolocItem.setProperties( properties );
        addDynamicFieldGeoloc( strName, geolocItem, codeDocumentType );
    }

    /**
     * Add a dynamic field
     * 
     * @param strName
     *            the name of the field
     * @param strValue
     *            the value of the field which should be a geojson string
     * @param codeDocumentType
     *            the codeDocumentType
     */
    public void addDynamicFieldGeoloc( String strName, String strValue, String codeDocumentType )
    {
        JsonNode object;

        try
        {
            object = new ObjectMapper( ).readTree( strValue );
        }
        catch( IOException e )
        {
            AppLogService.error( "SolrItem: exception during GEOJSON parsing : " + strValue + " : " + e );

            return;
        }

        JsonNode objCoordinates = object.path( GEOLOC_JSON_PATH_GEOMETRY ).path( GEOLOC_JSON_PATH_GEOMETRY_COORDINATES );

        if ( objCoordinates.isMissingNode( ) )
        {
            AppLogService.error( "SolrItem: missing coordinates : " + strValue );
        }
        else
        {
            if ( !objCoordinates.isArray( ) )
            {
                AppLogService.error( "SolrItem: coordinates not an array : " + strValue );
            }
            else
            {
                double [ ] parsedCoordinates = new double [ 2];
                Iterator<JsonNode> it = objCoordinates.getElements( );

                for ( int i = 0; i < parsedCoordinates.length; i++ )
                {
                    if ( !it.hasNext( ) )
                    {
                        AppLogService.error( "SolrItem: coordinates array too short : " + strValue + " at element " + Integer.toString( i ) );
                    }
                    else
                    {
                        JsonNode node = it.next( );
                        if ( !node.isNumber( ) )
                        {
                            AppLogService.error( "SolrItem: coordinate not a number : " + strValue + " at element " + Integer.toString( i ) );
                        }
                        else
                        {
                            parsedCoordinates [i] = node.asDouble( );
                        }
                    }
                }

            }
        }

        GeolocItem geolocItem = null;

        try
        {
            geolocItem = GeolocItem.fromJSON( strValue );
        }
        catch( IOException e )
        {
            AppLogService.error( "SolrItem: Error parsing JSON: " + strValue + "exception: " + e );
        }
        if ( geolocItem != null )
        {
            addDynamicFieldGeoloc( strName, geolocItem, codeDocumentType );
        }
    }

    /**
     * Gets the url
     * 
     * @return the url
     */
    public String getUrl( )
    {
        return _strUrl;
    }

    /**
     * Sets the url
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
        return _lDate;
    }

    /**
     * Sets the date
     * 
     * @param lDate
     *            the date
     */
    public void setDate( Date lDate )
    {
        _lDate = lDate;
    }

    /**
     * Gets the title
     * 
     * @return the title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Sets the title
     * 
     * @param strTitle
     *            the title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Gets the id
     * 
     * @return the id
     */
    public String getUid( )
    {
        return _strUid;
    }

    /**
     * Sets the id
     * 
     * @param strUid
     *            the id
     */
    public void setUid( String strUid )
    {
        _strUid = strUid;
    }

    /**
     * Gets the content
     * 
     * @return the content
     */
    public String getContent( )
    {
        return _strContent;
    }

    /**
     * Sets the content
     * 
     * @param strContent
     *            the content
     */
    public void setContent( String strContent )
    {
        _strContent = strContent;
    }

    /**
     * Gets the site
     * 
     * @return the site name
     */
    public String getSite( )
    {
        return _strSite;
    }

    /**
     * Sets the site name
     * 
     * @param strSite
     *            the site name
     */
    public void setSite( String strSite )
    {
        _strSite = strSite;
    }

    /**
     * Gets the summary
     * 
     * @return the summary
     */
    public String getSummary( )
    {
        return _strSummary;
    }

    /**
     * Sets the summary
     * 
     * @param strSummary
     *            the summary
     */
    public void setSummary( String strSummary )
    {
        _strSummary = strSummary;
    }

    /**
     * Gets the type
     * 
     * @return the type
     */
    public String getType( )
    {
        return _strType;
    }

    /**
     * Sets the type
     * 
     * @param strType
     *            the type
     */
    public void setType( String strType )
    {
        _strType = strType;
    }

    /**
     * Gets the role
     * 
     * @return the role
     */
    public String getRole( )
    {
        return _strRole;
    }

    /**
     * Sets the role
     * 
     * @param strRole
     *            the role
     */
    public void setRole( String strRole )
    {
        _strRole = strRole;
    }

    public String getXmlContent( )
    {
        return _strXmlContent;
    }

    public void setXmlContent( String strXmlContent )
    {
        _strXmlContent = strXmlContent;
    }

    public List<String> getCategorie( )
    {
        return _strCategorie;
    }

    public void setCategorie( List<String> strCategorie )
    {
        _strCategorie = strCategorie;
    }

    public String getHieDate( )
    {
        return _strHieDate;
    }

    public void setHieDate( String strHieDate )
    {
        _strHieDate = strHieDate;
    }

    /**
     * Gets the metadata
     * 
     * @return the metadata
     */
    public String getMetadata( )
    {
        return _strMetadata;
    }

    /**
     * Sets the metadata
     * 
     * @param strMetadata
     *            the metadata
     */
    public void setMetadata( String strMetadata )
    {
        _strMetadata = strMetadata;
    }

    /**
     * Gets the portlet document id
     * 
     * @return the portlet document id
     */
    public String getDocPortletId( )
    {
        return _strDocPortletId;
    }

    /**
     * Sets the portlet document id
     * 
     * @param strDocPortletId
     *            the portlet document id
     */
    public void setDocPortletId( String strDocPortletId )
    {
        _strDocPortletId = strDocPortletId;
    }

    /**
     * @return the strFileContent
     */
    public String getFileContent( )
    {
        return _strFileContent;
    }

    /**
     * @param strFileContent
     *            the strFileContent to set
     */
    public void setFileContent( String strFileContent )
    {
        _strFileContent = strFileContent;
    }
    /**
     * @return the strIdresource
     */
    public String getIdResource( )
    {
        return _strIdresource;
    }

    /**
     * @param strIdresource
     *            the resource id to set
     */
    public void setIdResource( String strIdresource )
    {
    	_strIdresource = strIdresource;
    }
    public void addChildDocument(String strName, SolrItem child) {
    	
   	   if (_childDocuments == null) {
   		   
    	   _childDocuments = new HashMap<>( );
   	   }
   	   Collection<SolrItem> items= _childDocuments.get(strName);
   	   if(items == null) {
   		   
   		   Collection<SolrItem> list =new ArrayList<>();
   		   list.add(child);
   		   _childDocuments.put( strName + FIELD_CHILD_DOCUMENTS , list);
   	   }
   	   else {
	   		items.add(child);
	   		_childDocuments.put(strName + FIELD_CHILD_DOCUMENTS, items);
   	   }
   }

   public void addChildDocuments(String strName, Collection<SolrItem> children) {
	   
       for (SolrItem child : children) {
   	      addChildDocument(strName, child);
   	    }
   }
   /** Returns the list of child documents, or null if none. */
   public Map<String, Collection<SolrItem>> getChildDocuments( ) {
     return _childDocuments;
   }
   
   /** Returns the list of child documents, or null if none. */
   public Map<String, Collection<SolrInputDocument>> getChilSolrInputDocument( ) 
   {
	   Map<String, Collection<SolrInputDocument>> childSolrInputDocument = new HashMap<>( );
	   if(hasChildDocuments( )) {
		   
		   _childDocuments.forEach( ( key, value) ->{
			   Collection<SolrInputDocument> coll= new ArrayList<>( );
			   value.forEach( child -> 
					   coll.add( SolrIndexerService.solrItem2SolrInputDocument(child))
				);
			   childSolrInputDocument.put(key, coll);
		   });
		   
	   }
     return childSolrInputDocument;
   }

   public boolean hasChildDocuments() {
     boolean isEmpty = (_childDocuments == null || _childDocuments.isEmpty());
     return !isEmpty;
   }

}
