package rolit;

import clientEnServer.RolitConstants;

/**
 * Geeft een object van het balletje.
 * 
 * @author Martijn & Camilio
 */
public enum Ball {
	EMPTY, RED, YELLOW, GREEN, BLUE;

	/**
	 * Geeft de string representatie van de velden.
	 */
	public String toString() {
		// Kijkt welke bal wordt aangeroepen en pakt daar dan de juiste string
		// voor. We pakken dezelfde string als afgesproken in het protecol.
		switch (this) {
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