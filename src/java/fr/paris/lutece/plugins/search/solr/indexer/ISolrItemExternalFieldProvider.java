package fr.paris.lutece.plugins.search.solr.indexer;

import java.util.Collection;

/**
 * This interface is used for provisioning external fields to solrItem
 */
public interface ISolrItemExternalFieldProvider {

    /**
     * Provide external fields to SolrItems objects
     * 
     * @param SolrItems
     *            list of solrItem objects
     *
     */
    public void provideFields( Collection<SolrItem> solrItems );
}
