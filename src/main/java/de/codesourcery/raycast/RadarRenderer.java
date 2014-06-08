package de.codesourcery.raycast;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

public class RadarRenderer {

	private final TileManager factory;
	private final Player player;
	
	public RadarRenderer(TileManager factory,Player player) {
		this.factory = factory;
		this.player = player;
	}
	
	public void render(Rectangle r , Graphics2D g , Color backgroundColor) 
	{
		// clear background
		g.setColor(backgroundColor);
		g.fillRect( r.x ,  r.y ,  r.width ,  r.height );
		
		// draw outline
		g.setColor(Color.BLACK);
		g.drawRect( r.x , r.y , r.width-1 , r.height-1 );
		
		TileId tileId = factory.getTileId( player.position );
		final Tile tile = factory.getTile( tileId );
		
		final int tileSize = factory.tileSize();
		final float stepX = (r.width-2.0f)  / (float) tileSize;
		final float stepY = (r.height-2.0f) / (float) tileSize;
		
		for ( int x = 0 ; x < tileSize ; x++ ) 
		{
			for ( int y = 0 ; y < tileSize ; y++ ) 
			{
				final float x0 = r.x + 1 + x*stepX;
				final float y0 = r.y + 1 + y*stepY;
				final float x1 = x0 + stepX;
				final float y1 = y0 + stepY;
				
				final Wall wall = tile.getWall(x,y);
				if ( wall != null ) 
				{
					g.setColor(wall.darkColor);
					g.fillRect( Math.round(x0) , Math.round(y0) , Math.round(x1-x0), Math.round(y1-y0) );
				}
			}
		}
		
		// mark player position
		final Vec2d playerPos = factory.toLocalCoordinates( tileId ,  player.position.x ,  player.position.y );
		final Vec2d playerHeading = new Vec2d(player.direction).scale(1.5).add( playerPos );
		
		// transform player position
		double p0x = playerPos.x / factory.tileSize(); // 0...1
		double p0y = playerPos.y / factory.tileSize();
		
		int x0 = r.x + 1 + Math.round( (r.width-2)  *  (float) p0x );
		int y0 = r.y + 1 + Math.round( (r.height-2) *  (float) p0y );
		
		System.out.println("px="+p0x+" , py= "+p0y);
		
		// transform player heading
		double p1x = playerHeading.x / factory.tileSize(); // 0...1
		double p1y = playerHeading.y / factory.tileSize();
		
		int x1 = r.x + 1 + Math.round( (r.width-2)  *  (float) p1x );
		int y1 = r.y + 1 + Math.round( (r.height-2) *  (float) p1y );		
		
		g.setColor(Color.RED);
		
		g.fillRect( x0 -2 , y0 -2 , 4 , 4 );
		final Stroke oldStroke = g.getStroke();
		g.setStroke( new BasicStroke(2));
		g.drawLine( x0,y0 ,x1,y1 );
		g.setStroke(oldStroke);
		
//		g.drawLine( x0 , r.y+1 , x0 , r.y+r.height-2 );
//		g.drawLine( r.x+1 , y0 , r.x + r.width -2 , y0 );
	}
}
