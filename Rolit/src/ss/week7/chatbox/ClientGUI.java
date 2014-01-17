package ss.week7.chatbox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * ClientGui. A GUI for the Client.
 * @author  Theo Ruys
 * @version 2005.02.21
 */
@SuppressWarnings("serial")
public class ClientGUI extends JFrame implements ActionListener, MessageUI, DocumentListener {

	private JButton bConnect;
	private JTextField tfHost;
	private JTextField tfPort;
	private JTextField tfName;
	private JTextArea taMessages;
	private JTextField taMyMessages;
	private Client Client;

	/** Constructs a ClientGUI object. */
	public ClientGUI() {
		super("ClientGUI");

		buildGUI();
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/** builds the GUI. */
	public void buildGUI() {
		setSize(600, 500);

		// Panel p1 - Listen

		JPanel p1 = new JPanel(new FlowLayout());
		JPanel pp = new JPanel(new GridLayout(3, 4));

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
		tfName = new JTextField("", 15);
		tfName.getDocument().addDocumentListener(this);
		tfName.addActionListener(this);

		pp.add(lbHost);
		pp.add(tfHost);
		pp.add(lbPort);
		pp.add(tfPort);
		pp.add(lbName);
		pp.add(tfName);

		bConnect = new JButton("Connect");
		bConnect.addActionListener(this);
		bConnect.setEnabled(false);
		p1.add(pp, BorderLayout.WEST);
		p1.add(bConnect, BorderLayout.EAST);

		// Panel p2 - Messages

		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());

		JLabel lbMessages = new JLabel("Messages:");
		taMessages = new JTextArea("", 15, 50);
		taMessages.setEditable(false);
		taMessages.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.red));
		p2.add(lbMessages);
		p2.add(taMessages, BorderLayout.SOUTH);
		
		// Panel p3 - Messages

		JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());

		JLabel lbMyMessages = new JLabel("My message:");
		taMyMessages = new JTextField("", 50);
		taMyMessages.setEditable(false);
		taMyMessages.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.red));
		taMyMessages.addActionListener(this);
		p3.add(lbMyMessages);
		p3.add(taMyMessages, BorderLayout.SOUTH);

		Container cc = getContentPane();
		cc.setLayout(new FlowLayout());
		cc.add(p1);
		cc.add(p3);
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
	 * listener for the "connect" button
	 */
	public void actionPerformed(ActionEvent ev) {
		Object src = ev.getSource();
		if (src == bConnect) {
			startListening();
		}
		else if (src == taMyMessages){
			System.out.println("NEW MESSAGE SEND: "+taMyMessages.getText());
			Client.sendMessage(taMyMessages.getText());
			taMyMessages.setText("");
		}
	}

	/**
	 * Construct a Client-object, which is waiting for clients. The port field and button should be disabled
	 */
	private void startListening() {
		int port = 0;
		InetAddress host;
		try {
			host = InetAddress.getByName("localhost");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String name = "No Name";
		int max = 0;

		try {
			port = Integer.parseInt(tfPort.getText());
			host = InetAddress.getByName(tfHost.getText());
			name = tfName.getText();
		} catch (NumberFormatException e) {
			addMessage("ERROR: not a valid portnumber!");
			return;
		}
		catch (UnknownHostException e) {
				addMessage("ERROR: not a valid host!");
				return;
		}

		tfPort.setEditable(false);
		tfHost.setEditable(false);
		tfName.setEditable(false);
		taMyMessages.setEditable(true);
		bConnect.setEnabled(false);

		try {
			Client = new Client(name, host, port, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Client.start();

		addMessage("Started searching on port " + port + " of host "+ host +"...");
	}

	/** add a message to the textarea  */
	public void addMessage(String msg) {
		taMessages.append(msg + "\n");
		System.out.println(msg);
	}

	/** Start a ClientGUI application */
	public static void main(String[] args) {
		ClientGUI gui = new ClientGUI();
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
	
	private void tfUpdates(DocumentEvent e){
		Object src = e.getDocument();
		System.out.println("src="+src);
		if (src == tfHost.getDocument() || src == tfPort.getDocument() || src == tfName.getDocument()) {
			System.out.println("CHANGE en port: "+Integer.parseInt(tfPort.getText()));
			if (!tfHost.getText().equals("") && !tfPort.getText().equals("") && !tfName.getText().equals("") && !(Integer.parseInt(tfPort.getText())<=0)){
				//De gegevens zijn allemaal ingevuld, klaar voor connect
				bConnect.setEnabled(true);
			}
			else {
				bConnect.setEnabled(false);
			}
		}
	}

}
