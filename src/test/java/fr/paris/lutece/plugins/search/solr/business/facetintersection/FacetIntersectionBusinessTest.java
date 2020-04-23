package fr.paris.lutece.plugins.search.solr.business.facetintersection;

import java.util.List;

import fr.paris.lutece.plugins.search.solr.business.field.Field;
import fr.paris.lutece.test.LuteceTestCase;

public class FacetIntersectionBusinessTest extends LuteceTestCase
{

    public void testCRUD( )
    {
        Field f1 = new Field( );
        f1.setIdField( 1 );
        
        Field f2 = new Field( );
        f2.setIdField( 2 );
        
        FacetIntersection facetIntersection = new FacetIntersection( );
        facetIntersection.setField1( f1 );
        facetIntersection.setField2( f2 );
        
        FacetIntersectionHome.create( facetIntersection );
        
        List<FacetIntersection> list = FacetIntersectionHome.getFacetIntersectionsList( );
        assertEquals( 1, list.size( ) );
        
        FacetIntersectionHome.remove( f1.getIdField( ), f2.getIdField( ) );
        list = FacetIntersectionHome.getFacetIntersectionsList( );
        assertEquals( 0, list.size( ) );
    }
}
