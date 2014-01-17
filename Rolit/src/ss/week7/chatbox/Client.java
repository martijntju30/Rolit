package ss.week7.chatbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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

	/**
	 * Constructs a Client-object and tries to make a socket connection
	 */
	public Client(String name, InetAddress host, int port, MessageUI muiArg)
			throws IOException {
		// Set the Message UI
		this.mui = muiArg;
		// try to open a Socket to the server
		try {
			sock = new Socket(host, port);
			// create the bufferedreader and writer
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					sock.getOutputStream()));
			out.write(name+"\n");
			out.flush();
		} catch (IOException e) {
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
			while (true) {
				
				String line = in.readLine();
				line = applyFilter(line);
				if (line != null) {
					mui.addMessage(line);
				}
			}
		} catch (SocketException e) {
			mui.addMessage("Verbinding verloren");
		} catch (IOException e) {
			mui.addMessage("ERROR: er was een exceptie: "+e.getMessage()+"\n met een pad: "+e.getStackTrace());
		}
	}

	private String applyFilter(String line) {
		String[] parts = line.split(" ");
		String command = parts[0];
		for (int i = 0; i<Server.commandslist.length; i++){
			if (command.equals(Server.commandslist[i])){
				return null;
			}
		}
		return line;
	}

	/** send a message to a ClientHandler. */
	public void sendMessage(String msg) {
		try {
			System.out.println("Try to send message: "+msg);
			out.write(msg + "\n");
			out.flush();
		} catch (IOException e) {
			mui.addMessage("ERROR: could not send message: "+msg+". Please try again!");
		}
	}

	/** close the socket connection. */
	public void shutdown() {
		try {
			sock.close();
		} catch (IOException e) {
			mui.addMessage("ERROR: could not close socketconnection.");
		}
	}

	/** returns the client name */
	public String getClientName() {
		return clientName;
	}

} // end of class Client
