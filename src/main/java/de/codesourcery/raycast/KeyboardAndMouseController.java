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

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class KeyboardAndMouseController extends KeyboardController {

	public static final double ROTATION_SPEED = 0.5;
	
	private int lastX=-1;
	
	private boolean isTrackingMouse = false;
	
	private final MouseAdapter mouseListener = new MouseAdapter() 
	{
		public void mouseMoved(java.awt.event.MouseEvent e) 
		{
			if ( isTrackingMouse ) 
			{
				final int dx = lastX - e.getX();
				lastX = e.getX();				
				player.rotate(dx*ROTATION_SPEED);
			}
		}
		
		public void mouseClicked(java.awt.event.MouseEvent e) {
			
			if ( e.getButton() == MouseEvent.BUTTON3 && ! isTrackingMouse ) 
			{
				lastX = e.getX();
				component.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
				isTrackingMouse=true;
			}
		}
	};
	
	public KeyboardAndMouseController(Player p,RadarRenderer renderer) {
		super(p,renderer);
	}
	
	@Override
	public void processInput() 
	{
		super.processInput();
		ifPressed( KeyEvent.VK_ESCAPE , () -> {
			if ( isTrackingMouse ) {
				isTrackingMouse = false;
				component.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
			}			
		} );
	}
	
	@Override
	protected void doAttach(JComponent component) 
	{
		super.doAttach( component );
		component.addMouseListener( mouseListener );
		component.addMouseMotionListener( mouseListener );
	}

	@Override
	protected void doDetach(JComponent component) {
		super.doDetach( component );
		this.component.removeMouseListener( mouseListener );
		this.component.removeMouseMotionListener( mouseListener );			
	}
}