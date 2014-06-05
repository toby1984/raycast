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


public class Tile {

	public final TileId tileId;
	private final Wall[][] tiles;
	private final int tileSize;
	
	protected Tile(TileId tileId,int tileSize , Wall[][] tiles) 
	{
		if ( tileId == null ) {
			throw new IllegalArgumentException("tileId must not be NULL");
		}
		if ( tiles == null ) {
			throw new IllegalArgumentException("tiles must not be NULL");
		}
		this.tiles = tiles;
		this.tileSize = tileSize;
		this.tileId = tileId;
	}
	
	public int width() {
		return tileSize;
	}
	
	public int height() {
		return tileSize;
	}
	
	public Wall getWall(int x,int y) {
		return tiles[x][y];
	}
	
	public boolean isOccupied(int x,int y) {
		return tiles[x][y] != null;
	}
	
	public boolean isFree(int x,int y) {
		return tiles[x][y] == null;
	}	
	
	public LocalCoordinates toLocalCoordinates(GlobalCoordinates in) {
		return in.toLocalCoordinates( this.tileId , tileSize ); 
	}
	
	public GlobalCoordinates toGlobalCoordinates(LocalCoordinates in) {
		return in.toGlobalCoordinates( this.tileId ,  tileSize );
	}	
}