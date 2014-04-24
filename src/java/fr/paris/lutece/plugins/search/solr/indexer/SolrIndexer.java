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
package fr.paris.lutece.plugins.search.solr.indexer;

import fr.paris.lutece.plugins.search.solr.business.field.Field;

import java.util.List;


/**
 *
 * SolrIndexer
 *
 */
public interface SolrIndexer
{
    /**
    * Index all solr documents from the plugin, replace  List<SolrItem> getDocuments(  ) method
    * @return A list of error messages or null if there is no error
    */
    List<String> indexDocuments(  );

    /**
     * Returns the name of the indexer.
     * @return the name of the indexer
     */
    String getName(  );

    /**
     * Returns the version.
     * @return the version.
     */
    String getVersion(  );

    /**
     * Returns the description.
     * @return the description.
     */
    String getDescription(  );

    /**
     * Tells whether the service is enable or not
     * @return true if enable, otherwise false
     */
    boolean isEnable(  );

    /**
     * Returns the list of dynamic fields. The name of the fields must end with a {@link SolrItem} suffix (SolrItem.DYNAMIC_URL_FIELD_SUFFIX, SolrItem.DYNAMIC_TEXT_FIELD_SUFFIX ...)
     * @return the list of additional indexed fields or null if there are no additional fields
     */
    List<Field> getAdditionalFields(  );

    /**
     * Returns a list of SolR documents to add to the index
     * @param strIdDocument the identifier of the document to convert into a {@link SolrItem} object
     * @return A list of documents corresponding to the resource identifier. If nothing has been found, returns null
     */
    List<SolrItem> getDocuments( String strIdDocument );

    /**
     * Returns the name of the indexed resources. To prevent conflicts, the names must begin with PluginName_ where PluginName is the name of the plugin.
     * @return The list of names of the indexed resources or null
     */
    List<String> getResourcesName(  );

    /**
     * Return the uid of a resource
     * @param strResourceId the identifier of the resource
     * @param strResourceType the type of the resource
     * @return the uid of a resource or null if the resource is unknow
     */
    String getResourceUid( String strResourceId, String strResourceType );
}
