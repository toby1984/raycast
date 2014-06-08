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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main {

	private static final Dimension FRAME_SIZE = new Dimension(640,480);
	
	private static final int MAX_RENDER_DISTANCE = 20;
	
	private static final boolean RENDER_DISTANCE_FOG = false;
	
	private static final DecimalFormat DF = new DecimalFormat("###0.0#");

	private static final boolean BENCHMARK_MODE = false;
	
	protected TileManager tileManager;
	protected Player player;
	protected InputController inputController; 	
	protected MyPanel panel;
	protected final GameLogic gameLogic = new GameLogic();
	
	protected long totalFrames;
	protected float totalFrameTimeSeconds;
	
	protected enum Side {
		NORTH_SOUTH, EAST_WEST;
	}
	
	public static void main(String[] args) {
		new Main().run(args);
	}

	private void run(String[] args) 
	{
		tileManager = new TileManager( new TileFactory(25) );
		
		final Vec2d startingPosition = tileManager.findStartingPosition( gameLogic );
		player = new Player( startingPosition ) 
		{
			@Override
			protected boolean canMoveTo(double newX, double newY) 
			{
				return isFree( newX , position.y ) &&
				        isFree( position.x, newY ) &&
				        isFree( newX , newY );
			}
			
			private boolean isFree(double x,double y) {
				return gameLogic.canPlayerMoveTo(  tileManager.getCellAt( x ,y ) );
			}
		};
		
		gameLogic.consumePill( player , tileManager.getCellAt( startingPosition ) );
		
		panel = new MyPanel( player , RENDER_DISTANCE_FOG );
		
		final JFrame frame = new JFrame("raycast");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		
		frame.setSize(FRAME_SIZE);
		frame.setPreferredSize(FRAME_SIZE);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
		
		inputController = new KeyboardAndMouseController(panel.player,panel.radarRenderer);
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
		if ( BENCHMARK_MODE ) {
			player.forward( 0.1 );
		}
		
		// apply movement
		inputController.processInput();
		
		// update player position
		player.tick( deltaSeconds );
		boolean pillConsumed = gameLogic.consumePill( player , tileManager.getCellAt( player.position ) );
		
		/* ===  REPAINT === */
		
		// update FPS
		if ( pillConsumed || player.hasMoved() || panel.radarRenderer.hasZoomFactorChanged() ) 
		{
			totalFrames++;
			totalFrameTimeSeconds+=deltaSeconds;
			float avgSecondsPerFrame = totalFrameTimeSeconds / totalFrames;
			panel.fps = 1.0f / avgSecondsPerFrame;
			
			if ( BENCHMARK_MODE && totalFrames == 1000 ) {
				System.out.println("Benchmark finished: FPS: "+panel.fps);
				System.exit(1);
			}
			// redraw panel
			panel.paintImmediately( 0 ,  0 ,  panel.getWidth() ,  panel.getHeight() );
		}		
	}
	
	protected class MyPanel extends JPanel {

		final Vec2d rayPos = new Vec2d();
		final Vec2d rayDir = new Vec2d();
		final Vec2d cameraPlane = new Vec2d();		
		
		public float fps;
		
		private BufferedImage buffer;
		private Graphics2D bufferGraphics;
		
		private final boolean renderDistanceFog;
		public final Player player;
		public final RadarRenderer radarRenderer;
		
		public MyPanel(Player player,boolean renderDistanceFog) 
		{
			this.player = player;
			this.radarRenderer = new RadarRenderer(tileManager, player);
			this.renderDistanceFog = renderDistanceFog;
			if ( renderDistanceFog ) {
				setOpaque(false); // enable alpha channel support
			}
		}
		
		private BufferedImage getBuffer() 
		{
			if ( buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight() ) 
			{
				if ( buffer != null ) {
					bufferGraphics.dispose();
				}
 				buffer = new BufferedImage(getWidth() , getHeight() , BufferedImage.TYPE_INT_ARGB );
				bufferGraphics = buffer.createGraphics();
			}
			return buffer;
		}
		
		@Override
		protected void paintComponent(Graphics g) 
		{
			long start = System.currentTimeMillis();
			final BufferedImage image = getBuffer();

			// clear buffer
			bufferGraphics.setColor( getBackground() );
			bufferGraphics.fillRect( 0 ,  0 ,  image.getWidth() ,  image.getHeight() );
			
			render(bufferGraphics);
			
			// render radar
			final int radarWidth = (int) (getWidth()*0.2);
			final int radarHeight = (int) (getWidth()*0.2);
			
			final int x0 = getWidth() - radarWidth -10;
			final int y0 = 20;
			
			radarRenderer.render( new Rectangle(x0,y0,radarWidth,radarHeight ) , bufferGraphics , getBackground() ) ;
			
			// render debug info
			bufferGraphics.setColor(Color.BLACK);
			
			final TileId tileId = tileManager.getTileId( player.position );
			int y = 15;
			bufferGraphics.drawString( "Score:"+ player.score ,10,y);
			y+=15;
			
			bufferGraphics.drawString( "FPS:"+ DF.format( fps ),10,y);
			y+=15;
			
			bufferGraphics.drawString( "Player position: "+player.position+" @ tile "+tileId.x+" , "+tileId.y , 10 , y );
			y+=15;
			
			bufferGraphics.drawString( "Player heading : "+player.direction , 10 , y );			
			y+=15;
			
			g.drawImage( image ,  0 , 0 , null );
			
			final long totalTime = System.currentTimeMillis() - start;
			g.drawString( "Rendering time: "+totalTime+" ms" , 10 , y );
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
			cameraPlane.rotZ( 90 );
			cameraPlane.scale(0.66); 
			
//			System.out.println("=========== Rendering ============");
			
forLoop:			
			for (int x = 0; x < w; x++) 
			{
				// calculate ray position and direction
				final double cameraX = 2.0 * x / w - 1.0; // x-coordinate in camera space (-1...1)
				
				rayPos.set( player.position );
				
				// which box of the map we're in
				int mapX = (int) Math.floor( rayPos.x );
				int mapY = (int) Math.floor( rayPos.y );
				
				rayDir.set( player.direction );
				rayDir.x += (cameraPlane.x * cameraX);
				rayDir.y += (cameraPlane.y * cameraX);

				// length of ray from one x or y-side to next x or y-side
				final double deltaDistX = Math.sqrt(1 + (rayDir.y * rayDir.y) / (rayDir.x * rayDir.x)); // = Math.sqrt( dx^2 + dy^2) = Math.sqrt( 1*1 + (y/x)^2 )
				final double deltaDistY = Math.sqrt(1 + (rayDir.x * rayDir.x) / (rayDir.y * rayDir.y));

				// calculate step and initial sideDist
				
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
				Side side = Side.NORTH_SOUTH; // was a NS or a EW wall hit?
				
				Cell cell = null;
				double perpWallDist=0.0;	
				while ( true ) 
				{
					// jump to next map square, OR in x-direction, OR in y-direction
					if (sideDistX < sideDistY) {
						sideDistX += deltaDistX;
						mapX += stepX;
						side = Side.EAST_WEST;
					} else {
						sideDistY += deltaDistY;
						mapY += stepY;
						side = Side.NORTH_SOUTH;
					}
					
					// Calculate distance projected on camera direction (oblique distance will give fisheye effect!)
					if (side == Side.EAST_WEST) {
						perpWallDist = Math.abs((mapX - rayPos.x + (1 - stepX) / 2) / rayDir.x);
					} else {
						perpWallDist = Math.abs((mapY - rayPos.y + (1 - stepY) / 2) / rayDir.y);
					}
					
					// Check if ray has hit a wall
					cell = tileManager.getCellAt( mapX ,  mapY );
					if ( cell.isWall() ) {
						break;
					} 
					else if ( cell.hasPill() ) {
						// TODO: Render pill...
					}
					
					if ( perpWallDist > MAX_RENDER_DISTANCE ) {
						continue forLoop;
					}
				}
				
				// Calculate line y-offset based on the player's current Z coordinate and wall distance
				float zCoordinate = player.z / Player.MAX_Z; // scale to [0..1]
				final int lineOffset = (int) ( ( h * zCoordinate ) / perpWallDist);
				
				// Calculate height of line to draw on screen
				final int lineHeight = Math.abs((int) ( h / perpWallDist*0.5f));
				
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
				// give x and y sides different brightness
				Color color = side == Side.EAST_WEST ? cell.darkColor : cell.lightColor;

				// distance fog - calculate alpha channel value depending on distance				
				if ( renderDistanceFog ) 
				{
					int alpha  = 255 - (int) ( 255*( perpWallDist / MAX_RENDER_DISTANCE ) );
					alpha = alpha > 255 ? 255 : alpha < 0 ? 0 : alpha;
					
					// draw the pixels of the stripe as a vertical line
					final int colorWithAlpha = ( color.getRGB() & 0x00ffffff ) | alpha  << 24;				
					color = new Color(colorWithAlpha,true);
				} 
				g.setColor(color);
				g.drawLine(x, lineOffset+drawStart, x, lineOffset+drawEnd);
			} // end for
		}
	}
}