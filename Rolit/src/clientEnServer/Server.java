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
 * Een server waarop clients zich kunnen aanmelden. Voor die clients wordt dan
 * een clienthandler aangemaakt zodat de server clients kan blijven accepteren.
 * 
 * @author Martijn & Camilio
 * @author Theo Ruys
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

	public static final boolean useFileLeaderboard = true;

	/**
	 * De serverconstructor. De gegevens worden ingegeven via de ServerGUI.
	 * 
	 * @param portArg
	 *            de poort waarop geluisterd moet worden
	 * @param muiArg
	 *            een verwijzing naar de GUI.
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Server(int portArg, MessageUI muiArg) throws FileNotFoundException,
			ClassNotFoundException, IOException {
		// Zet de poort en de GUI in fields zodat die overal aan te roepen zijn
		// in de klasse.
		port = portArg;
		mui = muiArg;
		// Als er is ingesteld (in een booleanfield) dat er een leaderboard moet
		// worden opgehaald uit een bestand, voer dit dan uit.
		if (useFileLeaderboard) {
			do {
				// Open het leaderboard aan de hand van een JFileChoser
				leaderboard = OpenLeaderboard();
				// Als dit null oplevert, dan is het dus geen leaderboard dan
				// moet er opnieuw een scherm worden geopend.
			} while (leaderboard == null);
		} else {
			// Maak anders gewoon een leeg leaderboard.
			leaderboard = new Leaderboard();
		}
	}

	/**
	 * Opent met behulp van een JFileChooser een leaderboardfile wat eogenlijk
	 * een tekstbestand is.
	 * 
	 * @return null als er geen goede file is gekozen of anders een
	 *         leaderboardobject.
	 */
	private Leaderboard OpenLeaderboard() {
		// Maak eerst een leeg leaderboard aan die gevuld kan worden.
		Leaderboard lb = new Leaderboard();
		// zorg ervoor dat de gebruiker weet wat er gebeurt en wat er verwacht
		// wordt.
		addMessage("Please open a leaderboard or press cancel to create a new one.");
		// Maak een mooie window aan die je een bestand laat kiezen
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);

		// Maak een filter aan die controleert of het wel een txt bestand is.
		class Filter extends FileFilter {

			// Accepteer alle mappen zodat je kan bladeren en alle txtbestanden.
			public boolean accept(File f) {
				// Kijk eerst of de file niet null is.
				if (f == null) {
					return false;
				}
				if (f.isDirectory()) {
					return true;
				}
				String extension = getExtension(f);
				if (extension != null && extension.equals("txt")) {
					return true;
				}

				return false;
			}

			// Haal de extensie op. Dit is alles achter de laatste punt.
			public String getExtension(File f) {
				String ext = null;
				String s = f.getName();
				int i = s.lastIndexOf('.');

				if (i > 0 && i < s.length() - 1) {
					ext = s.substring(i + 1).toLowerCase();
				}
				return ext;
			}

			// De description van dit filter
			public String getDescription() {
				return "Only .txt files";
			}
		}
		// Voeg het filter toe
		fc.addChoosableFileFilter(new Filter());
		fc.setFileFilter(new Filter());
		fc.setAcceptAllFileFilterUsed(false);

		// Kijk of er een bestand is geselecteerd
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// Pak het bestand dat geselecteerd is en plak dat in de field
			// leaderboardFile.
			File file = fc.getSelectedFile();
			leaderboardFile = file;
			// Kijk of het een geldig bestand is
			if (new Filter().accept(file)) {
				// Lees alle waarden uit, uit het bestand.
				BufferedReader fileReader;
				try {
					// Maak de filereader
					fileReader = new BufferedReader(new FileReader(file));
					// Als de filereader content heeft
					while (fileReader.ready()) {
						// Lees de eerst volgende lijn.
						String input = fileReader.readLine();
						// Haal alle spaties en rare opmaak eruit.
						String[] withSpace = input.split("");
						input = "";
						for (String deel : withSpace) {
							if (!deel.equals(" ")
									&& !deel.equals("----LEADERBOARD----")
									&& !deel.equals("+-----ID-----+----Name----+---Score----+----Date----+---Time---+")
									&& !deel.equals(""))
								input = input + deel;
						}
						// Kijk nu of je geen lege regel hebt.
						if (!input.equals(" ")) {
							// Splits de invoer op een / want dit is de
							// delimiter
							String[] parts = input.split("/");
							// Controleer of je een geldig resultaat hebt, voeg
							// dit resultaat dan toe.
							if (parts.length > 5) {
								lb.add(parts[2], parts[3], parts[4], parts[5]);
							}
						}

					}
					// Als alles gelezen is, kan de filereader worden gesloten.
					fileReader.close();
					// Alle waarden zitten weer in het leaderboard object, dus
					// geef deze terug.
					return lb;
				} catch (IOException e) {
					// Als een ongeldig bestand wordt aangeroepen, zeg dit tegen
					// de gebruiker en return null zodat hij het opnieuw kan
					// proberen.
					addMessage("This is not a valid file");
					return null;
				}

			} else {
				// Als een ongeldig bestand wordt aangeroepen, zeg dit tegen
				// de gebruiker en return null zodat hij het opnieuw kan
				// proberen.
				addMessage("This is not an textfile.");
				return null;
			}

		} else {
			// Als er op cancel gedrukt is dan mag er een nieuw, leeg,
			// leaderboard worden gemaakt.
			addMessage("New leaderboard created.");
			return new Leaderboard();
		}
	}

	// Haal de extensie op. Dit is alles achter de laatste punt.
	// public String getExtension(File f) {
	// String ext = null;
	// String s = f.getName();
	// int i = s.lastIndexOf('.');
	//
	// if (i > 0 && i < s.length() - 1) {
	// ext = s.substring(i + 1).toLowerCase();
	// }
	// return ext;
	// }

	// public String getFileWithoutExtension(File f) {
	// String res = "";
	// String s = f.getName();
	// int i = s.lastIndexOf('.');
	//
	// if (i > 0 && i < s.length() - 1) {
	// res = s.substring(0, i);
	// }
	// return res;
	// }

	/**
	 * Als de server wordt afgesloten dan moet het leaderboard ook weer worden
	 * opgeslagen. Dit gebeurt hier met een filewriter.
	 * 
	 * @return of het opslaan gelukt is.
	 */
	public boolean saveLeaderboard() {
		// Kies eerst waar het moet worden opgeslagen.
		JFileChooser fc = new JFileChooser();
		// Geef de naam weer
		fc.setSelectedFile(new File((leaderboardFile != null && leaderboardFile
				.getName() != null) ? leaderboardFile.getName()
				: "leaderboard.txt"));
		fc.setName((leaderboardFile != null && leaderboardFile.getName() != null) ? leaderboardFile
				.getName() : "leaderboard.txt");
		// Ga naar de map waar het bestand is opgehaald.
		fc.setCurrentDirectory(leaderboardFile);
		int returnVal = fc.showSaveDialog(null);
		// Er is een goed bestand gekozen om op te slaan.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			try {
				// Zet nu het leaderboard in het bestand.
				PrintWriter textfile = new PrintWriter(new FileWriter(file));
				Object[] bord = leaderboard
						.getShowBoard(leaderboard.getBoard());
				for (Object regel : bord) {
					textfile.write(regel + "\n");
				}
				// Sluit de filewriter
				textfile.close();
				// Zeg dat het geluik is.
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			addMessage("Saving: " + file.getName() + ".");
		} else {
			// Er is op cancel gedrukt. Geef dit weer aan de gebruiker en zeg
			// dat opslaan mislukt is.
			addMessage("Save command cancelled by user.");
			return true;
		}
		// Er is iets anders gebeurt, maar het leaderboard is niet opgeslagen,
		// zeg dit.
		return false;
	}

	/**
	 * Controleert of er clients zijn die willen verbinden met de server.
	 */
	public void run() {
		try {
			@SuppressWarnings("resource")
			// Maak de serversocket
			ServerSocket serverSock = new ServerSocket(port);
			// Blijf eeuwig kijken of er clients zijn. Deze loop wordt
			// afgebroken zodra de sessie wordt beëindigd met het kruisje van de
			// window.
			while (true) {
				// Kijkt of er een client is
				Socket newSock = serverSock.accept();
				// Voegt de client toe aan een clienthandler zodat de server
				// weer vrij is.
				addHandler(new ClientHandler(this, newSock));
			}
		} catch (IOException e) {
			// Er is een error, laat dit weten en zorg ervoor dat de gebruiker
			// het opnieuw kan proberen.
			mui.addMessage("Error, please try again on a different port!");
			((ServerGUI) mui).resetInvoer();
		}
	}

	/**
	 * Stuurt een bericht naar alle clients.
	 * 
	 * @param msg
	 *            het bericht.
	 */
	public void broadcastMessage(String msg) {
		// Voeg het bericht eerst toe aan de GUI.
		mui.addMessage(msg);
		// Loop dan alle clients langs en stuur het bericht.
		for (ClientHandler handler : threads) {
			handler.sendMessage(msg + "\n");
		}
	}

	/**
	 * Stuurt een commando naar alle clients van een game.
	 * 
	 * @param msg
	 *            het commando mét parameters
	 * @param ID
	 *            het ID van de game waarvan de clients het commando moeten
	 *            ontvangen.
	 */
	public void broadcastCommand(String msg, int ID) {
		// Voeg het bericht eerst toe aan de GUI.
		mui.addMessage(msg);
		// Loop dan alle clients langs en stuur het commando als de gameID
		// overeenkomt met het ingevoerde ID.
		for (ClientHandler handler : threads) {
			if (handler.gameID == ID) {
				handler.sendCommand(msg + "\n");
			}
		}
	}

	/**
	 * Voeg een ClientHandler toe aan de collectie van ClientHandlers. En start
	 * deze handler zodat hij de communicatie verder kan regelen.
	 * 
	 * @param handler
	 *            ClientHandler die moet worden toegevoegd.
	 */
	// @requires handler != null;
	// @ensures \old(threads).size()+1 = threads.size();
	public void addHandler(ClientHandler handler) {
		threads.add(handler);
		handler.start();
	}

	/**
	 * Controleer of de clientname geldig is. Hij is niet geldig als hij al een
	 * keer voorkomt of spaties bevat.
	 * 
	 * @param clientName
	 * @return
	 */
	private boolean checkValidUsername(String clientName) {
		// Controleer eerst of er geen spaties in zitten.
		if (clientName.contains(" ")) {
			return false;
		}
		// Kijk vervolgens hoe vaak de gebruikersnaam in de threads voorkomt.
		// Als dit 1 is, dan klopt het.
		int counter = 0;
		for (ClientHandler client : threads) {
			if (client.getClientName().equals(clientName)) {
				counter++;
			}
		}
		return counter <= 1;
	}

	/**
	 * Verwijder een ClientHandler van de collectie van ClientHanlders.
	 * 
	 * @param handler
	 *            ClientHandler die moet worden verwijderd.
	 */
	// @requires handler != null;
	// @ensures \old(threads).size()-1 = threads.size();
	public void removeHandler(ClientHandler handler) {
		threads.remove(handler);
	}

	/**
	 * Kijk elke keer of er voldoende spelers zijn.
	 */
	public void HandleRolitGame() {
		if (playersFor2.size() / 2 == 1) {
			// Er kan een game gestart worden met 2 spelers
			newGame(playersFor2);
		}
		if (playersFor3.size() / 3 == 1) {
			// Er kan een game gestart worden met 3 spelers
			newGame(playersFor3);
		}
		if (playersFor4.size() / 4 == 1) {
			// Er kan een game gestart worden met 4 spelers
			newGame(playersFor4);
		}

	}

	/**
	 * Maakt een nieuw spel en informeert de spelers hiervan.
	 * 
	 * @param handlers
	 *            de spelers die aan het nieuwe spel mee gaan doen.
	 */
	private void newGame(Set<ClientHandler> handlers) {
		int count = 0;
		// Maak de kleuren
		Ball[] kleuren = new Ball[4];
		kleuren[0] = Ball.RED;
		kleuren[1] = Ball.GREEN;
		kleuren[2] = Ball.YELLOW;
		kleuren[3] = Ball.BLUE;
		// Maak de spelers
		Player[] p = new Player[] { null, null, null, null };
		// Voeg de spelers uit de collectie toe aan de spelers van de game.
		for (ClientHandler client : handlers) {
			p[count] = new Player(client.getClientName(), kleuren[count]);
			count++;
		}
		// Start de game op de server/
		Game g = new Game(p[0], p[1], p[2], p[3], null, leaderboard);
		// Bepaald het ID
		int gameID = game.size();// Dit is altijd vrij dus zet hem hier neer.
		// Zet de game op de ID in de lijst.
		game.add(gameID, g);
		// Stuur naar alle spelers dat het spel is begonnen.
		for (ClientHandler client : handlers) {
			client.game = g;
			client.sendCommand(RolitControl.beginSpel + RolitConstants.msgDelim
					+ p[0] + RolitConstants.msgDelim + isNull(p[1])
					+ RolitConstants.msgDelim + isNull(p[2])
					+ RolitConstants.msgDelim + isNull(p[3]));
			client.setGameID(gameID);
		}

		// Verwijder tenslotte de clients uit de playersset, zodat ze niet nog
		// een keer geteld worden.
		handlers.removeAll(handlers);

	}

	/**
	 * Controleert of de player niet null is. Dit scheelt veel if statements.
	 * 
	 * @param p
	 *            de player
	 * @return de playernaam of niets.
	 */
	private String isNull(Player p) {
		if (p != null) {
			return p.getName();
		}
		return "";
	}

	/**
	 * Voeg een bericht toe aan de GUI.
	 * 
	 * @param line
	 *            Het bericht voor de GUI.
	 */
	public void addMessage(String line) {
		mui.addMessage(line);
	}

	/**
	 * Controleert of de ingevoerde credentials wel kloppen voor de
	 * clientHandler. Het gaat hier vooral om valide gebruikersnamen. Verder zal
	 * dit ook de handler toevoegen aan de collecties van spelers die een spel
	 * willen spelen.
	 * 
	 * @param handler
	 *            de handler die gecontroleerd moet worden.
	 * @return true als het een valide gebruikersnaam is.
	 */
	public boolean validate(ClientHandler handler) {
		if (!checkValidUsername(handler.getClientName())) {
			return false;
		} else {// Het is een valide gebruikersnaam
			// Als de authentificatie ook klopt, dan mag er worden toegevoegd.
			// Dan is de gebruikersnaam dus ook valide.
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
