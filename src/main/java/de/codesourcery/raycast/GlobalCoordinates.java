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

public class GlobalCoordinates extends AbstractVec2d<GlobalCoordinates> 
{
	public GlobalCoordinates() {
		super();
	}

	public GlobalCoordinates(double x, double y) {
		super(x, y);
	}

	public GlobalCoordinates(GlobalCoordinates other) {
		super(other);
	}
	
	public LocalCoordinates toLocalCoordinates(TileId tileId, int tileSize) 
	{
		int xOrigin = tileId.x * tileSize;
		int yOrigin = tileId.y * tileSize;
		
		double x;
		if ( xOrigin >= 0 ) {
			x = this.x - xOrigin;
		} else {
			x = -(tileId.x*tileSize - this.x);
		}
		
		double y;
		if ( yOrigin >= 0 ) {
			y = this.y - yOrigin;
		} else {
			y = -(tileId.y*tileSize - this.y);
		}		
		return new LocalCoordinates(x,y); 
	}

	@Override
	public String toString() {
		return "GlobalCoordinates [x=" + x + ", y=" + y + "]";
	}		
}
