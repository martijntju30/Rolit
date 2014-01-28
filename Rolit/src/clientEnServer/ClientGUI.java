package clientEnServer;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * ClientGui. Een GUI voor de Client.
 * 
 * @author Martijn & Camilio
 * @author Theo Ruys
 */
@SuppressWarnings("serial")
public class ClientGUI extends JFrame implements ActionListener, MessageUI,
		DocumentListener {

	private JButton bConnect;
	public JTextField tfHost;
	public JTextField tfPort;
	public JTextField tfName;
	public JPasswordField tfPass;
	public JTextField tfSpelers;
	private JTextArea taMessages;
	private JTextField taMyMessages;
	private Client Client;

	/**
	 * Maakt een ClientGUI.
	 */
	public ClientGUI() {
		// Zet de kop van het venster ClientGUI
		super("ClientGUI");
		// Maakt de GUI
		buildGUI();
		// Zorgt ervoor dat alle elementen zichtbaar worden
		setVisible(true);
		// Zorg ervoor dat, zodra er op sluiten wordt gedrukt, er een bericht
		// wordt gestuurd en dan moet de window worden gesloten.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (Client != null) {
					Client.sendCommand(RolitControl.verlaatSpel);
				}
				e.getWindow().dispose();
			}

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/**
	 * Maakt een ClientGUI. Het verschil is dat hier nieuwe standaard waarden
	 * zijn toegevoegd.
	 * 
	 * @param host
	 *            Het hostadres
	 * @param port
	 *            Het poortnummer
	 * @param name
	 *            De naam van de speler
	 * @param pass
	 *            Het wachtwoord van de speler
	 * @param spelers
	 *            Het aantal spelers waarmee gespeeld moet worden.
	 */
	public ClientGUI(String host, String port, String name, String pass,
			String spelers) {
		// Zet de kop van het venster ClientGUI
		super("ClientGUI");
		// Maakt de GUI
		buildGUI();
		// Zorgt ervoor dat alle elementen zichtbaar worden
		setVisible(true);
		// Zorg ervoor dat, zodra er op sluiten wordt gedrukt, er een bericht
		// wordt gestuurd en dan moet de window worden gesloten.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Client.sendCommand(RolitControl.verlaatSpel);
				e.getWindow().dispose();
			}

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});

		// Voer de ingegeven waarden in.
		tfHost.setText(host);
		tfPort.setText(port);
		tfName.setText(name);
		tfPass.setText(pass);
		tfSpelers.setText(spelers);
	}

	/** builds the GUI. */
	public void buildGUI() {
		// Geef de afmetingen van de window.
		setSize(600, 500);

		// Panel p1 - Listen
		// Maak een nieuw paneel voor het bovenste deel van de GUI.
		JPanel p1 = new JPanel(new FlowLayout());
		JPanel pp = new JPanel(new GridLayout(5, 2));

		// Maak labels en teksvakken toe voor host, poort, naam, pass en spelers
		JLabel lbHost = new JLabel("Hostname: ");
		tfHost = new JTextField(getHostAddress(), 12);
		tfHost.setEditable(true);
		tfHost.getDocument().addDocumentListener(this);
		tfHost.addActionListener(this);
		JLabel lbPort = new JLabel("Port:");
		tfPort = new JTextField("2727", 5);
		tfPort.getDocument().addDocumentListener(this);
		tfPort.addActionListener(this);
		JLabel lbName = new JLabel("Name:");
		tfName = new JTextField("player_", 15);
		tfName.getDocument().addDocumentListener(this);
		tfName.addActionListener(this);
		JLabel lbPass = new JLabel("Password:");
		tfPass = new JPasswordField("", 15);
		tfPass.getDocument().addDocumentListener(this);
		tfPass.addActionListener(this);
		JLabel lbSpelers = new JLabel("Aantal spelers:");
		tfSpelers = new JTextField("", 15);
		tfSpelers.getDocument().addDocumentListener(this);
		tfSpelers.addActionListener(this);

		// Voeg labels en teksvakken toe voor host, poort, naam, pass en spelers
		// aan het paneel
		pp.add(lbHost);
		pp.add(tfHost);
		pp.add(lbPort);
		pp.add(tfPort);
		pp.add(lbName);
		pp.add(tfName);
		pp.add(lbPass);
		pp.add(tfPass);
		pp.add(lbSpelers);
		pp.add(tfSpelers);

		// Maak de connectbutton en voeg deze toe aan paneel1
		bConnect = new JButton("Connect");
		bConnect.addActionListener(this);
		bConnect.setEnabled(false);
		p1.add(pp, BorderLayout.WEST);
		p1.add(bConnect, BorderLayout.EAST);

		// Panel p2 - Messages
		// Maak een paneel voor het weergeven van berichten
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		// Maak een label voor berichten
		JLabel lbMessages = new JLabel("Messages:");
		// Maak een tekstvak
		taMessages = new JTextArea("", 15, 50);
		taMessages.setEditable(false);
		// Geef het een grijze rand, zodat het opvalt
		taMessages.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2,
				Color.gray));
		p2.add(lbMessages);
		JScrollPane scroll = new JScrollPane(taMessages);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		p2.add(scroll, BorderLayout.SOUTH);

		// Panel p3 - Messages
		// Maak een paneel voor het schrijven van berichten
		JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		// Maak een label
		JLabel lbMyMessages = new JLabel("My message:");
		// Maak een tekstvak
		taMyMessages = new JTextField("", 50);
		taMyMessages.setEditable(false);
		// Geef het een grijze rand, zodat het opvalt
		taMyMessages.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2,
				Color.gray));
		taMyMessages.addActionListener(this);
		p3.add(lbMyMessages);
		p3.add(taMyMessages, BorderLayout.SOUTH);

		// Voeg alle panelen toe aan de container.
		Container cc = getContentPane();
		cc.setLayout(new FlowLayout());
		cc.add(p1);
		cc.add(p3);
		cc.add(p2);
	}

	/**
	 * Geeft het adres van deze computer
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
	 * listener voor de "connect" button en het berichtenveld
	 */
	public void actionPerformed(ActionEvent ev) {
		// haal de bron op van het event
		Object src = ev.getSource();
		if (src == bConnect) {
			// er is op Connect geklikt, voer de methode startListening() uit.
			startListening();
		} else if (src == taMyMessages) {
			// er is op enter gedrukt, verzend het bericht en maak het tekstvak
			// weer leeg.
			Client.sendMessage(taMyMessages.getText());
			taMyMessages.setText("");
		}
	}

	/**
	 * Maak een Client aan en start die client.
	 */
	private void startListening() {
		//maak de standaard variabelen
		int port = 0;
		InetAddress host;
		try {
			host = InetAddress.getByName("localhost");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String name = "No Name";
		String pass = "No Pass";
		int spelers = 4;

		try {
			//Wijzig de standaard variabelen naar de variabelen die zijn ingevoerd
			port = Integer.parseInt(tfPort.getText());
			host = InetAddress.getByName(tfHost.getText());
			name = tfName.getText();
			pass = new String(tfPass.getPassword());
			spelers = Integer.parseInt(tfSpelers.getText());
		} catch (NumberFormatException e) {
			addMessage("ERROR: not a valid portnumber!");
			return;
		} catch (UnknownHostException e) {
			addMessage("ERROR: not a valid host!");
			return;
		}
		//Zorg ervoor de alle invoervelden niet meer gewijzigd kunnen worden.
		tfPort.setEditable(false);
		tfHost.setEditable(false);
		tfName.setEditable(false);
		tfPass.setEditable(false);
		tfSpelers.setEditable(false);
		taMyMessages.setEnabled(true);
		taMyMessages.setEditable(true);
		bConnect.setEnabled(false);

		try {
			//Maak de client
			Client = new Client(name, pass, spelers, host, port, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Start de client
		Client.start();
		//Geef alles door naar de clientGUI.
		addMessage("Started searching on port " + port + " of host " + host
				+ "...");
		addMessage("If you are not connected in 5 seconds, please try again.");
	}

	/**
	 * Voeg een bericht toe aan het tekstvak
	 */
	public void addMessage(String msg) {
		taMessages.append(msg + "\n");
	}

	/**
	 * Maak een clientGUI
	 * @param args eventuele argumenten waar niets mee wordt gedaan
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ClientGUI gui = new ClientGUI();
	}

	/**
	 * Zet alle velden weer naar de originele staat zodat ze weer te wijzigen zijn en stop de client
	 */
	public void resetInvoer() {
		tfHost.setEditable(true);
		tfHost.setEnabled(true);
		tfPort.setEditable(true);
		tfPort.setEnabled(true);
		tfName.setEditable(true);
		tfName.setEnabled(true);
		tfPass.setEditable(true);
		tfPass.setEnabled(true);
		tfSpelers.setEditable(true);
		tfSpelers.setEnabled(true);
		bConnect.setEnabled(true);
		taMyMessages.setEditable(false);
		taMyMessages.setEnabled(false);
		//Stop de client
		Client.shutdown();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		tfUpdates(e);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		tfUpdates(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		tfUpdates(e);
	}
	/**
	 * Kijk of de velden goed zijn ingevoerd. En als dit zo is, zet de connectbutton zodat die aangeklikt is
	 * @param e
	 */
	private void tfUpdates(DocumentEvent e) {
		//Controleer of er iets is verandered in een van de vakken
		Object src = e.getDocument();
		if (src == tfHost.getDocument() || src == tfPort.getDocument()
				|| src == tfName.getDocument() || src == tfPass.getDocument()
				|| src == tfSpelers.getDocument()) {
			//Controleer vervolgens of het geldige invoer is.
			if (!tfHost.getText().equals("") && !tfPort.getText().equals("")
					&& !tfName.getText().equals("")
					&& !tfPass.getPassword().equals("")
					&& !tfSpelers.getText().equals("")
					&& !(Integer.parseInt(tfPort.getText()) <= 0)
					&& !(Integer.parseInt(tfSpelers.getText()) <= 1)
					&& !(Integer.parseInt(tfSpelers.getText()) > 4)) {
				// De gegevens zijn allemaal ingevuld, klaar voor connect
				bConnect.setEnabled(true);
			} else {
				bConnect.setEnabled(false);
			}
		}
	}

}
