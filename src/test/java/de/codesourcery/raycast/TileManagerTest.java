/**
 * Copyright 2014 Tobias Gierke <tobias.gierke@code-sourcery.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codesourcery.raycast;

import junit.framework.TestCase;

public class TileManagerTest extends TestCase {

	private TileManager tileManager;
	
	@Override
	protected void setUp() throws Exception 
	{
		final TileFactory tileFactory = new TileFactory(3) {
			@Override
			public Tile createTile(TileId coordinates) 
			{
				return new Tile(coordinates,tileSize,newWallArray());
			}
		};
		
		tileManager = new TileManager( tileFactory );
	}
	
	public void testToLocalCoordinates() {
		assertEquals( vec2d(0,1) , tileManager.toLocalCoordinates( tileId(0,0), -1, 0 ) );			
		assertEquals( vec2d(1,1) , tileManager.toLocalCoordinates( tileId(0,0),  0, 0 ) );
		assertEquals( vec2d(2,1) , tileManager.toLocalCoordinates( tileId(0,0),  1, 0 ) );		
		
		assertEquals( vec2d(1,0) , tileManager.toLocalCoordinates( tileId(0,0), 0, -1 ) );			
		assertEquals( vec2d(1,1) , tileManager.toLocalCoordinates( tileId(0,0), 0,  0 ) );
		assertEquals( vec2d(1,2) , tileManager.toLocalCoordinates( tileId(0,0), 0,  1 ) );			
	}
	
	public void testGetTileOrigin() {
		
		assertEquals( vec2d(0,0) , tileManager.getOrigin( tileId(0,0) ) );
		assertEquals( vec2d(3,0) , tileManager.getOrigin( tileId(1,0) ) );
		assertEquals( vec2d(-3,0) , tileManager.getOrigin( tileId(-1,0) ) );		
		
		assertEquals( vec2d(0,0) , tileManager.getOrigin( tileId(0,0) ) );
		assertEquals( vec2d(0,3) , tileManager.getOrigin( tileId(0,1) ) );
		assertEquals( vec2d(0,-3) , tileManager.getOrigin( tileId(0,-1) ) );			
	}
	
	public void testTileAtOrigin() {
		
		for ( int x = -1 ; x <= 1 ; x++ ) {
			for ( int y = -1 ; y <= 1 ; y++ ) {
				assertEquals( tileId(0,0) , tileManager.getTileId( x , y ) );
			}
		}
	}
	
	public void testTileAboveOrigin() {
		
		for ( int x = -1 ; x <= 1 ; x++ ) {
			for ( int y = -4 ; y <= -2 ; y++ ) {
				assertEquals( tileId(0,-1) , tileManager.getTileId( x , y ) );
			}
		}
	}	
	
	public void testTileBelowOrigin() {
		
		for ( int x = -1 ; x <= 1 ; x++ ) {
			for ( int y = 2 ; y <= 4 ; y++ ) {
				assertEquals( tileId(0,1) , tileManager.getTileId( x , y ) );
			}
		}
	}	
	
	public void testTileLeftOfOrigin() {
		
		for ( int x = -4 ; x <= -2 ; x++ ) {
			for ( int y = -1 ; y <= 1 ; y++ ) {
				assertEquals( "x="+x+",y="+y , tileId(-1,0) , tileManager.getTileId( x , y ) );
			}
		}
	}	
	
	public void testTileRightOfOrigin() {
		
		for ( int x = 2 ; x <= 4 ; x++ ) {
			for ( int y = -1 ; y <= 1 ; y++ ) {
				assertEquals( tileId(1,0) , tileManager.getTileId( x , y ) );
			}
		}
	}		
	
	private static Vec2d vec2d(double x,double y) {
		return new Vec2d(x,y);
	}
	
	private static TileId tileId(int x,int y) {
		return new TileId(x,y);
	}
}
