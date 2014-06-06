package de.codesourcery.raycast;

import java.util.LinkedHashMap;

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
	
	private Tile getTile(TileId tileId) 
	{
		if ( lastAccessedTile.tileId.equals(tileId )) {
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
	
	private Tile createTile(TileId tileId) 
	{
		System.out.println("Creating tile "+tileId);		
		return tileFactory.createTile( tileId );
	}	
	
	protected final TileId getTileId(Vec2d global) {
		return getTileId( global.x , global.y );
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
	
	protected final Vec2d toLocalCoordinates(TileId tileId, double globalX , double globalY ) {
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