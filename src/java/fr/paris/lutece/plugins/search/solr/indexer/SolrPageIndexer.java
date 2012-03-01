/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
import fr.paris.lutece.plugins.search.solr.util.SolrConstants;
import fr.paris.lutece.plugins.search.utils.SolrPageIndexerUtils;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.lucene.demo.html.HTMLParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;


/**
 * The indexer service for Solr.
 *
 */
public class SolrPageIndexer implements SolrIndexer
{
    public static final String NAME = "SolrPageIndexer";
    private static final String PARAMETER_PAGE_ID = "page_id";
    private static final String DESCRIPTION = "Solr page Indexer";
    private static final String VERSION = "1.0.0";
    private static final String TYPE = "PAGE";
    private static final String CATEGORIE = "Html";
    private static final String PROPERTY_INDEXER_ENABLE = "solr.indexer.page.enable";
    private static final String BEAN_PAGE_SERVICE = "pageService";
    private static final String SHORT_NAME = "page";
    private static final List<String> LIST_RESSOURCES_NAME = new ArrayList<String>(  );
    private static final String PAGE_INDEXATION_ERROR = "[SolrPageIndexer] An error occured during the indexation of the page number ";

    /**
     * Creates a new SolrPageIndexer
     */
    public SolrPageIndexer(  )
    {
        LIST_RESSOURCES_NAME.add( SolrPageIndexerUtils.RESSOURCE_PAGE );
    }

    /**
     * {@inheritDoc}
     */
    public List<String> indexDocuments(  )
    {
        List<Page> listPages = PageHome.getAllPages(  );
        List<String> lstErrors = new ArrayList<String>(  );

        for ( Page page : listPages )
        {
            try
            {
                // Generates the item to index
                SolrItem item = getItem( page, SolrIndexerService.getBaseUrl(  ) );

                if ( item != null )
                {
                    SolrIndexerService.write( item );
                }
            }
            catch ( Exception e )
            {
                lstErrors.add( SolrIndexerService.buildErrorMessage( e ) );
                AppLogService.error( PAGE_INDEXATION_ERROR + page.getId(  ), e );
            }
        }

        return lstErrors;
    }

    /**
    * Builds a document which will be used by Lucene during the indexing of the pages of the site with the following
    * fields : summary, uid, url, contents, title and description.
    * @return the built Document
    * @param strUrl The base URL for documents
    * @param page the page to index
    * @throws IOException The IO Exception
    * @throws InterruptedException The InterruptedException
    * @throws SiteMessageException occurs when a site message need to be displayed
    */
    private SolrItem getItem( Page page, String strUrl )
        throws IOException, InterruptedException, SiteMessageException
    {
        // the item
        SolrItem item = new SolrItem(  );

        // indexing page content
        String strPageContent = ( (IPageService) SpringContextService.getBean( BEAN_PAGE_SERVICE ) ).getPageContent( page.getId(  ),
                0, null );
        StringReader readerPage = new StringReader( strPageContent );
        HTMLParser parser = new HTMLParser( readerPage );

        //the content of the article is recovered in the parser because this one
        //had replaced the encoded caracters (as &eacute;) by the corresponding special caracter (as ?)
        Reader reader = parser.getReader(  );
        int c;
        StringBuilder sb = new StringBuilder(  );

        while ( ( c = reader.read(  ) ) != -1 )
        {
            sb.append( String.valueOf( (char) c ) );
        }

        reader.close(  );

        item.setContent( sb.toString(  ) );
        item.setTitle( page.getName(  ) );
        item.setRole( page.getRole(  ) );

        if ( ( page.getDescription(  ) != null ) && ( page.getDescription(  ).length(  ) > 1 ) )
        {
            item.setSummary( page.getDescription(  ) );
        }

        item.setType( TYPE );
        item.setSite( SolrIndexerService.getWebAppName(  ) );

        List<String> cat = new ArrayList<String>(  );
        cat.add( CATEGORIE );
        item.setCategorie( cat );
        item.setDate( page.getDateUpdate(  ) );

        UrlItem urlItem = new UrlItem( strUrl );
        urlItem.addParameter( PARAMETER_PAGE_ID, page.getId(  ) );
        item.setUrl( urlItem.getUrl(  ) );
        item.setUid( getResourceUid( String.valueOf( page.getId(  ) ), SolrPageIndexerUtils.RESSOURCE_PAGE ) );

        return item;
    }

    /**
     * Returns the name of the indexer.
     * @return the name of the indexer
     */
    public String getName(  )
    {
        return NAME;
    }

    /**
     * Returns the version.
     * @return the version.
     */
    public String getVersion(  )
    {
        return VERSION;
    }

    /**
         * {@inheritDoc}
         */
    public String getDescription(  )
    {
        return DESCRIPTION;
    }

    /**
         * {@inheritDoc}
         */
    public boolean isEnable(  )
    {
        return "true".equalsIgnoreCase( AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE ) );
    }

    /**
     * {@inheritDoc}
     */
    public List<Field> getAdditionalFields(  )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<SolrItem> getDocuments( String strIdDocument )
    {
        List<SolrItem> lstItems = new ArrayList<SolrItem>(  );

        try
        {
            int nIdDocument = Integer.parseInt( strIdDocument );
            Page page = PageHome.getPage( nIdDocument );
            lstItems.add( getItem( page, SolrIndexerService.getBaseUrl(  ) ) );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        return lstItems;
    }

    /**
         * {@inheritDoc}
         */
    public List<String> getResourcesName(  )
    {
        return LIST_RESSOURCES_NAME;
    }

    /**
         * {@inheritDoc}
         */
    public String getResourceUid( String strResourceId, String strResourceType )
    {
        StringBuffer sb = new StringBuffer( strResourceId );
        sb.append( SolrConstants.CONSTANT_UNDERSCORE ).append( SHORT_NAME );

        return sb.toString(  );
    }
}
