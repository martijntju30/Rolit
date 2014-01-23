package clientEnServer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import rolit.Game;

/**
 * ServerGui. A GUI for the Server.
 * 
 * @author Theo Ruys
 * @version 2005.02.21
 */
public class ServerGUI extends JFrame implements ActionListener, MessageUI {

	private JButton bConnect;
	private JTextField tfPort;
	private JTextArea taMessages;
	private Server server;
	private Game game;
	private JButton showLeaderboard;
	private JButton addAI;

	/** Constructs a ServerGUI object. */
	public ServerGUI() {
		super("ServerGUI");

		buildGUI();
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}

			public void windowClosed(WindowEvent e) {
				boolean leaderboardSaved = !Server.useFileLeaderboard;
				if (server != null && server.leaderboard != null)
				while (!leaderboardSaved) {
					leaderboardSaved = server.saveLeaderboard();
				}
				System.exit(0);
			}
		});
	}

	/** builds the GUI. */
	public void buildGUI() {
		setSize(600, 400);

		// Panel p1 - Listen

		JPanel p1 = new JPanel(new FlowLayout());
		JPanel pp = new JPanel(new GridLayout(2, 2));

		JLabel lbAddress = new JLabel("Address: ");
		JTextField tfAddress = new JTextField(getHostAddress(), 12);
		tfAddress.setEditable(false);

		JLabel lbPort = new JLabel("Port:");
		tfPort = new JTextField("2727", 5);

		pp.add(lbAddress);
		pp.add(tfAddress);
		pp.add(lbPort);
		pp.add(tfPort);

		bConnect = new JButton("Start Listening");
		bConnect.addActionListener(this);
		showLeaderboard = new JButton("Show leaderboard");
		showLeaderboard.addActionListener(this);
		showLeaderboard.setEnabled(false);
		addAI = new JButton("Add AI");
		addAI.addActionListener(this);
		addAI.setEnabled(false);

		p1.add(pp, BorderLayout.WEST);
		p1.add(bConnect, BorderLayout.EAST);
		p1.add(showLeaderboard, BorderLayout.EAST);
		//p1.add(addAI, BorderLayout.SOUTH);
		//p1.add(buttons, BorderLayout.EAST);
		
		// Panel p2 - Messages

		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());

		JLabel lbMessages = new JLabel("Messages:");
		taMessages = new JTextArea("", 15, 50);
		taMessages.setEditable(false);
		p2.add(lbMessages);
		JScrollPane scroll = new JScrollPane(taMessages);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		p2.add(scroll, BorderLayout.SOUTH);

		Container cc = getContentPane();
		cc.setLayout(new FlowLayout());
		cc.add(p1);
		cc.add(p2);
	}

	/** returns the Internetadress of this computer */
	private String getHostAddress() {
		try {
			InetAddress iaddr = InetAddress.getLocalHost();
			return iaddr.getHostAddress();
		} catch (UnknownHostException e) {
			return "?unknown?";
		}
	}

	/**
	 * listener for the "Start Listening" button
	 */
	public void actionPerformed(ActionEvent ev) {
		Object src = ev.getSource();
		if (src == bConnect) {
			startListening();
		}
		else if (src == showLeaderboard){
			if (server != null && server.leaderboard != null) {
				Object[] bord = server.leaderboard.getShowBoard(server.leaderboard.getHighscore(3));
				String res = "";
				for (Object regel:bord){
					res = res +""+regel;
				}
				addMessage(res);
			}
			
		}
		else if (src == addAI){
			if (server != null) {
				System.out.println("============================\n Dit werkt nog niet \n ========================");
				addMessage("============================\n Dit werkt nog niet \n ========================");
			}
			
		}
	}

	/**
	 * Construct a Server-object, which is waiting for clients. The port field
	 * and button should be disabled
	 */
	private void startListening() {
		int port = 0;
		int max = 0;

		try {
			port = Integer.parseInt(tfPort.getText());
		} catch (NumberFormatException e) {
			addMessage("ERROR: not a valid portnumber!");
			return;
		}

		tfPort.setEditable(false);
		bConnect.setEnabled(false);
		showLeaderboard.setEnabled(true);

		try {
			server = new Server(port, this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.start();

		addMessage("Started listening on port " + port + "...");
		// De server en chatbox zijn nu gestart, start nu ook begin voor de
		// game.
		server.HandleRolitGame();
	}

	/** add a message to the textarea */
	public void addMessage(String msg) {
		taMessages.append(msg + "\n");
	}

	/** Start a ServerGUI application */
	public static void main(String[] args) {
		ServerGUI gui = new ServerGUI();
	}

}
