package rolit;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import clientEnServer.Client;
import clientEnServer.RolitConstants;
import clientEnServer.RolitControl;
import project.StretchIcon;
import strategie.Strategys;

@SuppressWarnings("serial")
public class Rolit_view extends JFrame implements Observer, ActionListener {

	private Game g;
	private Board bord;
	public JLabel label;
	public JLabel kleur;
	private Container container;
	private JPanel c;
	private Client client;

	private boolean useImg = false;// Wil je images gebruiken? True=ja, False =
									// nee;

	// Maak alle buttons
	public JButton[] button = new JButton[Board.DIM * Board.DIM];
	private JButton hint = new JButton("Hint");
	private JButton start_ai = new JButton("Start ai");

	/**
	 * Maakt een nieuwe view
	 * 
	 * @param g
	 *            de game waar de view bij hoort
	 * @param client
	 *            de client waar de view voor is. Kan null zijn, dan wordt er
	 *            uit gegaan dat het de view voor de server is.
	 */
	public Rolit_view(Game g, Client client) {
		this.client = client;
		this.g = g;
		this.bord = g.getBoard();
		g.addObserver(this);

		// De acties die moeten worden uitgevoerd als er op het kruisje is
		// geklikt.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}

			public void windowClosed(WindowEvent e) {
				// System.exit(0);
			}
		});

		label = new JLabel("Start game, RED is allowed to start the game.");
		kleur = new JLabel("Your color is: --");

		// Bouwt de GUI op.
		start_ai.addActionListener(this);
		hint.addActionListener(this);
		container = getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		c = new JPanel();
		container.add(c);
		c.setLayout(new GridLayout(Board.DIM, Board.DIM));
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		buildBoard();
		if (client == null) {
			for (int i = 0; i < button.length; i++) {
				button[i].setEnabled(false);
			}
		}
		setTitle("Rolit");
		setSize(1000, 1000);
		setVisible(true);
	}

	public void buildBoard() {
		// Leeg de container
		// container.removeAll();
		c.removeAll();
		// Vul de buttons
		for (int i = 0; i < (Board.DIM * Board.DIM); i++) {
			// Zet de button
			button[i] = new JButton("");
			if (useImg) {
				// Maak eerst de background.
				StretchIcon background = null;

				switch (bord.getField(i)) {
				case YELLOW:
					background = createImageIcon("resources/vakje_geel.png");
					break;
				case GREEN:
					background = createImageIcon("resources/vakje_groen.png");
					break;
				case BLUE:
					background = createImageIcon("resources/vakje_blauw.png");
					break;
				case RED:
					background = createImageIcon("resources/vakje_rood.png");
					break;
				default:
					background = createImageIcon("resources/vakje.png");
					break;

				}
				// Moet de background worden gerisized?
				boolean resize = true;
				// Resize de achtergrond
				if (resize) {
					int height = button[i].getHeight();
					int width = button[i].getWidth();
					int size = Math.max(height, width);

					if (size == 0) {
						size = 100;
					}

					Image img = background.getImage();
					Image newimg = img.getScaledInstance(size, size,
							java.awt.Image.SCALE_SMOOTH);
					background = new StretchIcon(newimg);
				}
				button[i].setIcon(background);
				button[i].setBackground(Color.BLACK);
			} else {
				button[i].setText(bord.getField(i) + "");
				switch (bord.getField(i)) {
				case YELLOW:
					button[i].setBackground(Color.YELLOW);
					break;
				case GREEN:
					button[i].setBackground(Color.GREEN);
					break;
				case BLUE:
					button[i].setBackground(Color.BLUE);
					break;
				case RED:
					button[i].setBackground(Color.RED);
					break;
				default:
					button[i].setBackground(Color.BLACK);
					break;

				}
			}
			button[i].setName(i + "");
			button[i].addActionListener(this);
			button[i].setEnabled(true);
			c.add(button[i]);
		}

		container.add(label);
		container.add(kleur);
		container.add(hint);
		container.add(start_ai);

		repaint();
		setVisible(true);
	}

	/**
	 * Zorgt ervoor dat alle buttons worden geupdate op het moment dat er een
	 * wijziging is geweest in het bord.
	 */
	public void showBoard() {
		//Loop het bord door en geef dan de button, de juiste achtergrond.
		for (int i = 0; i < Board.DIM * Board.DIM; i++) {
			button[i].setText(bord.getField(i) + "");
			switch (bord.getField(i)) {
			case YELLOW:
				button[i].setBackground(Color.YELLOW);
				break;
			case GREEN:
				button[i].setBackground(Color.GREEN);
				break;
			case BLUE:
				button[i].setBackground(Color.BLUE);
				break;
			case RED:
				button[i].setBackground(Color.RED);
				break;
			default:
				button[i].setBackground(Color.BLACK);
				break;

			}
		}
		repaint();
		setVisible(true);
	}

	/**
	 * Een speciale versie van ICON.
	 * @param path
	 * @return
	 */
	protected StretchIcon createImageIcon(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new StretchIcon(imgURL, "Een vak van ROLIT");
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Als er een update is dan wordt deze aangeroepen door het object dat dit object observeert. 
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 != null) {
			// Het label wie aan de beurt is.
			Player currentplayer = g.getCurrentPlayer();
			label.setText("It's " + currentplayer.getName() + "'s ("
					+ currentplayer.getBall() + ") turn.");
			//Update het bord.
			showBoard();
			repaint();
			invalidate();
		}
	}

	/**
	 * Er is op een button geklikt.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		ActionEvent e = arg0;
		if (e.getSource() instanceof JButton) {
			if (e.getSource() == hint) {
				// int index =
				// Strategys.smartStrategyForHint(bord,g.getCurrentPlayer());
				int index = Strategys.selectRandom(Strategys.getValidBestMoves(
						bord, g.getCurrentPlayer()));
				button[index].setBackground(Color.GRAY);
			} else if (e.getSource() == start_ai) {
				if (g.getCurrentPlayer().toString().startsWith("ai_")) {
					client.sendCommand(RolitControl.doeZet
							+ RolitConstants.msgDelim
							+ strategie.Strategys.smartStrategyForHint(
									g.getBoard(), g.getCurrentPlayer()));
					start_ai.setEnabled(false);
				}
			} else {
				// Zoek de index op waarop is geklikt.
				String[] chars = arg0.toString().split(" ");
				int button_index = Integer.parseInt(chars[chars.length - 1]);
				// Doe de zet
				g.takeTurn(button_index);
				showBoard();
				start_ai.setEnabled(false);
			}
			repaint();
			invalidate();
		}
	}
}
