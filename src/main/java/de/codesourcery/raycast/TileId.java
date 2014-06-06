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
	
	@Override
	public String toString() {
		return "TileId[x=" + x + ", y=" + y + "]";
	}
}