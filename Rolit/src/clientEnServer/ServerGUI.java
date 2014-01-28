package clientEnServer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

/**
 * ServerGui.
 * 
 * @author Martijn & Camilio
 * @author Theo Ruys
 */
@SuppressWarnings("serial")
public class ServerGUI extends JFrame implements ActionListener, MessageUI {

	private JButton bConnect;
	private JTextField tfPort;
	private JTextArea taMessages;
	private Server server;
	private JButton showLeaderboard;
	private JButton addAI;

	/**
	 * Maakt een serverGUI object
	 */
	public ServerGUI() {
		// Geef de titel van de window.
		super("ServerGUI");
		// Maak de GUI
		buildGUI();
		// Maak de elementen zichtbaar
		setVisible(true);
		// Geef de actie aan die moet gebeuren als er op kruisje is geklikt.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// /Sluit de window
				e.getWindow().dispose();
			}

			public void windowClosed(WindowEvent e) {
				// Als er een leaderboard moet worden opgeslagen, doe dit als
				// hij niet null is.
				boolean leaderboardSaved = !Server.useFileLeaderboard;
				if (server != null && server.leaderboard != null)
					while (!leaderboardSaved) {
						leaderboardSaved = server.saveLeaderboard();
					}
				// Stop de sessie.
				System.exit(0);
			}
		});
	}

	/** builds the GUI. */
	public void buildGUI() {
		setSize(600, 400);

		// Panel p1 - Listen
		// Maak het bovenste paneel. Bestaande uit een gridLayout voor de
		// invoervakken en een button
		JPanel p1 = new JPanel(new FlowLayout());
		JPanel pp = new JPanel(new GridLayout(2, 2));

		// Maak het adreslabel en het tekstvak.
		JLabel lbAddress = new JLabel("Address: ");
		JTextField tfAddress = new JTextField(getHostAddress(), 12);
		tfAddress.setEditable(false);

		// Doe hetzelfde voor de poort.
		JLabel lbPort = new JLabel("Port:");
		tfPort = new JTextField("2727", 5);

		// Voeg ze toe aan de gridlayout.
		pp.add(lbAddress);
		pp.add(tfAddress);
		pp.add(lbPort);
		pp.add(tfPort);

		// Maak een connectbutton en een showLeaderboard
		bConnect = new JButton("Start Listening");
		bConnect.addActionListener(this);
		showLeaderboard = new JButton("Show leaderboard");
		showLeaderboard.addActionListener(this);
		showLeaderboard.setEnabled(false);

		p1.add(pp, BorderLayout.WEST);
		p1.add(bConnect, BorderLayout.EAST);
		p1.add(showLeaderboard, BorderLayout.EAST);

		// Panel p2 - Messages
		// Het tekstvak met daarin alle berichten.
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());

		JLabel lbMessages = new JLabel("Messages:");
		taMessages = new JTextArea("", 15, 50);
		taMessages.setEditable(false);
		p2.add(lbMessages);
		// Geef het vak een scrollbar.
		JScrollPane scroll = new JScrollPane(taMessages);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		p2.add(scroll, BorderLayout.SOUTH);

		// Voeg alles toe aan de container.
		Container cc = getContentPane();
		cc.setLayout(new FlowLayout());
		cc.add(p1);
		cc.add(p2);
	}

	/**
	 * Geeft het Internetadress van deze computer
	 */
	private String getHostAddress() {
		try {
			InetAddress iaddr = InetAddress.getLocalHost();
			return iaddr.getHostAddress();
		} catch (UnknownHostException e) {
			return "?unknown?";
		}
	}

	/**
	 * listener voor de buttons
	 */
	public void actionPerformed(ActionEvent ev) {
		Object src = ev.getSource();
		// Er is op connect gedrukt
		if (src == bConnect) {
			// Start de server
			startListening();
		}
		// Er is op show Leaderboard gedrukt
		else if (src == showLeaderboard) {
			// Geef het leaderboard weer als het leaderboard niet null is.
			if (server != null && server.leaderboard != null) {
				Object[] bord = server.leaderboard
						.getShowBoard(server.leaderboard.getHighscore(3));
				String res = "";
				for (Object regel : bord) {
					res = res + "" + regel;
				}
				addMessage(res);
			}

		}
	}

	/**
	 * Maakt een serverobject die de clients accepteert. De poort kan hierna
	 * niet meer gewijzigd worden. Ook de button connect mag niet meer op worden
	 * gedrukt. Maar het leaderboard juist wel.
	 */
	private void startListening() {
		int port = 0;
		try {
			// Haal de poort op
			port = Integer.parseInt(tfPort.getText());
		} catch (NumberFormatException e) {
			addMessage("ERROR: not a valid portnumber!");
			return;
		}

		// Zorg ervoor dat er niet meer gewijzigd kan worden.
		tfPort.setEditable(false);
		bConnect.setEnabled(false);
		showLeaderboard.setEnabled(true);

		try {
			// Maak de server
			server = new Server(port, this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Start de server
		server.start();

		// Zeg dat de server is gestart
		addMessage("Started listening on port " + port + "...");
		// De server en chatbox zijn nu gestart, start nu ook begin voor de
		// game.
		server.HandleRolitGame();
	}

	/**
	 * Voeg een bericht toe aan het tekstveld.
	 */
	public void addMessage(String msg) {
		taMessages.append(msg + "\n");
	}

	/**
	 * Maak een serverGUI
	 * 
	 * @param args
	 *            eventuele argumenten waar niets mee wordt gedaan.
	 */
	public static void main(String[] args) {
		new ServerGUI();
	}

	/**
	 * Zet de invoer weer terug op zijn originele staat. Dit is handig voor als
	 * er iets niet goed gaat.
	 */
	public void resetInvoer() {
		tfPort.setEditable(true);
		bConnect.setEnabled(true);
		showLeaderboard.setEnabled(false);
	}

}
