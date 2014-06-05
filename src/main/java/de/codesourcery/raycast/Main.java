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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main {

	private static final Dimension FRAME_SIZE = new Dimension(640,480);

	private static final DecimalFormat DF = new DecimalFormat("###0.0#");
	
	private TileManager tileManager;
	private Player player;
	private InputController inputController; 	
	private MyPanel panel;
	
	private long totalFrames;
	private float totalFrameTimeSeconds;
	
	protected enum Side {
		NORTH_SOUTH, EAST_WEST;
	}
	
	public static void main(String[] args) {
		new Main().run(args);
	}

	private void run(String[] args) 
	{
		tileManager = new TileManager( new TileFactory(24) );
		
		player = new Player( new GlobalCoordinates(22,12) ,  new Vec2d( -1 , 0 ) ) 
		{
			@Override
			protected boolean canMoveTo(double newX, double newY) 
			{
				final Tile currentTile = tileManager.getTile( player.position );
				return currentTile.isFree( (int) newX , (int) position.y ) &&
				       currentTile.isFree( (int) position.x, (int) newY ) &&
				       currentTile.isFree( (int) newX , (int) newY );
			}
		};
		panel = new MyPanel( player );
		
		final JFrame frame = new JFrame("raycast");
		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().add(panel);
		
		frame.setSize(FRAME_SIZE);
		frame.setPreferredSize(FRAME_SIZE);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
		
		inputController = new KeyboardAndMouseController(panel.player);
		inputController.attach( panel );
		panel.setFocusable( true );
		panel.requestFocus();

		// start game loop
		final Timer timer = new Timer(0, new ActionListener() {

			private long lastCall = -1;
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				final long now = System.currentTimeMillis();
				if ( lastCall != -1 ) 
				{
					float deltaMillis = now - lastCall;
					gameLoop( deltaMillis / 1000.0f );
				}
				lastCall = now;
			}
		});
		timer.setInitialDelay(0);
		timer.setDelay( 16 );
		timer.start();
	}
	
	private void gameLoop(float deltaSeconds) 
	{
		// apply movement
		inputController.processInput();
		
		// update player position
		player.tick( deltaSeconds );
		
		/* ===  REPAINT === */
		
		// update FPS
		if ( player.hasMoved() ) 
		{
			totalFrames++;
			totalFrameTimeSeconds+=deltaSeconds;
			float avgSecondsPerFrame = totalFrameTimeSeconds / totalFrames;
			panel.fps = 1.0f / avgSecondsPerFrame;
			
			// redraw panel
			panel.paintImmediately( 0 ,  0 ,  panel.getWidth() ,  panel.getHeight() );
		}		
	}
	
	protected class MyPanel extends JPanel {

		final LocalCoordinates rayPos = new LocalCoordinates();
		final Vec2d rayDir = new Vec2d();
		final Vec2d cameraPlane = new Vec2d();		
		
		public float fps;
		
		public final Player player;
		
		public MyPanel(Player player) 
		{
			this.player = player;
		}
		
		@Override
		protected void paintComponent(Graphics g) 
		{
			super.paintComponent(g);
			render(g);
			g.setColor(Color.BLACK);
			
			final TileId tileId = tileManager.getTile( player.position ).tileId;
			g.drawString( "FPS:"+ DF.format( fps ) + " ( "+player.position+" @ tile "+tileId.x+" , "+tileId.y+" )", 10 , 15 );				
		}
		
		protected void render(Graphics g) 
		{
			final int w = getWidth();
			final int h = getHeight();
			
			/*
			 * The following code is taken from/derived from
			 * Lode Vandevenne's excellent tutorial at
			 * 
			 * http://lodev.org/cgtutor/raycasting.html
			 */
			
			cameraPlane.set( player.direction );
			cameraPlane.rotY( -90 );
			cameraPlane.scale(0.66); // player direction is always a normalized vector 
			
			final Tile currentTile = tileManager.getTile( player.position );
			for (int x = 0; x < w; x++) 
			{
				// calculate ray position and direction
				final double cameraX = 2.0 * x / w - 1.0; // x-coordinate in camera space (0...1)
				
				rayPos.set( currentTile.toLocalCoordinates( player.position ) );
				rayDir.set(player.direction);
				
				rayDir.x += cameraPlane.x * cameraX;
				rayDir.y += cameraPlane.y * cameraX;

				// length of ray from one x or y-side to next x or y-side
				final double deltaDistX = Math.sqrt(1 + (rayDir.y * rayDir.y) / (rayDir.x * rayDir.x));
				final double deltaDistY = Math.sqrt(1 + (rayDir.x * rayDir.x) / (rayDir.y * rayDir.y));

				// calculate step and initial sideDist
				
				// which box of the map we're in
				int mapX = (int) rayPos.x;
				int mapY = (int) rayPos.y;
				
				// what direction to step in x or y-direction (either +1 or -1)
				final int stepX;
				final int stepY;

				// length of ray from current position to next x or y-side				
				double sideDistX;
				double sideDistY;				
				if (rayDir.x < 0) {
					stepX = -1;
					sideDistX = (rayPos.x - mapX) * deltaDistX;
				} else {
					stepX = 1;
					sideDistX = (mapX + 1.0 - rayPos.x) * deltaDistX;
				}
				if (rayDir.y < 0) {
					stepY = -1;
					sideDistY = (rayPos.y - mapY) * deltaDistY;
				} else {
					stepY = 1;
					sideDistY = (mapY + 1.0 - rayPos.y) * deltaDistY;
				}

				// perform DDA
				int hit = 0; // was there a wall hit?
				Side side = Side.NORTH_SOUTH; // was a NS or a EW wall hit?
				
				while (hit == 0) {
					// jump to next map square, OR in x-direction, OR in y-direction
					if (sideDistX < sideDistY) {
						sideDistX += deltaDistX;
						mapX += stepX;
						side = Side.NORTH_SOUTH;
					} else {
						sideDistY += deltaDistY;
						mapY += stepY;
						side = Side.EAST_WEST;
					}
					// Check if ray has hit a wall
					if ( currentTile.isOccupied(  mapX , mapY ) ) {
						hit = 1;
					}
				}

				// Calculate distance projected on camera direction (oblique distance will give fisheye effect!)
				final double perpWallDist;				
				if (side == Side.NORTH_SOUTH) {
					perpWallDist = Math.abs((mapX - rayPos.x + (1 - stepX) / 2) / rayDir.x);
				} else {
					perpWallDist = Math.abs((mapY - rayPos.y + (1 - stepY) / 2) / rayDir.y);
				}
				
				// Calculate line y-offset based on the player's current Z coordinate and wall distance
				float zCoordinate = player.z / Player.MAX_Z; // scale to [0..1]
				final int lineOffset = (int) ( ( h * zCoordinate ) / perpWallDist);
				
				// Calculate height of line to draw on screen
				final int lineHeight = Math.abs((int) ( 2*h / perpWallDist*0.5f));
				
				// calculate lowest and highest pixel to fill in current stripe
				int drawStart = -lineHeight / 2 + h / 2;
				if (drawStart < 0) {
					drawStart = 0;
				}
				int drawEnd = lineHeight / 2 + h / 2;
				if (drawEnd >= h) {
					drawEnd = h - 1;
				}

				// choose wall color
				final Wall tile = currentTile.getWall( mapX , mapY );
				Color color = Color.YELLOW;
				if ( tile != null ) {
					color = tile.lightColor;
				} 

				// give x and y sides different brightness
				if (side == Side.EAST_WEST) {
					color = tile != null ? tile.darkColor : color;
				}

				// draw the pixels of the stripe as a vertical line
				g.setColor(color);
				g.drawLine(x, lineOffset+drawStart, x, lineOffset+drawEnd);
			} // end for
		}
	}
}