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
package fr.paris.lutece.plugins.search.solr.service;

import java.util.List;

import fr.paris.lutece.plugins.search.solr.business.SolrSearchAppConf;
import fr.paris.lutece.plugins.search.solr.util.SolrConstants;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 *
 * SolrSearchApp
 *
 */
public class SolrSearchAppConfService
{
    public static final String EMPTY_CODE = "";
    private static final String DSKEY_PREFIX = "solr.app.conf.";
    private static final String DSKEY_INSTALLED = ".installed";
    private static final String DSKEY_FQ = ".fq";
    private static final String DSKEY_FL = ".fl";
    private static final String DSKEY_TEMPLATE = ".template";
    private static final String DSKEY_MAPPING = ".mapping";
    private static final String DSKEY_ADDON_BEANS = ".addonBeans.";

    private SolrSearchAppConfService( )
    {
    }

    public static SolrSearchAppConf loadConfiguration( String code )
    {
        String strSafeCode = ( code == null ) ? EMPTY_CODE : code;
        String strPrefix = DSKEY_PREFIX + strSafeCode;
        ReferenceList referenceList = DatastoreService.getDataByPrefix( strPrefix + "." );

        if ( referenceList.isEmpty( ) && !EMPTY_CODE.equals( strSafeCode ) )
        {
            return null;
        }

        SolrSearchAppConf conf = new SolrSearchAppConf( );
        conf.setCode( strSafeCode );

        String strAddonPrefix = strPrefix + DSKEY_ADDON_BEANS;
        for ( ReferenceItem referenceItem : referenceList )
        {
            String referenceItemCode = referenceItem.getCode( );
            String referenceItemName = referenceItem.getName( );
            if ( referenceItemCode.endsWith( DSKEY_FQ ) )
            {
                conf.setFilterQuery( referenceItemName );
            }
            else if(referenceItemCode.endsWith( DSKEY_FL )){
            	conf.setFieldList( referenceItemName );
            }
            else
                if ( referenceItemCode.endsWith( DSKEY_TEMPLATE ) )
                {
                    conf.setTemplate( referenceItemName );
                }
                else
                    if ( referenceItemCode.endsWith( DSKEY_MAPPING ) )
                    {
                        conf.setExtraMappingQuery( SolrConstants.CONSTANT_TRUE.equals( referenceItemName ) );
                    }
                    else
                        if ( referenceItemCode.startsWith( strAddonPrefix ) )
                        {
                            conf.getAddonBeanNames( ).add( referenceItemName );
                        }
        }

        return conf;
    }

    public static void saveConfiguration( SolrSearchAppConf conf )
    {
        String strSafeCode = ( conf.getCode( ) != null ) ? conf.getCode( ) : EMPTY_CODE;
        String strPrefix = DSKEY_PREFIX + strSafeCode;
        DatastoreService.setDataValue( strPrefix + DSKEY_INSTALLED, SolrConstants.CONSTANT_TRUE );
        DatastoreService.setDataValue( strPrefix + DSKEY_FQ, conf.getFilterQuery( ) );
        DatastoreService.setDataValue( strPrefix + DSKEY_TEMPLATE, conf.getTemplate( ) );
        DatastoreService.setDataValue( strPrefix + DSKEY_MAPPING, conf.getExtraMappingQuery( ) ? SolrConstants.CONSTANT_TRUE : SolrConstants.CONSTANT_FALSE );
        List<String> listAddonBeanNames = conf.getAddonBeanNames( );
        for ( int i = 0; i < listAddonBeanNames.size( ); i++ )
        {
            DatastoreService.setDataValue( strPrefix + DSKEY_ADDON_BEANS + i, listAddonBeanNames.get( i ) );
        }
    }
}
