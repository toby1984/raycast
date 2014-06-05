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

public abstract class AbstractVec2d<T extends AbstractVec2d<T>> 
{
	public double x;
	public double y;
	
	public AbstractVec2d() { }
	
	public AbstractVec2d(T other) {
		this(other.x,other.y);
	}
	
	public AbstractVec2d(double x,double y) {
		this.x = x;
		this.y = y;
	}
	
	public final T set(T other) { this.x = other.x ; this.y = other.y ; return (T) this; }

	public final T normalize() 
	{
		double len = x*x + y*y;
		if ( len != 0 ) {
			len = Math.sqrt(len);
			x /= len;
			y /= len;
		}
		return (T) this;
	}
	
	@Override
	public final boolean equals(Object obj) 
	{
		if ( obj != null && getClass() == obj.getClass() ) {
			final AbstractVec2d<?> other = (AbstractVec2d<?>) obj;
			return this.x == other.x && this.y == other.y;
		}
		return false;
	}
	
	public final T rotY(double angleInDeg) 
	{
		double theta = angleInDeg * Math.PI/180f;

		double cs = Math.cos(theta);
		double sn = Math.sin(theta);

		final double newX = x * cs - y * sn;
		final double newY = x * sn + y * cs;
		
		this.x = newX;
		this.y = newY;
		return (T) this;
	}

	public final T scale(double d) {
		this.x *= d;
		this.y *= d;
		return (T) this;
	}
}