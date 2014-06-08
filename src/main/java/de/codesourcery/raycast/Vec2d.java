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

public final class Vec2d
{
	public double x;
	public double y;
	
	public Vec2d() { }
	
	public Vec2d(Vec2d other) {
		this(other.x,other.y);
	}
	
	public Vec2d(double x,double y) {
		this.x = x;
		this.y = y;
	}
	
	public final Vec2d set(Vec2d other) { this.x = other.x ; this.y = other.y ; return this; }

	public final Vec2d multiplyAndAdd(Vec2d other,double factor) {
		this.x += (other.x*factor);
		this.y += (other.y*factor);
		return this;
	}
	
	public final Vec2d add(Vec2d other) {
		this.x += other.x;
		this.y += other.y;
		return this;
	}
	
	public final Vec2d sub(Vec2d other) {
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}
	
	public final Vec2d normalize() 
	{
		double len = x*x + y*y;
		if ( len != 0 ) {
			len = Math.sqrt(len);
			x /= len;
			y /= len;
		}
		return this;
	}
	
	@Override
	public final boolean equals(Object obj) 
	{
		if ( obj instanceof Vec2d) {
			final Vec2d other = (Vec2d) obj;
			return this.x == other.x && this.y == other.y;
		}
		return false;
	}
	
	public final Vec2d flip() {
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}
	
	public final Vec2d rotZ(double angleInDeg) 
	{
		double theta = angleInDeg * Math.PI/180f;

		double cs = Math.cos(theta);
		double sn = Math.sin(theta);

		final double newX = x * cs - y * sn;
		final double newY = x * sn + y * cs;
		
		this.x = newX;
		this.y = newY;
		return this;
	}

	public final Vec2d scale(double d) {
		this.x *= d;
		this.y *= d;
		return this;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}