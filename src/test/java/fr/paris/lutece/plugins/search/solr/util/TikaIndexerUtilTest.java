package fr.paris.lutece.plugins.search.solr.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ContentHandler;

import fr.paris.lutece.plugins.search.solr.indexer.SolrItem;
import fr.paris.lutece.test.LuteceTestCase;

public class TikaIndexerUtilTest extends LuteceTestCase
{

    public void testParseHtml( ) throws LuteceSolrException
    {
        String content = "<div><p>Hello World !</p></div>";
        ContentHandler handler = TikaIndexerUtil.parseHtml( content );
        assertEquals( "Hello World !", handler.toString( ).trim( ) );
    }
    
    public void testAddFileContentToSolrItem( ) throws LuteceSolrException
    {
        SolrItem item = new SolrItem( );
        List<byte [ ]> contentList = new ArrayList<>( );
        contentList.add( "<div><p>Hello World !</p></div>".getBytes( ) );
        contentList.add( "<div><p>Goodbye</p></div>".getBytes( ) );
        TikaIndexerUtil.addFileContentToSolrItem( item, contentList );
        
        assertTrue( item.getFileContent( ).trim( ).startsWith( "Hello World !" ) );
        assertTrue( item.getFileContent( ).trim( ).endsWith( "Goodbye" ) );
    }
}
