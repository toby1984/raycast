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
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class RadarRenderer {

	private final TileManager tileManager;
	private final Player player;
	
	private double zoomFactor = 2.0d;
	private boolean zoomFactorChanged = true;
	
	public RadarRenderer(TileManager factory,Player player) {
		this.tileManager = factory;
		this.player = player;
	}
	
	public void zoomIn(double delta)
	{
		this.zoomFactor -= delta;
		this.zoomFactorChanged = true;
	}
	
	public void zoomOut(double delta)
	{
		this.zoomFactor += delta;
		this.zoomFactorChanged = true;
	}
	
	public boolean hasZoomFactorChanged() {
		final boolean result = zoomFactorChanged;
		zoomFactorChanged = false;
		return result;
	}
	
	public void render(Rectangle r , Graphics2D g , Color backgroundColor) 
	{
		// clear background
		g.setColor(backgroundColor);
		g.fillRect( r.x ,  r.y ,  r.width ,  r.height );
		
		// draw outline
		g.setColor(Color.BLACK);
		g.drawRect( r.x , r.y , r.width-1 , r.height-1 );
		
		final int tileSize = (int) Math.max( 2 , tileManager.tileSize()*zoomFactor );
		
		final TileId tileId = tileManager.getTileId( player.position );
		
		Vec2d local = tileManager.toLocalCoordinates( player.position.x , player.position.y );
		Vec2d global = tileManager.toGlobalCoordinates( tileId , (int) local.x , (int) local.y );
		
		final double xmin = global.x - (tileSize/2.0);
		final double xmax = global.x + (tileSize/2.0);
		
		final double ymin = global.y - (tileSize/2.0);
		final double ymax = global.y + (tileSize/2.0);		
		
		double stepX = (r.width-2) / (double) tileSize;
		double stepY = (r.height-2) / (double) tileSize;
		
		int ix = 0;
		int iy = 0;
		for ( double x = xmin  ; x < xmax ; x+=1 , ix++ ) 
		{
			iy = 0;
			for ( double y = ymin ; y < ymax; y+=1 , iy++ ) 
			{
				final double x0 = Math.floor( r.x + 1 + ix*stepX );
				final double y0 = Math.floor( r.y + 1 + iy*stepY );
				final double x1 = x0 + stepX;
				final double y1 = y0 + stepY;
				
				final Cell cell = tileManager.getCellAt( x , y );
				if ( cell.isWall() ) 
				{
					g.setColor(cell.darkColor);
					
					final int w = (int) Math.max( x1-x0 , 1 );
					final int h = (int) Math.max( y1-y0 , 1 );					
					g.fillRect( (int) x0, (int) y0 , w , h);
				} 
				else if ( cell.hasPill() ) 
				{
					final int w = (int) Math.max( (x1-x0)/2.0 , 1 );
					final int h = (int) Math.max( (y1-y0)/2.0 , 1 );
					g.setColor(Color.YELLOW);
					g.fillArc((int) (x0+w/2) , (int) (y0+h/2) , w , h , 0, 360 );
				}
			}
		}
		
		// mark player position
		int x0 = r.x + 1 + Math.round( ( (r.width - 2) / 2.0f ) );
		int y0 = r.y + 1 + Math.round( ( (r.height - 2) / 2.0f ) );
		
		g.setColor(Color.RED);
		g.fillRect( x0 -2 , y0 -2 , 4 , 4 );
	}
}
