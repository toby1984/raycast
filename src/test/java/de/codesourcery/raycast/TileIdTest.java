package de.codesourcery.raycast;

import junit.framework.TestCase;

public class TileIdTest extends TestCase {

	private static final int TILE_SIZE = 3;
	
	public void testGlobalCoordsToTileIdX() 
	{
		assertEquals( tileId( -1 , 0 ) , toTileId( -1 , 0 ) );
		assertEquals( tileId( -1 , 0 ) , toTileId( -2 , 0 ) );	
		assertEquals( tileId( -1 , 0 ) , toTileId( -3 , 0 ) );
		assertEquals( tileId( -2 , 0 ) , toTileId( -4 , 0 ) );
		                                           
		assertEquals( tileId( 0 , 0 )  , toTileId( 0 , 0  ) );
		assertEquals( tileId( 0 , 0 )  , toTileId( 1 , 0  ) );
		assertEquals( tileId( 0 , 0 )  , toTileId( 2 , 0  ) );
		assertEquals( tileId( 1 , 0 )  , toTileId( 3 , 0  ) );
	}
	
	public void testGlobalCoordsToTileIdY() 
	{
		assertEquals( tileId( 0 , -1 ) , toTileId( 0 , -1 ) );
		assertEquals( tileId( 0 , -1 ) , toTileId( 0 , -2 ) );	
		assertEquals( tileId( 0 , -1 ) , toTileId( 0 , -3 ) );
		assertEquals( tileId( 0 , -2 ) , toTileId( 0 , -4 ) );
		                                           
		assertEquals( tileId( 0 , 0 )  , toTileId( 0 , 0  ) );
		assertEquals( tileId( 0 , 0 )  , toTileId( 0 , 1  ) );
		assertEquals( tileId( 0 , 0 )  , toTileId( 0 , 2  ) );
		assertEquals( tileId( 0 , 1 )  , toTileId( 0 , 3  ) );
	}	
	
	public void testGlobalToLocal() 
	{
		checkGlobalToLocal( global(-4,0) , local(2,0) );		
		checkGlobalToLocal( global(-3,0) , local(0,0) );		
		checkGlobalToLocal( global(-2,0) , local(1,0) );
		checkGlobalToLocal( global(-1,0) , local(2,0) );	
		
		checkGlobalToLocal( global(0,0) , local(0,0) );
		
		checkGlobalToLocal( global(1,0) , local(1,0) );
		checkGlobalToLocal( global(2,0) , local(2,0) );
		checkGlobalToLocal( global(3,0) , local(0,0) );		
	}
	
	private void checkGlobalToLocal(GlobalCoordinates global,LocalCoordinates expected) 
	{
		final TileId tileId = toTileId( global );
		final LocalCoordinates local = global.toLocalCoordinates( tileId ,  TILE_SIZE );
		System.out.println( global+" resolves to "+tileId+" with "+local);
		assertEquals( expected , local );				
	}
	
	public void testGlobalToLocalToGlobal() 
	{
		for ( int x = -6 ; x <= 6 ; x++ ) {
			for ( int y = -6 ; y <= 6 ; y++ ) {
				checkGlobalToLocalToGlobal(x,y);
			}
		}
	}
	
	private void checkGlobalToLocalToGlobal(double globalX,double globalY) 
	{
		GlobalCoordinates global = global(globalX,globalY);
		TileId tileId = toTileId( global );
		
		LocalCoordinates local = tileId.toLocalCoordinates( global , TILE_SIZE );
		GlobalCoordinates converted = local.toGlobalCoordinates( tileId , TILE_SIZE );
		System.out.println( local+" on tile "+tileId+" => "+converted);
		assertEquals( global , converted );
	}
	
	private static GlobalCoordinates global(double x,double y) {
		return new GlobalCoordinates(x,y);
	}
	
	private static LocalCoordinates local(double x,double y) {
		return new LocalCoordinates(x,y);
	}	
	
	private TileId toTileId(double x,double y) {
		final TileId result = TileId.toTileId( global( x , y ), TILE_SIZE);
		System.out.println("("+x+","+y+") => "+result);
		return result;
	}
	
	private TileId toTileId(GlobalCoordinates coords) {
		return toTileId(coords.x,coords.y);
	}	
	
	private static TileId tileId(int x,int y) {
		return new TileId(x,y);
	}
}
