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
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Maze 
{
	protected static enum Direction 
	{
		NORTH,SOUTH,WEST,EAST;
	}

	protected final int size;
	protected final Room[] rooms;
	
	private final Point[] directions = {new Point(0,-1),new Point(1,0),new Point(0,1),new Point(-1,0)};	

	public static final class Room 
	{
		public final int x;
		public final int y;

		// connected neighbors
		public Room north;
		public Room east;
		public Room south;
		public Room west;

		public boolean visited=false;            

		public Room(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public void reset() {
			visited = false;
			north=east=south=west=null;
		}

		@Override
		public String toString()
		{
			return "("+x+","+y+")";
		}

		private void addPassageTo(Direction d,Room room) 
		{
			switch(d) {
			case NORTH:
				assert( north == null );
				north=room;
				room.south = this;
				break;
			case SOUTH:
				assert( south == null );
				south=room;
				room.north = this;
				break;
			case EAST:
				assert( east == null );
				east=room;
				room.west = this;
				break;
			case WEST:
				assert( west == null );
				west=room;
				room.east = this;
				break;
			default:
				throw new IllegalArgumentException("Unhandled direction: "+d);
			}
		}

		public void addPassageTo(Room room) 
		{
			if ( room.y == y ) { // can only be the western or eastern neighbor cell
				if ( room.x == x-1 ) {
					addPassageTo(Direction.WEST , room );
				} else {
					addPassageTo(Direction.EAST , room );
				}
			} else if ( room.x == x ) { // can only be the northern or southern neighbor cell
				if ( room.y == y - 1 ) 
				{
					addPassageTo(Direction.NORTH , room );
				} else {
					addPassageTo(Direction.SOUTH , room );
				}
			} 
			else 
			{
				throw new RuntimeException("Unreachable code reached");
			}
		}
	}

	public Maze(int size)
	{
		this.size = size;
		this.rooms = new Room[ size*size ];
		int ptr = 0;
		for ( int y = 0 ; y < size ; y++ ) {
			for ( int x = 0 ; x < size ; x++ ) {
				rooms[ptr++] = new Room(x,y);
			}
		}              
	}

	protected void reset() 
	{
		for ( int i = 0 ; i < size*size ; i++ ) {
			rooms[i].reset();
		}            
	}

	private void shuffleDirections(Random rnd) 
	{
		/* Fisher-Yates shuffle.
		 * To shuffle an array a of n elements (indices 0..n-1):
		 * 
		 * for i from n − 1 downto 1 do
		 *   j ← random integer with 0 ≤ j ≤ i
		 *   exchange a[j] and a[i]             
		 */
		for ( int i = 3 ; i >= 1 ; i--) 
		{
			int j = rnd.nextInt( i+1 );
			Point tmp = directions[i];
			directions[i] = directions[j];
			directions[j] = tmp;
		}
	}

	protected Room getUnvisitedNeighbor(Room c,Random rnd) 
	{
		final int x = c.x;
		final int y = c.y;

		shuffleDirections(rnd);

		for (int i = 0; i < directions.length; i++) 
		{
			final Point p = directions[i];
			Room n = getCell( x+p.x   , y+p.y ); 
			if ( n != null && ! n.visited ) {
				return n;
			}                 
		}
		return null;
	}

	protected Room getCell(int x,int y) 
	{
		if ( x >= 0 && x < size && y>=0 && y < size ) {
			return rooms[ x + y * size ];
		}
		return null;
	}        

	public static void renderMaze(Maze maze,Graphics2D g,int w,int h) 
	{
		final int cellWidth = w / maze.size;
		final int cellHeight = h / maze.size;                

		for ( int y = 0 ; y < maze.size ; y++ ) 
		{
			for ( int x = 0 ; x < maze.size ; x++ ) 
			{
				final Room cell = maze.getCell( x,y );

				final int xmin = 10+(x * cellWidth);
				final int xmax = xmin + cellWidth;

				final int ymin = 10+(y * cellHeight);
				final int ymax = ymin + cellHeight;

				if ( cell.north == null ) { // draw north wall
					g.drawLine( xmin , ymin , xmax , ymin );
				} 
				if ( cell.south == null ) { // draw south wall
					g.drawLine( xmin , ymax , xmax , ymax );
				}
				if ( cell.west == null ) { // draw west wall
					g.drawLine( xmin , ymin , xmin, ymax);
				} 
				if ( cell.east == null ) { // draw east wall
					g.drawLine( xmax , ymin , xmax, ymax );
				}                        
			}
		}
	}
	
	public static void renderMaze(Maze maze,Wall[][] array,int w,int h,boolean renderBorder) 
	{
		final int cellWidth = w / maze.size;
		final int cellHeight = h / maze.size;                

		final Wall wall = new Wall(Color.RED);
		for ( int y = 0 ; y < maze.size ; y++ ) 
		{
			for ( int x = 0 ; x < maze.size ; x++ ) 
			{
				final Room cell = maze.getCell( x,y );

				final int xmin = x * cellWidth;
				final int xmax = xmin + cellWidth;

				final int ymin = y * cellHeight;
				final int ymax = ymin + cellHeight;

				if ( cell.north == null ) { // draw north wall
					drawHorizontalLine(ymin, xmin, xmax, array, wall );
				} 
				if ( cell.south == null ) { // draw south wall
					drawHorizontalLine(ymax, xmin, xmax, array, wall );
				}
				if ( cell.west == null ) { // draw west wall
					drawVerticalLine(xmin, ymin, ymax, array, wall );
				} 
				if ( cell.east == null ) { // draw east wall
					drawVerticalLine(xmax, ymin, ymax, array, wall );
				}                        
			}
		}
		
		if ( ! renderBorder ) {
			drawHorizontalLine( 0 , 0 , w , array, null );
			drawHorizontalLine( h-1 , 0 , w , array, null );
			
			drawVerticalLine(0, 0, h-1 , array, null );
			drawVerticalLine(w-1, 0, h-1 , array, null );			
		}
	}	
	
	private static void drawHorizontalLine(int y, int xmin,int xmax,Wall[][] array,Wall wall) {
		for ( int x = xmin ; x <= xmax ; x++ ) {
			try {
			array[x][y]=wall;
			} catch(ArrayIndexOutOfBoundsException e) {
				// System.err.println("AIOBE ("+x+","+y+")");
			}			
		}
	}
	
	private static void drawVerticalLine(int x, int ymin,int ymax,Wall[][] array,Wall value) {
		for ( int y = ymin ; y <= ymax ; y++ ) {
			try {
				array[x][y]=value;
			} catch(ArrayIndexOutOfBoundsException e) {
				// System.err.println("AIOBE ("+x+","+y+")");
			}
		}
	}	

	public void generateMaze(long seed) 
	{
		reset();

		final Random rnd = new Random(seed);
		final List<Room> rooms = new ArrayList<>();

		final Room start = getCell(rnd.nextInt(size), rnd.nextInt(size) );
		rooms.add( start );

		do
		{
			final Room currentRoom = rooms.get( rnd.nextInt(rooms.size() ) );

			final Room neighbour = getUnvisitedNeighbor(currentRoom,rnd);
			if ( neighbour == null ) 
			{
				rooms.remove( currentRoom );
			} 
			else 
			{
				currentRoom.visited = true;
				neighbour.visited = true;
				currentRoom.addPassageTo( neighbour );
				rooms.add( neighbour );
			}
		} while ( ! rooms.isEmpty() );
	}
}