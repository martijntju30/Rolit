package clientEnServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import rolit.Game;
import rolit.Validatie;

/**
 * ClientHandler.
 * 
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
	 * Constructs a ClientHandler object Initialises both Data streams. @
	 * requires server != null && sock != null;
	 */
	public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
		if (serverArg != null && sockArg != null) {
			server = serverArg;
			sock = sockArg;
			// create the bufferedreader and writer
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					sock.getOutputStream()));
		}
	}

	/**
	 * Reads the name of a Client from the input stream and sends a broadcast
	 * message to the Server to signal that the Client is participating in the
	 * chat. Notice that this method should be called immediately after the
	 * ClientHandler has been constructed.
	 * 
	 * @return
	 */
	public int announce() throws IOException {
		boolean isValid = server.validate(this);
		if (isValid) {
			server.broadcastMessage("[" + clientName + " has entered]");
		} else {
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
	public void run() {
		try {
			while (doorgaan) {
				if (sock.isClosed() || !sock.isConnected()) {
					System.out.println("Socket is closed");
					shutdown();
				} else {
					String line = in.readLine();
					// Er komt een command binnen, voer dit juist uit.
					HandleCommand(line);
				}
			}
			shutdown();
		} catch (IOException e) {
			System.out.println("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + e.getStackTrace());
			shutdown();
		}
	}

	public void HandleCommand(String line) throws IOException {
		if (line == null || line.equals(""))
			return;
		System.out
				.println("Er is een nieuw command aangeroepen om uitgevoerd te worden. \n Dit command is: "
						+ line);
		String[] commandline = line.split(RolitConstants.msgDelim);
		String command = commandline[0];
		server.addMessage(line);
		switch (command) {
		case (RolitControl.sign):
			try {
				pubkey = Authentication.getPublickey(clientName);
				String sign = commandline[1];
				System.out.println("SIGN = " + new String(sign));
				if (Authentication.decodesignatur(sign, nonce, pubkey)) {
					this.authenticatie = true;
					announce();
					break;
				} else {
					shutdown();
				}
			} catch (InvalidKeyException | InvalidKeySpecException
					| NoSuchAlgorithmException | SignatureException e) {
				e.printStackTrace();
				shutdown();
			}
			shutdown();
			break;
		case (RolitControl.speelSpel):
			clientName = commandline[1];
			preferredPlayers = Integer.parseInt(commandline[2]);
			if (clientName.startsWith("ai_")) {
				this.authenticatie = true;
				announce();
			} else {
				try {
					nonce = Authentication.makenonce();
					sendCommand(RolitControl.nonce + RolitConstants.msgDelim
							+ nonce);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// server.broadcastMessage("[" + clientName + " has entered]");
			}
			break;

		case (RolitControl.nieuwChatbericht):
			// Een chatbericht kan spaties bevatten en dit is toevallig de
			// delimiter
			String zin = "";
			for (int i = 1; i < commandline.length; i++) {
				zin = zin + " " + commandline[i];
			}
			server.broadcastMessage(clientName + " says: " + zin);
			break;
		case RolitControl.doeZet:
			int zet = Integer.parseInt(commandline[1]);
			System.out.println("=====De zet is: " + zet);
			if (Validatie.validMove(zet, game.getBoard(),
					game.getCurrentPlayer())) {
				server.broadcastCommand(line, gameID);
				System.out.println("DOE BROADCAST VAN ZET");
				game.takeTurn(zet, true);
			} else {
				sendError(RolitConstants.errorOngeldigeZet);
			}
			break;

		case RolitConstants.errorAantalSpelersOngeldig:
		case RolitConstants.errorGebruikersnaamInGebruik:
		case RolitConstants.errorOngeldigCommando:
		case RolitConstants.errorOngeldigeGebruikersnaam:
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

	/**
	 * This method can be used to send a message over the socket connection to
	 * the Client. If the writing of a message fails, the method concludes that
	 * the socket connection has been lost and shutdown() is called.
	 */
	public void sendMessage(String msg) {
		try {
			if (msg != null && !msg.equals("") && !msg.equals(" ")) {
				out.write(RolitControl.nieuwChatbericht
						+ RolitConstants.msgDelim + msg + "\n");
				out.flush();
			}
		} catch (IOException e) {
			System.out.println("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + Arrays.toString(e.getStackTrace()));
			shutdown();
		}
	}

	/**
	 * This method can be used to send a message over the socket connection to
	 * the Client. If the writing of a message fails, the method concludes that
	 * the socket connection has been lost and shutdown() is called.
	 */
	public void sendCommand(String msg) {
		try {
			if (msg != null && !msg.equals("") && !msg.equals(" ")) {
				System.out.println("Handler: ik heb een command: " + msg);
				out.write(msg + "\n");
				out.flush();
			}
		} catch (IOException e) {
			System.out.println("ERROR: er was een exceptie: " + e.getMessage()
					+ "\n met een pad: " + Arrays.toString(e.getStackTrace()));
			shutdown();
		}
	}

	/**
	 * This ClientHandler signs off from the Server and subsequently sends a
	 * last broadcast to the Server to inform that the Client is no longer
	 * participating in the chat.
	 */
	private void shutdown() {
		System.out.println("Remove thread");
		server.removeHandler(this);
		// if (this.authenticatie){
		server.broadcastMessage("[" + clientName + " has left]");
		// }
		doorgaan = false;
	}

	protected void setGameID(int gameID2) {
		this.gameID = gameID2;
	}

	protected int getGameID() {
		return this.gameID;
	}

	public String getClientName() {
		return clientName;
	}

} // end of class ClientHandler