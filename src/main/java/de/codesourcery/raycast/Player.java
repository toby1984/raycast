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

public abstract class Player 
{
	public static final float JUMP_ACCELERATION = 2f;
	public static final float GRAVITY = 1f;
	
	public static final float MAX_Z = 5;
	public static final float MAX_ACCELERATION = 2;
	
	private static final Vec2d INITIAL_HEADING = new Vec2d(0,-1); // 0 degrees heading
	
	public double heading;
	public final Vec2d position;
	public final Vec2d direction;
	
	public int score;
	
	private float zAcceleration;
	private float zVelocity;	
	public float z;
	
	private boolean hasMoved = true;
	
	public Player(Vec2d pos) {
		this.position = new Vec2d(pos);
		this.direction = new Vec2d(INITIAL_HEADING);
	}
	
	public boolean hasMoved() {
		final boolean result = hasMoved;
		hasMoved = false;
		return result;
	}
	
	private boolean setMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
		return hasMoved;
	}
	
	public void incScore(int value) {
		score += value;
	}
	
	public boolean forward(double factor) 
	{
		double newX = position.x + direction.x*factor;
		double newY = position.y + direction.y*factor;
		return setMoved( maybeMoveTo(newX,newY) );
	}
	
	private boolean maybeMoveTo(double newX,double newY) {
		if ( canMoveTo( newX,newY ) ) {
			this.position.x = newX;
			this.position.y = newY;
			return true;
		} 
		return false;
	}
	
	protected abstract boolean canMoveTo(double x,double y);
	
	public boolean backward(double factor) 
	{
		return forward(-factor);
	}	
	
	public boolean strafeLeft(double factor) 
	{
		return strafeRight(-factor);
	}		
	
	public boolean strafeRight(double factor) 
	{
		final Vec2d strafeDir = new Vec2d(-direction.y,direction.x).scale( factor );		
		double newX = position.x + strafeDir.x;
		double newY = position.y + strafeDir.y;
		return setMoved( maybeMoveTo(newX, newY) ); 
	}	
	
	public void resetOrientation() {
		setHeading(0);
	}	
	
	public boolean setHeading(double angleInDegrees) {
		while( angleInDegrees >= 360 ) {
			angleInDegrees-=360;
		}
		while( angleInDegrees < 0 ) {
			angleInDegrees += 360;
		}
		this.heading = angleInDegrees;
		direction.set( new Vec2d(INITIAL_HEADING).rotZ( heading ) );
		hasMoved = true;
		return true;		
	}
	
	public boolean rotate(double angleInDegrees) {
		heading += angleInDegrees;
		return setHeading( heading );
	}
	
	public void jump() {
		if ( zAcceleration == 0 ) {
			zAcceleration += JUMP_ACCELERATION;
		}
	}
	
	public void tick(float deltaSeconds) 
	{
		if ( z != 0 || zAcceleration != 0 ) 
		{
			hasMoved = true;
			
			deltaSeconds *= 10;
			
			/*
			 *       m              m*s
			 *  a = ---  => a * s = --- 
			 *      s^2              s
			 *  
			 * s = v*t
			 * v = ds/dt
			 * a = dv / dt 
			 */
			
			zAcceleration = limit( zAcceleration , MAX_ACCELERATION );
			
			System.out.println("z: "+z+" | a: "+zAcceleration+" | v: "+zVelocity);
			z += zVelocity*deltaSeconds + 0.5f * zAcceleration * deltaSeconds * deltaSeconds;
					
			if ( z >= MAX_Z ) {
				z = MAX_Z;
			}
			
			zVelocity += zAcceleration*deltaSeconds;
			
			zAcceleration -= GRAVITY*deltaSeconds*2;
			
			if ( z <= 0 ) {
				zAcceleration = 0;
				zVelocity = 0;
				z = 0;
			}
		}
	}
	
	private static final float limit(float value,float absValue) {
		if ( value > absValue ) {
			return absValue;
		} 
		if ( value < -absValue ) {
			return -absValue;
		}
		return value;
	}
	
	public boolean rotateLeft(double angleInDegrees) {
		return rotate(angleInDegrees);
	}
	
	public boolean rotateRight(double angleInDegrees) {
		return rotate(-angleInDegrees);
	}		
}