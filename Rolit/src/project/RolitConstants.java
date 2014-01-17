package project;

/**
 * Alle benodigde constanten.
 * @author Christian Versloot
 * @version 1.0 (13/01/2014)
 * 
 * [C] = Client
 * [S] = Server
 *  -> = naar
 *
 */

public interface RolitConstants {
	
	/**
	 * Commando en parameter delimeter.
	 * Zoals afgesproken worden de commando's en parameters gescheiden door een spatie.
	 * Richting: [C] -> [S] en [S] -> [C]
	 */
	String msgDelim = " ";
	
	/**
	 * Score delimeter
	 * Zoals afgesproken worden de scores gestuurd met een komma als delimeter.
	 * Richting: [S] -> [C]
	 */
	String scoreDelim = ",";
	
	/**
	 * Toegestane characters.
	 * Zoals afgesproken zijn alleen deze karakters in spelernamen toegestaan.
	 * Te controleren met String.matches(charRegex) (return: true of false)
	 * Gebruikte bron: http://stackoverflow.com/questions/5238491/check-if-string-contains-only-letters
	 * Richting: n.v.t.
	 */
	String charRegex = "[a-zA-Z0-9]+";
	
	/**
	 * De velden in het bord.
	 * Richting: n.v.t.
	 */
	String roodVeld = "rood";
	String groenVeld = "groen";
	String geelVeld = "geel";
	String blauwVeld = "blauw";
	String leegVeld = "leeg";
	
	/**
	 * De mogelijke te retourneren foutmeldingen.
	 * Richting: [S] -> [C]
	 */	
	String errorOngeldigeGebruikersnaam = "ongeldigeGebruikersnaam";
	String errorOngeldigeZet = "ongeldigeZet";
	String errorOngeldigCommando = "ongeldigCommando";
	String errorGebruikersnaamInGebruik = "gebruikersnaamInGebruik";
	String errorAantalSpelersOngeldig = "aantalSpelersOngeldig";

}
