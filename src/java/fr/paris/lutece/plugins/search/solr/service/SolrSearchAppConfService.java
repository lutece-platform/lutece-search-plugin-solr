/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
    private static final String DSKEY_TEMPLATE = ".template";
    private static final String DSKEY_MAPPING = ".mapping";

    public static SolrSearchAppConf loadConfiguration( String code )
    {
        String strSafeCode = ( code == null ) ? EMPTY_CODE : code;
        ReferenceList referenceList = DatastoreService.getDataByPrefix( DSKEY_PREFIX + strSafeCode + "." );

        if ( referenceList.isEmpty(  ) && !EMPTY_CODE.equals( strSafeCode ) )
        {
            return null;
        }

        SolrSearchAppConf conf = new SolrSearchAppConf(  );
        conf.setCode( strSafeCode );

        for ( ReferenceItem referenceItem : referenceList )
        {
            if ( referenceItem.getCode(  ).endsWith( DSKEY_FQ ) )
            {
                conf.setFilterQuery( referenceItem.getName(  ) );
            }
            else if ( referenceItem.getCode(  ).endsWith( DSKEY_TEMPLATE ) )
            {
                conf.setTemplate( referenceItem.getName(  ) );
            }
            else if ( referenceItem.getCode(  ).endsWith( DSKEY_MAPPING ) )
            {
                conf.setExtraMappingQuery( SolrConstants.CONSTANT_TRUE.equals( referenceItem.getName(  ) ) );
            }
        }

        return conf;
    }

    public static void saveConfiguration( SolrSearchAppConf conf )
    {
        String code = ( conf.getCode(  ) != null ) ? conf.getCode(  ) : EMPTY_CODE;
        DatastoreService.setDataValue( DSKEY_PREFIX + code + DSKEY_INSTALLED, SolrConstants.CONSTANT_TRUE );
        DatastoreService.setDataValue( DSKEY_PREFIX + code + DSKEY_FQ, conf.getFilterQuery(  ) );
        DatastoreService.setDataValue( DSKEY_PREFIX + code + DSKEY_TEMPLATE, conf.getTemplate(  ) );
        DatastoreService.setDataValue( DSKEY_PREFIX + code + DSKEY_MAPPING,
            conf.getExtraMappingQuery(  ) ? SolrConstants.CONSTANT_TRUE : SolrConstants.CONSTANT_FALSE );
    }
}
