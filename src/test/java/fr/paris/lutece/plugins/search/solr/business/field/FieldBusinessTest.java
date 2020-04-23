package fr.paris.lutece.plugins.search.solr.business.field;

import java.util.List;

import fr.paris.lutece.test.LuteceTestCase;

public class FieldBusinessTest extends LuteceTestCase
{

    public void testCRUD( )
    {
        Field field = new Field( );
        field.setName( "strName" );
        field.setDescription( "strDescription" );
        field.setLabel( "strLabel" );
        field.setEnableFacet( true );
        field.setEnableSort( true );
        field.setFacetMincount( 22 );
        field.setIsFacet( true );
        field.setIsSort( true );
        field.setOperator( "strOperator" );
        field.setWeight( 5D );
        field.setDefaultSort( true );
        
        FieldHome.create( field );
        Field loaded = FieldHome.findByPrimaryKey( field.getIdField( ) );
        assertEquals( field.getName( ), loaded.getName( ) );
        assertEquals( field.getDescription( ), loaded.getDescription( ) );
        assertEquals( field.getLabel( ), loaded.getLabel( ) );
        assertEquals( field.getEnableFacet( ), loaded.getEnableFacet( ) );
        assertEquals( field.getEnableSort( ), loaded.getEnableSort( ) );
        assertEquals( field.getFacetMincount( ), loaded.getFacetMincount( ) );
        assertEquals( field.getIsFacet( ), loaded.getIsFacet( ) );
        assertEquals( field.getIsSort( ), loaded.getIsSort( ) );
        assertEquals( field.getOperator( ), loaded.getOperator( ) );
        assertEquals( field.getWeight( ), loaded.getWeight( ) );
        assertEquals( field.getDefaultSort( ), loaded.getDefaultSort( ) );
        
        field.setName( "strName2" );
        field.setDescription( "strDescription2" );
        field.setLabel( "strLabel2" );
        field.setEnableFacet( false );
        field.setEnableSort( false );
        field.setFacetMincount( 20 );
        field.setIsFacet( false );
        field.setIsSort( false );
        field.setOperator( "strOperator" );
        field.setWeight( 3D );
        field.setDefaultSort( false );
        
        FieldHome.update( field );
        loaded = FieldHome.findByPrimaryKey( field.getIdField( ) );
        assertEquals( field.getName( ), loaded.getName( ) );
        assertEquals( field.getDescription( ), loaded.getDescription( ) );
        assertEquals( field.getLabel( ), loaded.getLabel( ) );
        assertEquals( field.getEnableFacet( ), loaded.getEnableFacet( ) );
        assertEquals( field.getEnableSort( ), loaded.getEnableSort( ) );
        assertEquals( field.getFacetMincount( ), loaded.getFacetMincount( ) );
        assertEquals( field.getIsFacet( ), loaded.getIsFacet( ) );
        assertEquals( field.getIsSort( ), loaded.getIsSort( ) );
        assertEquals( field.getOperator( ), loaded.getOperator( ) );
        assertEquals( field.getWeight( ), loaded.getWeight( ) );
        assertEquals( field.getDefaultSort( ), loaded.getDefaultSort( ) );
        
        List<Field> list = FieldHome.getFieldsList( );
        // 8 fields in the init db script
        assertEquals( 9, list.size( ) );
        
        FieldHome.remove( field.getIdField( ) );
        
        loaded = FieldHome.findByPrimaryKey( field.getIdField( ) );
        assertNull( loaded );
    }
}
