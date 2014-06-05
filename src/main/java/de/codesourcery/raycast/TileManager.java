package de.codesourcery.raycast;

public class TileManager {

	private final TileFactory tileFactory;
	
	public TileManager(TileFactory tileFactory) {
		this.tileFactory = tileFactory;
	}
	
	public Tile getTile(GlobalCoordinates coordinates) {
		return createTile( coordinates );
	}
	
	private Tile createTile(GlobalCoordinates coordinates) 
	{
		return tileFactory.createTile( TileId.toTileId( coordinates , tileFactory.tileSize) );
	}
}
