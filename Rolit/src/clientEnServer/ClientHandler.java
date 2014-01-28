package clientEnServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import rolit.Board;
import rolit.Game;
import rolit.Validatie;

/**
 * ClientHandler.
 * @author Martijn & Camilio
 * @author Theo Ruys
 * @version 2005.02.21
 */
public class ClientHandler extends Thread {

	private Server server;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	protected String clientName;
	public int gameID;
	public Game game;
	protected int preferredPlayers;
	private String nonce;
	private PublicKey pubkey;
	public boolean authenticatie = false;
	private boolean doorgaan = true;

	/** 
	 * Maakt een nieuwe clientHandler. De clientHandlers zijn 
	 * de verwijzingen naae de clients. De clienthandler zorgt 
	 * ervoor dat je met meerdwre clients kan verbinden. In de 
	 * constructor worden ook de input en de output communicatie aangemaakt
	 * @param serverArg Een verwijzing naae de server waar de handler dooe gemaakt is
	 * @param sockArg De socket waarlangs hij communiceert
	 * @throws IOException
	 */
	//@requires server != null && sock != null;
	public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
		if (serverArg != null && sockArg != null) {
			//Maak de fields van de server en de socket.
			server = serverArg;
			sock = sockArg;
			//Maak de input en de output communicatie met de client
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					sock.getOutputStream()));
		}
	}

	/**
	 * Wordt aangeroepen zodra de speler zich aanmeldt bij de server en er 
	 * een handler is gemaakt. Zorgt ervoor dat de basisinformatie wordt 
	 * opgeslagen in de clienthandler. Genereert ook het welkomsbericht 
	 * naar de client. Tenslotte geeft deze methode ook aan dat er een 
	 * nieuwe speler is die een spel wil spelen.
	 * @return Geeft het aantal spelers terug waarmee de speler wil spelen.
	 * @throws IOException
	 */
	public int announce() throws IOException {
		//Controleer of de ingevoerde credentials wel kloppen en dergelijke
		boolean isValid = server.validate(this);
		if (isValid) {
			//Genereer een welkomsbericht.
			//Haal het resterend aantal spelers op.
			int resterendAantalSpelers = 0;
			switch (this.preferredPlayers) {
			case 2:
				resterendAantalSpelers = 2 - server.playersFor2.size();
				break;
			case 3:
				resterendAantalSpelers = 3 - server.playersFor3.size();
				break;
			case 4:
				resterendAantalSpelers = 4 - server.playersFor4.size();
				break;
			}
			//Stuur het welkomsbericht
			sendCommand(RolitControl.welkom + RolitConstants.msgDelim
					+ resterendAantalSpelers);
			//Stuur naar iedereen dat er een nieuwe speler is
			server.broadcastMessage(clientName + RolitConstants.msgDelim+"[" + clientName + " has entered]");
		} else {
			//De validatie is niet goed gegaan. Laat dit opnieuw gebeuren en sluit de connectie.
			sendError(RolitConstants.errorGebruikersnaamInGebruik);
			shutdown();
		}
		return preferredPlayers;
	}

	/**
	 * This method takes care of sending messages from the Client. Every message
	 * that is received, is preprended with the name of the Client, and the new
	 * message is offered to the Server for broadcasting. If an IOException is
	 * thrown while reading the message, the method concludes that the socket
	 * connection is broken and shutdown() will be called.
	 */
	/**
	 * Deze methode vangt de berichten van de client op. Als er een probleem is met de IO dan is er dus iets mis met de connectie dus verwijder de speler en laat hem opnieuw beginnen. Alle binnenkomende berichten worden gevoerd aan de commandhandler zodat ze goed geïnterpreteerd worden. 
	 */
	public void run() {
		try {
			//Blijf kijken of er berichten zijn zolang de clienthandler bestaat. Bij afsluiten wordt doorgaan namelijk op false gezet en daarmee beëindig je de thread.
			while (doorgaan) {
				if (sock.isClosed() || !sock.isConnected()) {
					//De socket is toch afgesloten, sluit dan de clienthandler ook.
					shutdown();
				} else {
					String line = in.readLine();
					// Er komt een command binnen, voer dit juist uit.
					HandleCommand(line);
				}
			}
			//einde van de clienthandler, dus shutdown()
			shutdown();
		} catch (IOException e) {
			System.out.println("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + e.getStackTrace());
			shutdown();
		}
	}

	/**
	 * zorg ervoor dat de commando's die binnenkomen juist worden verwerkt.
	 * @param line Het commando met zijn argumenten.
	 * @throws IOException
	 */
	public void HandleCommand(String line) throws IOException {
		//Controleer of de regel niet leeg is, als dat wel zo is mag er direct worden gestopt.
		if (line == null || line.equals(""))
			return;
		//De regel was niet leeg
		//Splits de inkomende regel in stukjes
		String[] commandline = line.split(RolitConstants.msgDelim);
		//Pak het commando eruit.
		String command = commandline[0];
		//Voeg het commando toe aan de serverGUI
		server.addMessage(line);
		//Kijk wel commando het is
		switch (command) {
		case (RolitControl.sign):
			try {
				//Haal de publickey op
				pubkey = Authentication.getPublickey(clientName);
				//haal de versleutelde nonce op
				String sign = commandline[1];
				//Controleer of dit juist is.
				if (Authentication.decodesignatur(sign, nonce, pubkey)) {
					this.authenticatie = true;
					//De credentials zijn gecontroleerd, laat nu alle andere spelers weten dat er een nieuwe speler is.
					announce();
					break;
				} else {
					//Er klopte iets niet dus laat de client opnieuw proberen
					shutdown();
				}
			} catch (InvalidKeyException | InvalidKeySpecException
					| NoSuchAlgorithmException | SignatureException e) {
				e.printStackTrace();
				shutdown();
			}
			shutdown();
			break;
			
		//Er wordt gevraagd om het bord, geef ze die dan terug
		case RolitControl.getBoard:
			//Haal eerst het juiste bord op.
			Board bord = server.game.get(gameID).getBoardCopy();
			//Haal het commando op
			String bordmessage = RolitControl.board;
			//Voeg nu de waarde van elk veld toe aan het commando
			//@loop_invariant i >=0&&i<Board.DIM^2;
			//@loop_invariant \old(bordmessage).length+RolitConstants.msgDelim.length+bord.getField(i).toString().lenght = bordmessage
			for (int i = 0; i < (Board.DIM * Board.DIM); i++) {
				bordmessage += RolitConstants.msgDelim
						+ bord.getField(i).toString();
			}
			//Stuur het gehele commando met parameters terug
			sendCommand(bordmessage);
			break;
			
		//Er is een commando gekomen dat een speler mee wil doen
		case (RolitControl.speelSpel):
			//Haal zijn naam op en het aantal spelers om mee te spelen
			clientName = commandline[1];
			preferredPlayers = Integer.parseInt(commandline[2]);
			//Kijk of het een computerspeler is, die hoeft zich verder niet te authentificeren
			if (clientName.startsWith("ai_")) {
				this.authenticatie = true;
				announce();
			} else {
				//laat de speler zich authentificeren aan de hand van een nonce
				try {
					nonce = Authentication.makenonce();
					sendCommand(RolitControl.nonce + RolitConstants.msgDelim
							+ nonce);
				} catch (NoSuchAlgorithmException e) {
					//Het algoritme ligt vast dus deze exceptie zou niet mogen komen
					e.printStackTrace();
				}
			}
			break;

		//Er is een nieuw chatbericht
		case (RolitControl.nieuwChatbericht):
			// Een chatbericht kan spaties bevatten en dit is toevallig de
			// delimiter
			String zin = "";
		//@loop_invariant i>=1 && i<commandline.length;
		//@loop_invariant \old(zin).length + 1 + commandline[i].length = zin.length
			for (int i = 1; i < commandline.length; i++) {
				zin = zin + " " + commandline[i];
			}
			//Stuur het bericht door naar iedereen
			server.broadcastMessage(clientName
					+ RolitConstants.msgDelim + zin);
			break;
			
		//Het commando is een zet
		case RolitControl.doeZet:
			//Kijk welke zet het is
			int zet = Integer.parseInt(commandline[1]);
			//Kijk of de zet geldig is
			if (Validatie.validMove(zet, game.getBoard(),
					game.getCurrentPlayer())) {
				//De zet is geldig dus stuur dat naar de clients die aan dat spel meedoen
				server.broadcastCommand(RolitControl.zetGedaan
						+ RolitConstants.msgDelim + zet
						+ RolitConstants.msgDelim + clientName, gameID);
				//doe de zet ook op het serverbord
				game.takeTurn(zet, true);
				//kijk of er na deze zet een winnaar is, zo ja, stuur dit commando, zo nee, stuur wie er aan de beurt is.
				if (game.getBoard().hasWinner()) {
					server.broadcastCommand(RolitControl.gameOver
							+ RolitConstants.msgDelim + game.getWinner(),
							gameID);
				} else {
					server.broadcastCommand(
							RolitControl.aanDeBeurt + RolitConstants.msgDelim
									+ game.getCurrentPlayer(), gameID);
				}
			} else {
				//De zet was niet geldig, stuur dit naar de client die bij deze clienthandler hoort
				sendError(RolitConstants.errorOngeldigeZet);
			}
			break;

		//Iemand heeft het spel verlaten
		case RolitControl.verlaatSpel:
			//Geef dit door aan zijn medespelers en beindig het spel op de server.
			server.broadcastCommand(RolitControl.spelAfgelopen
					+ RolitConstants.msgDelim
					+ server.game.get(gameID).getWinner(true)
					+ RolitConstants.msgDelim + clientName, gameID);
			stopgame();
			break;

		//Het leaderboard wordt opgevraagd.
		case RolitControl.getScores:
			//Haal de scores op en stuur deze naar alle clients
			server.broadcastCommand(RolitControl.scoreOverzicht
					+ server.leaderboard.getCommandScore(), gameID);
			break;

		//Het commando is onbekend, dus stuur ongeldig commando
		default:
			sendError(RolitConstants.errorOngeldigCommando);
			break;
		}
	}

	/**
	 * Stuur een error naar de client
	 * @param errormsg De error zónder het errorcommando
	 */
	//@requires errormsg != null &&!errormsg.equals("") && !errormsg.equals(" "));
	//@requires errormsg.equals(RolitConstants.errorOngeldigeSignature) || errormsg.equals(RolitConstants.errorOngeldigeGebruikersnaam) ||errormsg.equals(RolitConstants.errorOngeldigeZet) || errormsg.equals(RolitConstants.errorOngeldigCommando) || errormsg.equals(RolitConstants.errorGebruikersnaamInGebruik) || errormsg.equals(RolitConstants.errorAantalSpelersOngeldig); 
	public void sendError(String errormsg) {
		sendCommand(RolitControl.error + RolitConstants.msgDelim + errormsg);
	}

	/**
	 * Stuur een bericht naar de client
	 * @param msg Het bericht dat moet worden verstuurd
	 */
	//@erquires msg != null && !msg.equals("") && !msg.equals(" ");
	public void sendMessage(String msg) {
		try {
			//Controleer of er aan de precondities wordt voldaan
			if (msg != null && !msg.equals("") && !msg.equals(" ")) {
				//Stuur het bericht
				out.write(RolitControl.chatberichtOntvangen
						+ RolitConstants.msgDelim + msg + "\n");
				out.flush();
			}
		} catch (IOException e) {
			//En de standaard errorafhandeling
			System.out.println("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + Arrays.toString(e.getStackTrace()));
			shutdown();
		}
	}

	/**
	 * Stuur een commando naar de client
	 * @param het commando mét parameters
	 */
	//@erquires msg != null && !msg.equals("") && !msg.equals(" ");
	public void sendCommand(String msg) {
		try {
			//Controleer de preconditie
			if (msg != null && !msg.equals("") && !msg.equals(" ")) {
				//Stuur het commando
				out.write(msg + "\n");
				out.flush();
			}
		} catch (IOException e) {
			//En de standaard errorafhandeling
			System.out.println("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + Arrays.toString(e.getStackTrace()));
			shutdown();
		}
	}

	/**
	 * Zorgt ervoor dat de clienthandler losgekoppeld wordt van de server en dat de thread stopt. Ook moet er netjes afgehandeld worden dat de client opeens weg is.
	 */
	private void shutdown() {
		//Als het een echte client is dan verlaat hij het spel en zegt dit ook in de chatbox
		if (this.authenticatie){
			server.broadcastCommand(RolitControl.verlaatSpel, gameID);
			server.broadcastMessage(clientName + RolitConstants.msgDelim+"[" + clientName + " has left]");
		}
		//Koppel los van de server
		server.removeHandler(this);
		//Beëindig de thread.
		doorgaan = false;
	}
	
	/**
	 * Stop de game waar deze client in zit.
	 */
	private void stopgame() {
		server.game.get(gameID).endGame();
		server.game.remove(gameID);
	}

	/**
	 * Zet de gameID van de client.
	 * @param gameID2 de plek van de game in de servercollectie van games. (de index van 0 tot size()-1)
	 */
	//@requires gameID2 >=0 && gameID2 < server.game.size();
	//@ensures gameID2 == getGameID();
	protected void setGameID(int gameID2) {
		this.gameID = gameID2;
	}

	/**
	 * Geef de gameID terug
	 * @return de gameID
	 */
	//@pure;
	protected int getGameID() {
		return this.gameID;
	}

	/**
	 * Geeft de clientName
	 * @return de Clientname
	 */
	public String getClientName() {
		return clientName;
	}

} // end of class ClientHandler
