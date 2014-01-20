package ss.week7.chatbox;

import java.io.*;
import java.net.*;
import java.util.*;

import project.*;

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
	private List<Game> game = new ArrayList<Game>();
	private Set<ClientHandler> playersFor2 = new HashSet<ClientHandler>();
	private Set<ClientHandler> playersFor3 = new HashSet<ClientHandler>();
	private Set<ClientHandler> playersFor4 = new HashSet<ClientHandler>();
	
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
		// Nu allemaal authenticatie doen en dan zeggen dat er een nieuwe speler
		// is.
		boolean authenticatie = true;
		if (authenticatie) {
			try {
				handler.start();
				int toplayWith = handler.announce();
				switch (toplayWith){
				case 2:
					playersFor2.add(handler);
					break;
				
				case 3:
					playersFor3.add(handler);
					break;
					
				case 4:
					playersFor4.add(handler);
					break;
					
				default:
					handler.sendMessage(RolitConstants.errorAantalSpelersOngeldig+RolitConstants.msgDelim+"Dit aantal spelers bestaat niet.");
				}
				HandleRolitGame();//Mochten er nu genoeg spelers zijn, dan start hij een nieuw spel.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	public void command(String line, int gameID) {
		// Er is een command doorgekomen. Die moet in de server wel worden
		// geprint maar niet naar alle clients gebroadcast in het chatvenster.
		if (game != null && game.get(gameID) !=null) {
			String[] parts = line.split(" ");
			String command = parts[0];
			switch (command) {
			case "move":
				((Game) game).takeTurn(((Game) game.get(gameID)).getBoardCopy().index(
						Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
				break;
			}
		} else {
			mui.addMessage("ERROR: command voor game maar game is nog niet gestart");
		}
	}

	public void HandleRolitGame() {
		if (true){
			if (playersFor2.size()/2 == 1){
				//Er kan een game gestart worden met 2 spelers
				newGame(playersFor2);
			}
			if (playersFor3.size()/3 == 1){
				//Er kan een game gestart worden met 2 spelers
				newGame(playersFor3);
			}
			if (playersFor4.size()/4 == 1){
				//Er kan een game gestart worden met 2 spelers
				newGame(playersFor4);
			}
		}

	}

	private void newGame(Set<ClientHandler> handlers) {
		System.out.println("EEN NIEUWE GAME STARTEN!!");
		int count = 0;
		Ball[] kleuren = new Ball[4];
		Player[] p = new Player[4];
		kleuren[0] = Ball.RED;
		kleuren[1] = Ball.GREEN;
		kleuren[2] = Ball.YELLOW;
		kleuren[3] = Ball.BLUE;
		for (ClientHandler client: handlers) {
			p[count] = new Player(client.getName(), kleuren[count]);
			count++;
		}
		game.add(new Game(p[0], p[1], p[2], p[3]));
		int gameID = game.size()-1;
		for (ClientHandler client: handlers) {
			client.sendMessage(RolitControl.beginSpel+RolitConstants.msgDelim+p[0]+RolitConstants.msgDelim+p[1]+RolitConstants.msgDelim+p[2]+RolitConstants.msgDelim+p[3]);
			client.setGameID(gameID);
			//Verwijder tenslotte de clients uit de playersset
			handlers.remove(client);
		}
		
	}

} // end of class Server
