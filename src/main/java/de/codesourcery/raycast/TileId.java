package de.codesourcery.raycast;

public final class TileId {

	public final int x;
	public final int y;
	
	public TileId(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		return 31 * (31 + x) + y;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if ( obj instanceof TileId ) {
			TileId other = (TileId ) obj;
			return other.x == this.x && other.y == this.y;
		}
		return false;
	}
	
	public static TileId toTileId(GlobalCoordinates coordinates,int tileSize) 
	{
		int x = (int) coordinates.x;
		int tileX;
		if ( x >= 0 ) {
			tileX = x / tileSize;
		} else {
			tileX = -1 - ( -(x+1) / tileSize );				
		}
		
		int y = (int) coordinates.y;
		int tileY;
		if ( y >= 0 ) {
			tileY = y / tileSize;
		} else {
			tileY = -1 - ( -(y+1) / tileSize );			
		}
		return new TileId( tileX , tileY );
	}	
	
	@Override
	public String toString() {
		return "TileId[x=" + x + ", y=" + y + "]";
	}

	public LocalCoordinates toLocalCoordinates(GlobalCoordinates global, int tileSize) {
		return global.toLocalCoordinates( this ,  tileSize);
	}
	
	public GlobalCoordinates toGlobalCoordinates(LocalCoordinates local, int tileSize) {
		return local.toGlobalCoordinates( this ,  tileSize );
	}	
}