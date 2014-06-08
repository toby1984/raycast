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

import java.awt.Color;

public final class Cell {
	
	public final Color lightColor;
	public final Color darkColor;
	
	private boolean hasPill;
	
	private Cell(boolean hasPill) {
		this.hasPill = hasPill;
		this.lightColor = this.darkColor = null;
	}
	
	public static Cell cellWithPill() {
		return new Cell(true);
	}
	
	public static Cell emptyCell() {
		return new Cell(false);
	}	
	
	public static Cell wall(Color wallColor) {
		return new Cell(wallColor);
	}
	
	public boolean hasPill() {
		return hasPill && ! isWall();
	}
	
	public boolean consumePill() {
		if ( hasPill() ) {
			hasPill = false;
			return true;
		}
		return false;
	}
	
	public boolean isWall() {
		return lightColor != null;
	}
	
	public Cell(Color color1) {
		this.lightColor=color1;
		this.darkColor=darken( color1 );
	}
	
	protected static Color darken(Color color) 
	{
		int newColor = ( color.getRGB()>> 1 ) & 0b00000000_011111111_011111111_011111111;
		newColor |= color.getRGB() & 0xff000000; // preserve alpha
		return new Color(newColor);		
	}	
}