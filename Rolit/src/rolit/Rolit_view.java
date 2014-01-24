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
	public JButton[] button = new JButton[bord.DIM * bord.DIM];
	private JButton hint = new JButton("Hint");
	private JButton start_ai = new JButton("start ai");

	public Rolit_view(Game g, Client client) {
		this.client = client;
		this.g = g;
		this.bord = g.getBoard();
		g.addObserver(this);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}

			public void windowClosed(WindowEvent e) {
				//System.exit(0);
			}
		});

		label = new JLabel("Start game, RED is allowed to start the game.");
		kleur = new JLabel("Your color is: --");

		/*if(client.getName()!=null&&client.getName().startsWith("ai_")&&g.getCurrentPlayer().getName()!= null&&g.getCurrentPlayer().getName().startsWith("ai_")){

			start_ai.addActionListener(this);
			
		}*/
		start_ai.addActionListener(this);
		hint.addActionListener(this);
		container = getContentPane();
		// c = getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		c = new JPanel();
		container.add(c);
		c.setLayout(new GridLayout(bord.DIM, bord.DIM));
		c.setAlignmentX(Component.LEFT_ALIGNMENT);// 0.0
		label.setAlignmentX(Component.LEFT_ALIGNMENT);// 20.0
		buildBoard();
		if (client == null){
			for (int i = 0; i<button.length; i++){
				button[i].setEnabled(false);
			}
		}
		setTitle("Rolit");
		setSize(1000, 1000);
		setVisible(true);
	}

	public void buildBoard() {
		System.out.println("HET BORD MOET EEN UPDATE KRIJGEN");
		// Leeg de container
		// container.removeAll();
		c.removeAll();
		// Vul de buttons
		for (int i = 0; i < (bord.DIM * bord.DIM); i++) {
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
		// c.setVisible(true);
		setVisible(true);
	}

	public void showBoard() {
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

	protected StretchIcon createImageIcon(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new StretchIcon(imgURL, "Een vak van ROLIT");
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 != null) {
			System.out.println("Het bord moet worden geüpdate");
			// Het label wie aan de beurt is.
			Player currentplayer = g.getCurrentPlayer();
			label.setText("It's " + currentplayer.getName() + "'s ("
					+ currentplayer.getBall() + ") turn.");
			showBoard();
			repaint();
			invalidate();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		ActionEvent e = arg0;
		if (e.getSource() instanceof JButton) {
			if (e.getSource() == hint) {
				int index = Strategys.smartStrategyForHint(bord, g.getCurrentPlayer());
				button[index].setBackground(Color.GRAY);
			} else if(e.getSource() == start_ai){
				if(g.getCurrentPlayer().toString().startsWith("ai_")){
				client.sendCommand(RolitControl.doeZet+RolitConstants.msgDelim+strategie.Strategys.smartStrategyForHint(g.getBoard(), g.getCurrentPlayer()));
				}
			} else {
				String[] chars = arg0.toString().split(" ");
				int button_index = Integer.parseInt(chars[chars.length - 1]);
				// button[button_index].setEnabled(false);
				g.takeTurn(button_index);
				showBoard();
			}
			repaint();
			invalidate();
		}
	}
}
