package ss.week7.chatbox;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import project.Game;

/**
 * P2 prac wk5. <br>
 * Server. A Thread class that listens to a socket connection on a specified
 * port. For every socket connection with a Client, a new ClientHandler thread
 * is started.
 * 
 * @author Theo Ruys
 * @version 2005.02.21
 */
public class Server extends Thread {
	private int port;
	private MessageUI mui;
	private Collection<ClientHandler> threads = new ArrayList<ClientHandler>();
	private Game game;
	public static final String[] commandslist = new String[] { "move" };

	/** Constructs a new Server object */
	public Server(int portArg, MessageUI muiArg) {
		port = portArg;
		mui = muiArg;
	}

	/**
	 * Listens to a port of this Server if there are any Clients that would like
	 * to connect. For every new socket connection a new ClientHandler thread is
	 * started that takes care of the further communication with the Client.
	 */
	public void run() {
		try {
			ServerSocket serverSock = new ServerSocket(port);
			while (true) {
				Socket newSock = serverSock.accept();
				System.out.println("NEW CLIENT FOUND");
				addHandler(new ClientHandler(this, newSock));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sends a message using the collection of connected ClientHandlers to all
	 * connected Clients.
	 * 
	 * @param msg
	 *            message that is send
	 */
	public void broadcast(String msg) {
		mui.addMessage(msg);
		Iterator<ClientHandler> threadIter = threads.iterator();
		while (threadIter.hasNext()) {
			ClientHandler handler = threadIter.next();
			System.out.println("Send message from server to: " + handler);
			handler.sendMessage(msg + "\n");
		}
	}

	/**
	 * Add a ClientHandler to the collection of ClientHandlers.
	 * 
	 * @param handler
	 *            ClientHandler that will be added
	 */
	public void addHandler(ClientHandler handler) {
		threads.add(handler);
		try {
			handler.start();
			handler.announce();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Remove a ClientHandler from the collection of ClientHanlders.
	 * 
	 * @param handler
	 *            ClientHandler that will be removed
	 */
	public void removeHandler(ClientHandler handler) {
		threads.remove(handler);
	}

	public void command(String line) {
		// Er is een command doorgekomen. Die moet in de server wel worden
		// geprint maar niet naar alle clients gebroadcast in het chatvenster.
		if (game != null) {
			String[] parts = line.split(" ");
			String command = parts[0];
			switch (command) {
			case "move":
				game.takeTurn(game.getBoardCopy().index(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
				break;
			}
		} else {
			mui.addMessage("ERROR: command voor game maar game is nog niet gestart");
		}
	}

} // end of class Server
