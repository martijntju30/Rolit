package clientEnServer;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import javax.swing.JButton;
import rolit.*;

/**
 * De controller van de client.
 * 
 * @author Martijn & Camilio
 * @author Theo Ruys
 * 
 */
public class Client extends Thread {

	private String clientName;
	private MessageUI mui;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private Game game;
	private Rolit_view view;
	private Ball clientKleur;
	private PrivateKey privkey;
	private boolean doorgaan = true;

	/**
	 * Maakt een client controller.
	 * 
	 * @param name
	 *            De naam van de speler (moet overeenkomen met de naam op de
	 *            authenticatieserver)
	 * @param pass
	 *            Het wachtwoord van de speler (moet overeenkomen met de naam op
	 *            de authenticatieserver)
	 * @param aantalSpelers
	 *            Het aantal spelers waarmee de speler wil spelen
	 * @param host
	 *            Het hostadres van de server
	 * @param port
	 *            Het poortnummer van de server
	 * @param muiArg
	 *            De clientGUI waarin de berichten moeten worden weergegeven.
	 * @throws IOException
	 */
	public Client(String name, String pass, int aantalSpelers,
			InetAddress host, int port, MessageUI muiArg) throws IOException {
		// Zet de GUI in een field
		this.mui = muiArg;
		// Zet de clientName in een field
		this.clientName = name;
		// Maak een Socket naar de server
		try {
			// Haal de privateKey op
			privkey = Authentication.getPrivateKey(name, pass);
			// Als de key niet null is, dan zijn de gegevens juist en dus kan er
			// verbinding gemaakt worden met de server. Een ai kan zich ook
			// aanmelden. In dit geval hoeft er geen authenticatie plaats te
			// vinden.
			if (privkey != null || name.startsWith("ai_")) {
				sock = new Socket(host, port);
				// Zet de in en output op met de server
				in = new BufferedReader(new InputStreamReader(
						sock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(
						sock.getOutputStream()));
				// Schrijf direct je naam en met hoeveel spelers je wil spelen.
				out.write(RolitControl.speelSpel + RolitConstants.msgDelim
						+ name + RolitConstants.msgDelim + aantalSpelers + "\n");
				out.flush();
			} else {// Stel de gegevens zijn niet juist, zet dit dan in de GUI
					// en zet de invoer van de GUI weer beschikbaar.
				mui.addMessage("The username and password are incorrect");
				((ClientGUI) mui).resetInvoer();
			}
		} catch (IOException | NoSuchAlgorithmException
				| InvalidKeySpecException e) {
			// Er waren problemen met de socket. Geef deze error door.
			// Er kan ook een Algoritme probleem zijn. Deze wordt genegeerd
			// omdat het algorimte vast staat in Authentication.java
			mui.addMessage("ERROR: could not create a socket on " + host
					+ " and port " + port);
		}
	}

	/**
	 * Haalt de berichten, die hij van de server krijgt op, en verwerkt deze in
	 * HandleCommand(..)
	 */
	public void run() {
		try {
			while (doorgaan) {
				String line = in.readLine();
				HandleCommand(line);
			}
			shutdown();
		} catch (SocketException e) {
			// Als er een probleem is met de socket, stuur verbinding verloren
			mui.addMessage("Verbinding verloren");
			shutdown();
		} catch (IOException e) {
			// Er is een probleem met het lezen van de invoer. Stuur hiervan een
			// bericht naar de GUI.
			mui.addMessage("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + Arrays.toString(e.getStackTrace())
					+ "\n Informeer de beheerder van deze fout.");
			shutdown();
		}
	}

	/**
	 * Stuurt een bericht naar de ClientHandler. Dit mag dan bepalen wat ermee
	 * moet gebeuren.
	 * 
	 * @param msg
	 *            Het bericht dat je wil sturen. Hierbij is een extra \n niet
	 *            nodig.
	 */
	// @requires msg !=null && !msg.equals("") && !msg.equals(" ");
	public void sendMessage(String msg) {
		try {
			// Controleer of er geen leeg bericht is gestuurd.
			if (msg != null && !msg.equals("") && !msg.equals(" ")) {
				// Er is geen leeg bericht, dus stuur maar naar de server.
				out.write(RolitControl.nieuwChatbericht
						+ RolitConstants.msgDelim + msg + "\n");
				out.flush();
			}
		} catch (IOException e) {
			// Er is een probleem. Schrijf het probleem op de GUI en zorg ervoor
			// dat de client opnieuw kan aanmelden.
			mui.addMessage("ERROR: could not send message: " + msg
					+ ". Please try again!");
			shutdown();
		}
	}

	/**
	 * Stuur een command naar de ClientHandler. Dit mag bepalen wat hij ermee
	 * doet.
	 * 
	 * @param msg
	 *            het commando wat je wil sturen met de eventuele argumenten.
	 *            Hierbij is een extra \n niet nodig.
	 */
	public void sendCommand(String msg) {
		try {
			// Controleer of er geen leeg bericht is gestuurd.
			if (msg != null && !msg.equals("") && !msg.equals(" ")) {
				out.write(msg + "\n");
				out.flush();
			}
		} catch (IOException e) {
			// Er is een probleem. Schrijf het probleem op de GUI en zorg ervoor
			// dat de client opnieuw kan aanmelden.
			mui.addMessage("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + Arrays.toString(e.getStackTrace()));
			shutdown();
		}
	}

	/**
	 * Sluit de client van alle open staande verbindingen. En geeft dit ook
	 * netjes door.
	 */
	public void shutdown() {
		try {
			// Breek de run af.
			doorgaan = false;
			// Sluit de socket.
			sock.close();
			//Geef nog een extra bericht, mocht de opdracht van de server komen.
			mui.addMessage("Verbindin beïndigd.");
		} catch (IOException e) {
			// Mocht er een error zijn, geef dit weer.
			mui.addMessage("ERROR: could not close socketconnection.");
		}
	}

	/**
	 * Geeft de clientName terug
	 * 
	 * @return de clientName
	 */
	// @pure
	public String getClientName() {
		return clientName;
	}

	/**
	 * Verwerkt alle commando's die binnenkomen.
	 * 
	 * @param line
	 *            Het volledige commando.
	 * @throws IOException
	 */
	public void HandleCommand(String line) throws IOException {
		// Kijkt voor de zekerheid of het commando niet leeg is.
		if (line == null || line.equals("")) {
			return;
		}
		// Splits het volledige commando in subonderdelen
		String[] commandline = line.split(RolitConstants.msgDelim);
		// Zet het commando in een aparte string
		String command = commandline[0];
		// kijk welk commando het is
		switch (command) {
		// Het commando is een vraag naar het versleutelen van de nonce
		case (RolitControl.nonce):
			try {
				// /Versleutel de nonce
				String sign = Authentication.signatur(commandline[1], privkey);
				if (sign == null) {
					// Voor de zekerheid, controleer nog een keer of het goed is
					// gegaan met de versleuteling.
					mui.addMessage("The username and password are incorrect");
					((ClientGUI) mui).resetInvoer();
					shutdown();
				}
				// Stuur het bericht naar de handler.
				sendCommand(RolitControl.sign + RolitConstants.msgDelim + sign);

			} catch (InvalidKeyException | NoSuchAlgorithmException
					| SignatureException e) {
				// Verwerk de error
				mui.addMessage("ERROR: er was een exceptie: " + e.getMessage()
						+ "\n met een pad: "
						+ Arrays.toString(e.getStackTrace()));
			}

			break;

		// Het commando is begin spel
		case (RolitControl.beginSpel):
			// Maak een array van kleuren en spelers. Vul de kleuren
			Ball[] kleuren = new Ball[4];
			Player[] p = new Player[4];
			kleuren[0] = Ball.RED;
			kleuren[1] = Ball.GREEN;
			kleuren[2] = Ball.YELLOW;
			kleuren[3] = Ball.BLUE;
			// Vul de sperlers en zet de kleur in een field.
			for (int i = 1; i < commandline.length; i++) {
				p[i - 1] = new Player(commandline[i], kleuren[i - 1]);
				if (commandline[i].equals(clientName)) {
					clientKleur = p[i - 1].getBall();
				}
			}
			// Start een game
			game = new Game(p[0], p[1], p[2], p[3], this, new Leaderboard());
			// Haal de view op
			view = game.view;
			// Zet het label welke kleur de speler is.
			view.kleur.setText("Your color: " + clientKleur.toString());
			// Vernieuw de view.
			view.invalidate();
			break;

		// Het commando is welkom
		case RolitControl.welkom:
			// Voeg het bericht terug.
			mui.addMessage("You were succesfully connected to the server. To start your game, we just need "
					+ commandline[1] + " more player(s).");
			break;

		// Het commando is voor een chatbericht
		case (RolitControl.chatberichtOntvangen):
			// Een chatbericht kan spaties bevatten en dit is toevallig de
			// delimiter, daar moet dus rekening mee gehouden worden. Doe dat
			// eerst.
			String zin = "";
			for (int i = 2; i < commandline.length; i++) {
				zin = zin + " " + commandline[i];
			}

			// De zin is gemaakt kijk nu of de zin eindigt met "has entered]" of
			// "as left]" dan moet je namelijk de hele zin achter elkaar zetten.
			if (zin.endsWith("has entered]") || zin.endsWith("as left]")) {
				mui.addMessage(zin);
			} else {
				//Een normaal chatbericht is binnengekomen. Geef dit juist weer.
				mui.addMessage(commandline[1] + " says:" + zin);
			}
			break;

		//Het commando is dat er een zet is gedaan.
		case RolitControl.zetGedaan:
			//voer de zet door.
			game.takeTurn(Integer.parseInt(commandline[1]), true);
			//Update de game
			game.update();
			//Update de view
			game.view.invalidate();
			break;

		//Het commando is wie er aan de beurt is.
		case RolitControl.aanDeBeurt:
			//Als de client een AI is, laat hem een zet doen als hij aan de beurt is.
			if (clientName.startsWith("ai_")
					&& commandline[1].equals(clientName)) {
				aiMove();
			}
			break;

		//Het commando is spel is afgelopen. Ofwel tussentijds ofwel aan het einde van het spel.
		case RolitControl.spelAfgelopen:
		case RolitControl.gameOver:
			//Reset de invoer
			((ClientGUI) mui).resetInvoer();
			//Maak alle buttons 
			for (JButton button : view.button) {
				button.setEnabled(false);
			}
			game = null;
			mui.addMessage("The game has been won by " + commandline[1]
					+ " you can login again to start another game.");
			break;
			
		//Het commando is dit is een score overzicht
		case RolitControl.scoreOverzicht:
			// Wij doen niets met dit commando.
			break;

		//Het commando is error
		case RolitControl.error:
			String errortype = commandline[1];
			//Verwerk de errors om hun errortype
			switch (errortype) {
			case RolitConstants.errorAantalSpelersOngeldig:
			case RolitConstants.errorGebruikersnaamInGebruik:
			case RolitConstants.errorOngeldigeGebruikersnaam:
				((ClientGUI) mui).resetInvoer();
				break;
				//Ongeldige commando's doen we niets mee, dit is alleen om een oneindige loop te voorkomen.
			case RolitConstants.errorOngeldigCommando:
				break;
				//Ongeldige zet, zet dit in de view en 
			case RolitConstants.errorOngeldigeZet:
				game.view.label
						.setText("The server determines this is not a valid move. Please try again! It's "
								+ game.getCurrentPlayer()
								+ " ("
								+ game.getCurrentPlayer().getBall() + ") turn.");
				break;
			}
			break;
			
		//Het commando is het bord.
		case RolitControl.board:
			// Wij doen niets met dit commando
			break;
		}
	}

	/**
	 * Bepaalt de move van de AI. En stuurt deze zet naar de server.
	 */
	private void aiMove() {
		sendCommand(RolitControl.doeZet
				+ RolitConstants.msgDelim
				+ strategie.Strategys.smartStrategyForHint(game.getBoard(),
						game.getCurrentPlayer()));
	}
	
	/**
	 * Stuurt een errorbericht.
	 * @param errormsg Het errorbericht mét het error command.
	 */
	public void sendError(String errormsg) {
		sendCommand(errormsg);
	}

} // end of class Client
