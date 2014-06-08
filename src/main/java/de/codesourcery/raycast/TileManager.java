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

import java.util.LinkedHashMap;
import java.util.Random;

public class TileManager {

	private final TileFactory tileFactory;

	private final double tileSize;
	private final double halfTileSize;
	
	private Tile lastAccessedTile;
	private final LinkedHashMap<TileId, Tile> tileCache = new LinkedHashMap<TileId,Tile>(10,0.75f,true) 
	{
		protected boolean removeEldestEntry(java.util.Map.Entry<TileId,Tile> eldest) {
			if( size() > 10 ) {
				System.out.println("Unloading tile "+eldest.getKey());
				return true;
			}
			return false;
		}
	};
	
	public TileManager(TileFactory tileFactory) {
		this.tileFactory = tileFactory;
		this.tileSize = tileFactory.tileSize;
		this.halfTileSize = tileFactory.tileSize/2;
		this.lastAccessedTile = tileFactory.createTile( new TileId(0,0 ) );
	}
	
	public int tileSize() {
		return tileFactory.tileSize;
	}
	
	public Tile getTile(TileId tileId) 
	{
		if ( lastAccessedTile.tileId.x == tileId.x && lastAccessedTile.tileId.y == tileId.y ) {
			return lastAccessedTile;
		}
		Tile cached = tileCache.get( tileId );
		if ( cached == null ) {
			cached = createTile( tileId );
			tileCache.put( tileId,cached);
		}
		lastAccessedTile = cached;
		return cached;
	}
	
	public Vec2d getStartingPosition() 
	{
		final Tile tile = getTile( new TileId(0,0) );
		
		final Random rnd = new Random(0xdeadbeef);
		while( true ) 
		{
			int x = rnd.nextInt( (int) tileSize );
			int y = rnd.nextInt( (int) tileSize );
				if ( tile.isFree( x , y ) ) {
					double x0 = (double) x - Math.floor( tileSize/2.0);
					double y0 = (double) y - Math.floor( tileSize/2.0);
					return new Vec2d(x0,y0);
				}
		}
	}
	
	private Tile createTile(TileId tileId) 
	{
		System.out.println("Creating tile "+tileId);		
		return tileFactory.createTile( tileId );
	}	
	
	protected final TileId getTileId(Vec2d globalCoordinates) {
		return getTileId( globalCoordinates.x , globalCoordinates.y );
	}
	
	protected final TileId getTileId(double globalX,double globalY) 
	{
		final int tileX = (int) Math.floor( (globalX + halfTileSize) / tileSize);
		final int tileY = (int) Math.floor( (globalY + halfTileSize) / tileSize);
		return new TileId(tileX,tileY);
	}
	
	protected final Vec2d getOrigin(TileId tileId) {
		double x0 = tileId.x * tileSize;
		double y0 = tileId.y * tileSize;
		return new Vec2d(x0 , y0 );
	}
	
	public final Vec2d toLocalCoordinates(TileId tileId, double globalX , double globalY ) {
		final Vec2d tileOrigin = getOrigin(tileId);
		double locX = (int) halfTileSize + (globalX - tileOrigin.x);
		double locY = halfTileSize + (globalY - tileOrigin.y );
		return new Vec2d(locX,locY);
	}
	
	public final Wall getWallSlow(double globalX,double globalY)
	{
		final TileId tileId = getTileId( globalX , globalY);
		final Vec2d local = toLocalCoordinates( tileId , globalX, globalY);
		return getTile( tileId ).getWall( (int) local.x ,  (int) local.y );		
	}
	
	public final Wall getWallFast(double globalX,double globalY)
	{
		// same algorithm as getWallSlow() but without all the intermediate object creation
		final int tileX = (int) Math.floor( (globalX + halfTileSize) / tileSize);
		final int tileY = (int) Math.floor( (globalY + halfTileSize) / tileSize);
		final TileId tileId = new TileId(tileX,tileY);
		final double tileOriginX = tileId.x * tileSize;
		final double tileOriginY = tileId.y * tileSize;
		final double locX = halfTileSize + (globalX - tileOriginX);
		final double locY = halfTileSize + (globalY - tileOriginY );
		return getTile( tileId ).getWall( (int) locX ,  (int) locY );		
	}	
}