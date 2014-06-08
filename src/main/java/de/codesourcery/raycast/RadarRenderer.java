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
		
		final int tileSize = factory.tileSize()*2;
		
		Vec2d pCenter = new Vec2d(player.direction).scale( tileSize / 2.0d ).add( player.position );
		Vec2d vXAxis = new Vec2d(player.direction).rotZ( 90 );
		Vec2d vYAxis = new Vec2d(player.direction).flip();		
		
		Vec2d p0 = new Vec2d( pCenter) .sub( new Vec2d( vXAxis).scale( tileSize  /2.0d  ) );

		final float stepX = (r.width-2.0f)  / (float) tileSize;
		final float stepY = (r.height-2.0f) / (float) tileSize;
		
		for ( int x = 0 ; x < tileSize ; x++ ) 
		{
			for ( int y = 0 ; y < tileSize ; y++ ) 
			{
				final Vec2d v = new Vec2d(p0).add( new Vec2d( vXAxis ).scale( x ) ).add( new Vec2d( vYAxis ).scale( y ) );
				
				final float x0 = r.x + 1 + x*stepX;
				final float y0 = r.y + 1 + y*stepY;
				final float x1 = x0 + stepX;
				final float y1 = y0 + stepY;
				
				final Wall wall = factory.getWallFast( v.x,v.y);
				if ( wall != null ) 
				{
					g.setColor(wall.darkColor);
					g.fillRect( Math.round(x0) , Math.round(y0) , Math.round(x1-x0), Math.round(y1-y0) );
				}
			}
		}
		
		// mark player position
		final Vec2d playerHeading = new Vec2d(player.direction).scale(1.5);
		
		// transform player position
		int x0 = r.x + 1 + Math.round( ( (r.width - 2) / 2.0f ) );
		int y0 = r.y + 1 + Math.round( ( (r.height - 2) / 2.0f ) );
		
		// transform player heading
		int x1 = Math.round( x0 + (float) playerHeading.x );
		int y1 = Math.round( y0 + (float) playerHeading.y );
		
		g.setColor(Color.RED);
		
		g.fillRect( x0 -2 , y0 -2 , 4 , 4 );
		final Stroke oldStroke = g.getStroke();
		g.setStroke( new BasicStroke(2));
		g.drawLine( x0,y0 ,x1,y1 );
		g.setStroke(oldStroke);
	}
}
