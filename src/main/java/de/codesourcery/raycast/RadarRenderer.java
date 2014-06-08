package de.codesourcery.raycast;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

public class RadarRenderer {

	private final TileManager factory;
	private final Player player;
	
	private double zoomFactor = 2.0d;
	private boolean zoomFactorChanged = true;
	
	public RadarRenderer(TileManager factory,Player player) {
		this.factory = factory;
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
		
		final double tileSize = factory.tileSize()*zoomFactor;
		
		Vec2d pCenter = new Vec2d(player.direction).scale( tileSize / 2.0d ).add( player.position );
		Vec2d vXAxis = new Vec2d(player.direction).rotZ( 90 );
		Vec2d vYAxis = new Vec2d(player.direction).flip();		
		
		Vec2d p0 = new Vec2d( pCenter) .sub( new Vec2d( vXAxis).scale( tileSize  /2.0d  ) );

		final double stepX = (r.width-2.0d)  / tileSize;
		final double stepY = (r.height-2.0d) / tileSize;
		
		for ( int x = 0 ; x < tileSize ; x++ ) 
		{
			for ( int y = 0 ; y < tileSize ; y++ ) 
			{
				final Vec2d v = new Vec2d(p0).multiplyAndAdd( vXAxis , x ).multiplyAndAdd( vYAxis , y );
				
				final double x0 = Math.floor( r.x + 1 + x*stepX );
				final double y0 = Math.floor( r.y + 1 + y*stepY );
				final double x1 = x0 + stepX;
				final double y1 = y0 + stepY;
				
				final Wall wall = factory.getWallFast( v.x,v.y);
				if ( wall != null ) 
				{
					g.setColor(wall.darkColor);
					
					final int w = (int) Math.max( x1-x0 , 1 );
					final int h = (int) Math.max( y1-y0 , 1 );					
					g.fillRect( (int) x0, (int) y0 , w , h);
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
