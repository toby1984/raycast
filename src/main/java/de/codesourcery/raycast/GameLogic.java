package de.codesourcery.raycast;

public class GameLogic {

	public boolean canPlayerMoveTo(Cell cell) {
		return ! cell.isWall();
	}
	
	public boolean consumePill(Player player,Cell cell) {
		if ( cell.consumePill() ) {
			System.out.println("Consumed pill at "+cell);
			player.incScore( 1 );
			return true;
		}
		return false;
	}
}
