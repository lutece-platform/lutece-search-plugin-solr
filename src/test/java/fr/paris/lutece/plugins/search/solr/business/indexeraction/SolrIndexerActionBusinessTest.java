package fr.paris.lutece.plugins.search.solr.business.indexeraction;

import java.util.List;

import fr.paris.lutece.plugins.search.solr.service.SolrPlugin;
import fr.paris.lutece.test.LuteceTestCase;

public class SolrIndexerActionBusinessTest extends LuteceTestCase
{
    public void testCRUD( )
    {
        SolrIndexerAction action = new SolrIndexerAction( );
        action.setIdDocument( "strIdDocument" );
        action.setIdPortlet( 14 );
        action.setIdTask( 15 );
        action.setTypeResource( "strTypeResource" );
        
        SolrIndexerActionHome.create( action, SolrPlugin._plugin );
        SolrIndexerAction loaded = SolrIndexerActionHome.findByPrimaryKey( action.getIdAction( ), SolrPlugin._plugin );
        assertEquals( action.getIdDocument( ), loaded.getIdDocument( ) );
        assertEquals( action.getIdPortlet( ), loaded.getIdPortlet( ) );
        assertEquals( action.getIdTask( ), loaded.getIdTask( ) );
        assertEquals( action.getTypeResource( ), loaded.getTypeResource( ) );
        
        action.setIdDocument( "strIdDocument2" );
        action.setIdPortlet( 114 );
        action.setIdTask( 115 );
        action.setTypeResource( "strTypeResource2" );
        
        SolrIndexerActionHome.update( action, SolrPlugin._plugin );
        loaded = SolrIndexerActionHome.findByPrimaryKey( action.getIdAction( ), SolrPlugin._plugin );
        assertEquals( action.getIdDocument( ), loaded.getIdDocument( ) );
        assertEquals( action.getIdPortlet( ), loaded.getIdPortlet( ) );
        assertEquals( action.getIdTask( ), loaded.getIdTask( ) );
        assertEquals( action.getTypeResource( ), loaded.getTypeResource( ) );
        
        List<SolrIndexerAction> list = SolrIndexerActionHome.getList( SolrPlugin._plugin );
        assertEquals( 1, list.size( ) );
        
        SolrIndexerActionHome.remove( action.getIdAction( ), SolrPlugin._plugin );
        loaded = SolrIndexerActionHome.findByPrimaryKey( action.getIdAction( ), SolrPlugin._plugin );
        assertNull( loaded );
    }
}
