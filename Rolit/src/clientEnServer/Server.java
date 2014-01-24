package clientEnServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;

import rolit.Ball;
import rolit.Game;
import rolit.Leaderboard;
import rolit.Player;

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
	public Leaderboard leaderboard;
	private Collection<ClientHandler> threads = new ArrayList<ClientHandler>();
	protected List<Game> game = new ArrayList<Game>();
	protected Set<ClientHandler> playersFor2 = new HashSet<ClientHandler>();
	protected Set<ClientHandler> playersFor3 = new HashSet<ClientHandler>();
	protected Set<ClientHandler> playersFor4 = new HashSet<ClientHandler>();
	private File leaderboardFile;

	public static final boolean useFileLeaderboard = false;

	/**
	 * Constructs a new Server object
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 */
	public Server(int portArg, MessageUI muiArg) throws FileNotFoundException,
			ClassNotFoundException, IOException {
		port = portArg;
		mui = muiArg;
		if (useFileLeaderboard) {
			do {
				leaderboard = OpenLeaderboard();
			} while (leaderboard == null);
		} else {
			leaderboard = new Leaderboard();
		}
	}

	private Leaderboard OpenLeaderboard() {
		Leaderboard lb = new Leaderboard();
		addMessage("Please open a leaderboard or press cancel to create a new one.");
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			leaderboardFile = file;

			class Filter extends FileFilter {

				// Accept all directories and all gif, jpg, tiff, or png files.
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					String extension = getExtension(f);
					if (extension != null && extension.equals("txt")) {
						return true;
					}

					return false;
				}

				public String getExtension(File f) {
					String ext = null;
					String s = f.getName();
					int i = s.lastIndexOf('.');

					if (i > 0 && i < s.length() - 1) {
						ext = s.substring(i + 1).toLowerCase();
					}
					return ext;
				}

				// The description of this filter
				public String getDescription() {
					return "Only .txt files";
				}
			}
			fc.addChoosableFileFilter(new Filter());
			fc.setFileFilter(new Filter());
			fc.setAcceptAllFileFilterUsed(false);
			if (new Filter().accept(file)) {
				BufferedReader fileReader;
				try {
					fileReader = new BufferedReader(new FileReader(file));
					while (fileReader.ready()) {
						String input = fileReader.readLine();
						String[] withSpace = input.split("");
						input = "";
						for (String deel : withSpace) {
							if (!deel.equals(" ")&& !deel.equals("----LEADERBOARD----")&& !deel.equals("+-----ID-----+----Name----+---Score----+----Date----+---Time---+")
									&& !deel.equals(""))
								input = input + deel;
						}
						if (!input.equals(" ")) {
							System.out.println("Zonder spaties: "+input);
								String[] parts = input.split("/");
								System.out.println(Arrays.toString(parts));
								if (parts.length >5){
								lb.add(parts[2], parts[3], parts[4], parts[5]);
								System.out.println("Een nieuw record toegevoegd.");
								}
							}
						
					}
					fileReader.close();
					return lb;
				} catch (IOException e) {
					addMessage("This is not a valid file");
					return null;
				}

			} else {
				addMessage("This is not an textfile.");
				return null;
			}

		} else {
			addMessage("New leaderboard created.");
			return new Leaderboard();
		}
	}

	public String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public String getFileWithoutExtension(File f) {
		String res = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			res = s.substring(0, i);
		}
		return res;
	}

	public boolean saveLeaderboard() {
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File((leaderboardFile != null && leaderboardFile
				.getName() != null) ? leaderboardFile.getName()
				: "leaderboard.txt"));
		fc.setName((leaderboardFile != null && leaderboardFile.getName() != null) ? leaderboardFile
				.getName() : "leaderboard.txt");
		fc.setCurrentDirectory(leaderboardFile);
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			try {
				PrintWriter textfile = new PrintWriter(new FileWriter(file));
				Object[] bord = leaderboard.getShowBoard(leaderboard.getBoard());
				for (Object regel:bord){
					textfile.write(regel+ "\n");
				}
				
				
				textfile.close();

				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			addMessage("Saving: " + file.getName() + ".");
		} else {
			addMessage("Save command cancelled by user.");
			return true;
		}
		return false;
	}

	/**
	 * Listens to a port of this Server if there are any Clients that would like
	 * to connect. For every new socket connection a new ClientHandler thread is
	 * started that takes care of the further communication with the Client.
	 */
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSock = new ServerSocket(port);
			while (true) {
				Socket newSock = serverSock.accept();
				addHandler(new ClientHandler(this, newSock));
			}
		} catch (IOException e) {
			mui.addMessage("Error, please try again on a different port!");
			((ServerGUI) mui).resetInvoer();
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
	public void broadcastMessage(String msg) {
		mui.addMessage(msg);
		for (ClientHandler handler: threads) {
			handler.sendMessage(msg + "\n");
		}
	}

	public void broadcastCommand(String msg, int ID) {
		mui.addMessage(msg);
		for (ClientHandler handler: threads) {
			if (handler.gameID == ID) {
				handler.sendCommand(msg + "\n");
			}
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
		handler.start();
	}

	private boolean checkValidUsername(String clientName) {
		int counter = 0;
		for (ClientHandler client : threads) {
			if (client.getClientName().equals(clientName)) {
				counter++;
			}
		}
		System.out.println("De gebruikers naam " + clientName + " komt "
				+ counter + " keer voor. Dus return: " + (counter <= 1));
		return counter <= 1;
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

	// public void command(String line, int gameID, ClientHandler from) {
	// // Er is een command doorgekomen. Die moet in de server wel worden
	// // geprint maar niet naar alle clients gebroadcast in het chatvenster.
	// if (game != null && game.get(gameID) != null) {
	// String[] parts = line.split(" ");
	// String command = parts[0];
	// switch (command) {
	// case RolitControl.doeZet:
	// int zet = Integer.parseInt(parts[1]);
	// if (Validatie.validMove(zet, game.get(gameID).getBoard(), game
	// .get(gameID).getCurrentPlayer())) {
	// game.get(gameID).takeTurn(zet, true);
	// broadcast(line);
	// }
	// else {
	// from.sendMessage(RolitConstants.errorOngeldigeZet);
	// }
	// break;
	// }
	// } else {
	// mui.addMessage("ERROR: command voor game maar game is nog niet gestart");
	// }
	// }

	public void HandleRolitGame() {
		if (playersFor2.size() / 2 == 1) {
			// Er kan een game gestart worden met 2 spelers
			newGame(playersFor2);
		}
		if (playersFor3.size() / 3 == 1) {
			// Er kan een game gestart worden met 2 spelers
			newGame(playersFor3);
		}
		if (playersFor4.size() / 4 == 1) {
			// Er kan een game gestart worden met 2 spelers
			newGame(playersFor4);
		}

	}

	private void newGame(Set<ClientHandler> handlers) {
		int count = 0;
		Ball[] kleuren = new Ball[4];
		Player[] p = new Player[] { null, null, null, null };
		kleuren[0] = Ball.RED;
		kleuren[1] = Ball.GREEN;
		kleuren[2] = Ball.YELLOW;
		kleuren[3] = Ball.BLUE;
		for (ClientHandler client : handlers) {
			p[count] = new Player(client.getClientName(), kleuren[count]);
			count++;
		}
		Game g = new Game(p[0], p[1], p[2], p[3], null, leaderboard);
		int gameID = game.size();//Dit is altijd vrij dus zet hem hier neer.
		game.add(gameID, g);
		for (ClientHandler client : handlers) {
			client.game = g;
			client.sendCommand(RolitControl.beginSpel + RolitConstants.msgDelim
					+ p[0] + RolitConstants.msgDelim + p[1]
					+ RolitConstants.msgDelim + p[2] + RolitConstants.msgDelim
					+ p[3]);
			client.setGameID(gameID);
		}

		// Verwijder tenslotte de clients uit de playersset
		handlers.removeAll(handlers);

	}

	public void addMessage(String line) {
		mui.addMessage(line);
	}

	public boolean validate(ClientHandler handler) {
		if (!checkValidUsername(handler.getClientName())) {
			return false;
		} else {// Het is een valide gebruikersnaam
			// Nu allemaal authenticatie doen en dan zeggen dat er een nieuwe
			// speler
			// is.
			if (handler.authenticatie) {
				int toplayWith = handler.preferredPlayers;
				switch (toplayWith) {
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
					handler.sendMessage(RolitConstants.errorAantalSpelersOngeldig
							+ RolitConstants.msgDelim
							+ "Dit aantal spelers bestaat niet.");
				}
				HandleRolitGame();// Mochten er nu genoeg spelers zijn, dan
									// start hij een nieuw spel.
				return true;
			}
		}
		return false;
	}

} // end of class Server
