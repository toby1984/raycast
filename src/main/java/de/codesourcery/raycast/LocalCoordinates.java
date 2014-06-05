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

public class LocalCoordinates extends AbstractVec2d<LocalCoordinates> {

	public LocalCoordinates() {
		super();
	}

	public LocalCoordinates(double x, double y) {
		super(x, y);
	}

	public LocalCoordinates(LocalCoordinates other) {
		super(other);
	}

	public GlobalCoordinates toGlobalCoordinates(TileId tileId, int tileSize) 
	{
		double x;
		if ( tileId.x >= 0 ) {
			x = (tileId.x*tileSize) + this.x;
		} else {
			x = (tileId.x * tileSize) + this.x;
		}
		
		double y;
		if ( tileId.y >= 0 ) {
			y = (tileId.y*tileSize) + this.y;
		} else {
			y = (tileId.y * tileSize) + this.y;	
		}		
		return new GlobalCoordinates(x,y);
	}

	@Override
	public String toString() {
		return "LocalCoordinates [x=" + x + ", y=" + y + "]";
	}
}
