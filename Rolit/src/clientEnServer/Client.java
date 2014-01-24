package clientEnServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.swing.JButton;

import project.*;
import rolit.Ball;
import rolit.Game;
import rolit.Leaderboard;
import rolit.Player;
import rolit.Rolit_view;

/**
 * P2 prac wk4. <br>
 * Client.
 * 
 * @author Theo Ruys
 * @version 2005.02.21
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
	 * Constructs a Client-object and tries to make a socket connection
	 */
	public Client(String name, String pass, int aantalSpelers,
			InetAddress host, int port, MessageUI muiArg) throws IOException {
		// Set the Message UI
		this.mui = muiArg;
		this.clientName = name;
		// try to open a Socket to the server
		try {
			privkey = Authentication.getPrivateKey(name, pass);
			if (privkey != null || name.startsWith("ai_")) {
				sock = new Socket(host, port);
				// create the bufferedreader and writer
				in = new BufferedReader(new InputStreamReader(
						sock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(
						sock.getOutputStream()));
				out.write(RolitControl.speelSpel + RolitConstants.msgDelim
						+ name + RolitConstants.msgDelim + aantalSpelers + "\n");
				out.flush();
			} else {
				mui.addMessage("The username and password are incorrect");
				((ClientGUI) mui).resetInvoer();
			}
		} catch (IOException | NoSuchAlgorithmException
				| InvalidKeySpecException e) {
			mui.addMessage("ERROR: could not create a socket on " + host
					+ " and port " + port);
		}
	}

	/**
	 * Reads the messages in the socket connection. Each message will be
	 * forwarded to the MessageUI
	 */
	public void run() {
		try {
			while (doorgaan) {
				String line = in.readLine();
				HandleCommand(line);
			}
			shutdown();
		} catch (SocketException e) {
			mui.addMessage("Verbinding verloren");
			shutdown();
		} catch (IOException e) {
			mui.addMessage("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + e.getStackTrace());
			shutdown();
		}
	}

	/** send a message to a ClientHandler. */
	public void sendMessage(String msg) {
		try {
			if (msg != null && !msg.equals("") && !msg.equals(" ")) {
				out.write(RolitControl.nieuwChatbericht
						+ RolitConstants.msgDelim + msg + "\n");
				out.flush();
			}
		} catch (IOException e) {
			mui.addMessage("ERROR: could not send message: " + msg
					+ ". Please try again!");
			shutdown();
		}
	}

	public void sendCommand(String msg) {
		try {
			if (msg != null && !msg.equals("") && !msg.equals(" ")) {
				System.out.println("Client: ik heb een command: " + msg);
				out.write(msg + "\n");
				out.flush();
			}
		} catch (IOException e) {
			System.err.println("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + Arrays.toString(e.getStackTrace()));
			shutdown();
		}
	}

	/** close the socket connection. */
	public void shutdown() {// /Dit moet goed doen..
		try {
			System.out.println("Shutdown client");
			if (!sock.isClosed()) {
			sendCommand(RolitControl.verlaatSpel);
			}
			doorgaan = false;
			sock.close();
		} catch (IOException e) {
			mui.addMessage("ERROR: could not close socketconnection.");
		}
	}

	/** returns the client name */
	public String getClientName() {
		return clientName;
	}

	public void HandleCommand(String line) throws IOException {
		if (line == null || line.equals("")) {
			return;
		}
		String[] commandline = line.split(RolitConstants.msgDelim);
		String command = commandline[0];

		switch (command) {
		case (RolitControl.nonce):
			try {
				String sign = Authentication.signatur(commandline[1], privkey);
				if (sign == null) {
					mui.addMessage("The username and password are incorrect");
					((ClientGUI) mui).resetInvoer();
					shutdown();
				}
				sendCommand(RolitControl.sign + RolitConstants.msgDelim + sign);

			} catch (InvalidKeyException | NoSuchAlgorithmException
					| SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case (RolitControl.beginSpel):
			Ball[] kleuren = new Ball[4];
			Player[] p = new Player[4];
			kleuren[0] = Ball.RED;
			kleuren[1] = Ball.GREEN;
			kleuren[2] = Ball.YELLOW;
			kleuren[3] = Ball.BLUE;
			for (int i = 1; i < commandline.length; i++) {
				p[i - 1] = new Player(commandline[i], kleuren[i - 1]);
				if (commandline[i].equals(clientName)) {
					clientKleur = p[i - 1].getBall();
				}
			}
			game = new Game(p[0], p[1], p[2], p[3], this, new Leaderboard());
			view = game.view;
			view.kleur.setText("Your color: " + clientKleur.toString());
			view.invalidate();
			//Als de huidige speler (de beginspeler) een ai is, dan moet hij een zet doen.
//			if (p[0].getName().startsWith("ai_")){
//				aiMove();
//			}
			break;
			
		case RolitControl.welkom:
			mui.addMessage("You were succesfully connected to the server. To start your game, we just need "+commandline[1]+" more player(s).");
			break;
		case (RolitControl.chatberichtOntvangen):
			// Een chatbericht kan spaties bevatten en dit is toevallig de
			// delimiter
			String zin = "";
			for (int i = 2; i < commandline.length; i++) {
				zin = zin + " " + commandline[i];
			}
			if (zin.endsWith("has entered]") || zin.endsWith("as left]")){
				mui.addMessage(zin);
			}
			else {
			mui.addMessage(commandline[1]+" says:"+zin);
			}
			break;

		case RolitControl.zetGedaan:
			System.out.println(clientName + ": Er wordt een zet gedaan..... "
					+ commandline[1]);
			game.takeTurn(Integer.parseInt(commandline[1]), true);
			game.update();
			game.view.invalidate();
			break;
			
		case RolitControl.aanDeBeurt:
			if (clientName.startsWith("ai_") && commandline[1].equals(clientName)){
				aiMove();
				}
			break;


		case RolitControl.spelAfgelopen:
		case RolitControl.gameOver:
			((ClientGUI) mui).resetInvoer();
			for (JButton button: view.button){
				button.setEnabled(false);
			}
			game = null;
			mui.addMessage("The game has been won by "+commandline[1]+" you can login again to start another game.");
			break;
			
		case RolitControl.scoreOverzicht:
			//Wij doen niets met dit commando.
			break;

		case RolitControl.error:
			String errortype = commandline[1];
			switch (errortype) {
			case RolitConstants.errorAantalSpelersOngeldig:
			case RolitConstants.errorGebruikersnaamInGebruik:
			case RolitConstants.errorOngeldigeGebruikersnaam:
				((ClientGUI) mui).resetInvoer();
				break;

			case RolitConstants.errorOngeldigCommando:
				break;
			case RolitConstants.errorOngeldigeZet:
				game.view.label.setText("The server determines this is not a valid move. Please try again! It's "+game.getCurrentPlayer()+ " ("+game.getCurrentPlayer().getBall()+") turn."); 
				break;
			}
			break;
		case RolitControl.board:
			//Wij doen niets met dit commando
			break;
		}
	}

	private void aiMove() {
		System.out.println("De AI doet een zet!");
		sendCommand(RolitControl.doeZet+RolitConstants.msgDelim+strategie.Strategys.smartStrategyForHint(game.getBoard(), game.getCurrentPlayer()));
	}

	public void sendError(String errormsg) {
		sendCommand(errormsg);
	}

} // end of class Client
