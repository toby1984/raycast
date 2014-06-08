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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

public class KeyboardController implements InputController {

	protected final Player player;
	protected final RadarRenderer renderer;
	protected JComponent component;
	
	private final Set<Integer> pressedKeys = new HashSet<>();
	
	@FunctionalInterface
	protected interface Block 
	{
		public void apply();
	}	
	
	private final KeyAdapter adapter = new KeyAdapter()
	{
		public void keyReleased(KeyEvent e) {
			pressedKeys.remove( e.getKeyCode() );
		}
		
		@Override
		public void keyPressed(KeyEvent e) 
		{
			pressedKeys.add( e.getKeyCode() );
		}
	};	
	
	protected final boolean ifPressed(int key1,Block b1) 
	{
		if ( pressedKeys.contains(key1 ) ) {
			b1.apply();
			return true;
		}
		return false;
	}
	
	protected final void ifPressed(int key1,Block b1,int key2, Block b2) 
	{
		if ( ! ifPressed(key1,b1 ) ) {
			ifPressed(key2,b2);
		}
	}	
	
	public void processInput() 
	{
		ifPressed( KeyEvent.VK_BACK_SPACE , player::resetOrientation );
				
		ifPressed( KeyEvent.VK_PLUS , () -> { renderer.zoomIn(0.1); } ,
				   KeyEvent.VK_MINUS , () -> { renderer.zoomOut(0.1); } );
		
		ifPressed( KeyEvent.VK_SPACE  , player::jump );
		ifPressed( KeyEvent.VK_A , () -> { player.strafeLeft( TRANSLATION_SPEED ); } , 
				   KeyEvent.VK_D , () -> { player.strafeRight( TRANSLATION_SPEED ); } );
		
		ifPressed( KeyEvent.VK_Q , () -> { player.rotateLeft( 3 ); } , 
				   KeyEvent.VK_E , () -> { player.rotateRight( 3 ); } );
		
		ifPressed( KeyEvent.VK_W , () -> { player.forward( TRANSLATION_SPEED ); } , 
				   KeyEvent.VK_S , () -> { player.backward( TRANSLATION_SPEED ); } );		
	}
	
	public KeyboardController(Player p,RadarRenderer renderer) {
		this.player = p;
		this.renderer = renderer;
	}
	
	@Override
	public final void attach(JComponent comp) 
	{
		if ( component != null ) {
			throw new IllegalStateException("Already attached");
		}
		try {
			doAttach(comp);
		} finally {
			this.component = comp;
		}
	}
	
	protected void doAttach(JComponent comp) {
		comp.addKeyListener( adapter );
	}

	@Override
	public final void detach() 
	{
		if ( this.component != null ) 
		{
			try {
				doDetach( this.component );
			} 
			finally {
				this.component = null;
			}
		}
	}
	
	protected void doDetach(JComponent comp) {
		this.component.removeKeyListener( adapter );		
	}	
}