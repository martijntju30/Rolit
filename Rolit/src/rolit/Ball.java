package rolit;

import clientEnServer.RolitConstants;

/**
 * Geeft een object van het balletje.
 * @author Martijn & Camilio
 * @version 1
 */
public enum Ball {
	EMPTY, RED, YELLOW, GREEN, BLUE;
	
	public String toString(){
		switch(this){
		case RED:
			return RolitConstants.roodVeld;
		case YELLOW:
			return RolitConstants.geelVeld;
		case GREEN:
			return RolitConstants.groenVeld;
		case BLUE:
			return RolitConstants.blauwVeld;
		default:
			return RolitConstants.leegVeld;
		}
	}
}