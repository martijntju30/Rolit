package project;
/**
 * Protocol voor de te gebruiken commando's tussen server en client.
 * @author Christian Versloot
 * @version 1.0 (13/01/2014)
 * 
 * Richting beschrijving:
 * [C] = Client
 * [S] = Server
 *  -> = naar
 *  
 * Delimeter beschrijving:
 * Scheid ALLE commando's en parameters met de delimeter zoals vermeld in RolitConstants.java
 * Scheid ALLE score elementen in score commando's met de score delimeter zoals in RolitConstants.java
 * 
 * Gebruikersnaamcriteria:
 * Controleer ALLE gebruikersnamen op de regular expression zoals vermeld in RolitConstants.java
 *  
 * Param beschrijving:
 * Een @param bij een string geeft aan welke parameters het commando daarnaast ook verwacht.
 * Volgorde van de @params per commando (van boven naar beneden) geeft de volgorde van versturen aan. 
 * 
 * Return beschrijving:
 * Een @return bij een string geeft aan wat het verwachte returntype is bij uitvoeren commando.
 * Meerdere @returns betekent dat meerdere return strings verwacht kunnen worden.
 * De volgorde van ontvangst is hierbij indien niet vermeld NIET aan de orde.
 *
 */
public interface RolitControl {
	
	/**
	 * Stuur een foutmelding naar de client.
	 * Richting: [S] -> [C]
	 * @param errorType in de vorm van een error string (zie RolitConstants.java)
	 */
	String error = "error";

	/**
	 * Vraag het bord op aan de server.
	 * Richting: [C] -> [S]
	 * @return board gevolgd door 64 keer een veld string (zie RolitConstants.java)
	 */
	String getBoard = "getBoard";
	
	/**
	 * Stuur het bord naar een client.
	 * Richting: [S] -> [C]
	 * @param 64 keer een veld string (zie RolitConstants.java)
	 */
	String board = "board";
	
	/**
	 * Speel het spel.
	 * Richting: [C] -> [S]
	 * @param naam - je naam
	 * @param aantalSpelers - het aantal spelers waarmee je wilt spelen.
	 * @return welkom - welkomst commando met resterend aantal spelers
	 * @return error - error commando met errorType
	 */
	String speelSpel = "speelSpel";
	
	/**
	 * Geef een welkomstbericht aan de client.
	 * Richting: [S] -> [C]
	 * @param resterend aantal spelers
	 */
	String welkom = "welkom";
	
	/**
	 * Vertel een client dat het spel kan beginnen.
	 * Richting: [S] -> [C]
	 * @param speler1
	 * @param speler2
	 * @param speler3
	 * @param speler4
	 */
	String beginSpel = "beginSpel";
	
	/**
	 * Vertel de server dat je een zet hebt gedaan.
	 * Richting: [C] -> [S]
	 * @param indexcijfer van 0 tot 63, het te zetten vakje.
	 * @return error met errorType
	 * OF:
	 * @return zetGedaan met vakje en door wie
	 * MET DAARNA:
	 * @return aanDeBeurt met wie aan de beurt is
	 * OF:
	 * @return gameOver met wie het spel heeft gewonnen
	 */
	String doeZet = "doeZet";
	
	/**
	 * Vertel een client dat een zet is gedaan.
	 * Richting: [S] -> [C]
	 * @param vakje - het gezette vakje
	 * @param doorWie - door wie de zet is gedaan
	 */
	String zetGedaan = "zetGedaan";
	
	/**
	 * Vertel een client wie aan de beurt is.
	 * Richting: [S] -> [C]
	 * @param wie - wie aan de beurt is
	 */
	String aanDeBeurt = "aanDeBeurt";
	
	/**
	 * Vertel een client dat het spel afgelopen is.
	 * Richting: [S] -> [C]
	 * @param winnaar - wie het spel heeft gewonnen
	 */
	String gameOver = "gameOver";
	
	/**
	 * Vertel de server dat je het spel verlaat.
	 * Richting: [C] -> [S]
	 * @return spelAfgelopen met wie de winnaar is en wie laf is geweest.
	 * 
	 */
	String verlaatSpel = "verlaatSpel";
	
	/**
	 * Vertel een client dat het spel is afgelopen voor het einde.
	 * Richting: [S] -> [C]
	 * @param winnaar - de winnaar van het spel
	 * @param wie is laf - hij / zij die het spel voortijdig heeft verlaten.
	 */
	String spelAfgelopen = "spelAfgelopen";
	
	/**
	 * Vraag de scores op aan de server.
	 * Richting: [C] -> [S]
	 * @return scoreOverzicht met n keer naam,score,datumtijd; n = aantal scores
	 * datumtijd = aantal seconden vanaf 1 januari 1970
	 */
	String getScores = "getScores";
	
	/**
	 * Geef de scores terug aan de client.
	 * Richting: [S] -> [C]
	 * @param n keer naam,score,datumtijd; n = aantal scores
	 */
	String scoreOverzicht = "scoreOverzicht";
	
	/**
	 * Verstuur een chatbericht.
	 * Richting: [C] -> [S]
	 * @param msg - het bericht
	 * @return chatberichtOntvangen met het bericht
	 */
	String nieuwChatbericht = "nieuwChatbericht";
	
	/**
	 * Vertel een client dat een nieuw chatbericht is gestuurd.
	 * Richting: [S] -> [C]
	 * @param user - de gebruiker
	 * @param msg - het bericht
	 */
	String chatberichtOntvangen = "chatberichtOntvangen";
	
	
}
