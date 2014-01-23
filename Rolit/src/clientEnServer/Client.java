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
	public Client(String name, String pass, int aantalSpelers, InetAddress host, int port,
			MessageUI muiArg) throws IOException {
		// Set the Message UI
		this.mui = muiArg;
		this.clientName = name;
		// try to open a Socket to the server
		try {
			privkey = Authentication.getPrivateKey(name,
					pass);
			if (privkey != null || name.startsWith("ai_")) {
			sock = new Socket(host, port);
			// create the bufferedreader and writer
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					sock.getOutputStream()));
			out.write(RolitControl.speelSpel + RolitConstants.msgDelim + name
					+ RolitConstants.msgDelim + aantalSpelers + "\n");
			out.flush();
			}
			else {
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
			System.out.println("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + Arrays.toString(e.getStackTrace()));
			shutdown();
		}
	}

	/** close the socket connection. */
	public void shutdown() {///Dit moet goed doen..
		try {
			System.out.println("Shutdown client");
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
				if (sign == null){
					mui.addMessage("The username and password are incorrect");
					((ClientGUI) mui).resetInvoer();
					shutdown();
				}
				sendCommand(RolitControl.sign + RolitConstants.msgDelim+ sign);
				
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
			break;

		case (RolitControl.nieuwChatbericht):
			// Een chatbericht kan spaties bevatten en dit is toevallig de
			// delimiter
			String zin = "";
			for (int i = 1; i < commandline.length; i++) {
				zin = zin + " " + commandline[i];
			}
			mui.addMessage(zin);
			break;

		case RolitControl.doeZet:
			System.out.println(clientName + ": Er wordt een zet gedaan..... "
					+ commandline[1]);
			game.takeTurn(Integer.parseInt(commandline[1]), true);
			break;

		case RolitConstants.errorAantalSpelersOngeldig:
		case RolitConstants.errorGebruikersnaamInGebruik:
		case RolitConstants.errorOngeldigeGebruikersnaam:
			((ClientGUI) mui).resetInvoer();
			break;
		case RolitConstants.errorOngeldigCommando:
		case RolitConstants.errorOngeldigeZet:
			break;

		default:
			sendError(RolitConstants.errorOngeldigCommando);
			break;
		}
	}

	public void sendError(String errormsg) {
		sendMessage(errormsg);
		sendCommand(errormsg);
	}

} // end of class Client
