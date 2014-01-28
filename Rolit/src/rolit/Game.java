package rolit;

import java.util.Observable;
import java.util.Scanner;
import java.util.Set;

//import strategie.SmartStrategy;
import clientEnServer.Client;
import clientEnServer.RolitConstants;
import clientEnServer.RolitControl;

/**
 * Class for maintaining the Tic Tac Toe game. Lab assignment Module 2
 * 
 * @author Theo Ruys en Arend Rensink
 * @version $Revision: 1.4 $
 */
public class Game extends Observable {

	public int NUMBER_PLAYERS = 0;
	private Board board;/* @ private invariant board != null; */
	public Rolit_view view;
	private Player[] players;
	private int[] score;
	private Leaderboard leaderboard;
	private int current;
	private Client client;

	/**
	 * Maakt een game-object
	 * 
	 * @param s0
	 *            speler 1
	 * @param s1
	 *            speler 2
	 * @param s2
	 *            speler 3
	 * @param s3
	 *            speler 4
	 * @param client
	 *            de client die deze game heeft gestart. null, dan is het de
	 *            server
	 * @param leaderboard
	 *            Het leaderboard waar deze game mee moet communiceren.
	 */
	public Game(Player s0, Player s1, Player s2, Player s3, Client client,
			Leaderboard leaderboard) {
		this.client = client;
		this.leaderboard = leaderboard;
		board = new Board();

		// Kijk hoeveel spelers er zijn
		if (s0 != null && !s0.getName().equals("null")) {
			NUMBER_PLAYERS++;
		}
		if (s1 != null && !s1.getName().equals("null")) {
			NUMBER_PLAYERS++;
		}
		if (s2 != null && !s2.getName().equals("null")) {
			NUMBER_PLAYERS++;
		}
		if (s3 != null && !s3.getName().equals("null")) {
			NUMBER_PLAYERS++;
		}

		// Initialiseer de spelers
		players = new Player[NUMBER_PLAYERS];
		score = new int[NUMBER_PLAYERS];
		if (s0 != null && !s0.getName().equals("null")) {
			players[0] = s0;
		}
		if (s1 != null && !s1.getName().equals("null")) {
			players[1] = s1;
		}
		if (s2 != null && !s2.getName().equals("null")) {
			players[2] = s2;
		}
		if (s3 != null && !s3.getName().equals("null")) {
			players[3] = s3;
		}

		// Zet de eerste speler aan de beurt.
		current = 0;
		// Maak een view voor deze game.
		view = new Rolit_view(this, client);
		// Update het gameveld.
		update();
	}

	/**
	 * Starts the Tic Tac Toe game. <br>
	 * Asks after each ended game if the user want to continue. Continues until
	 * the user does not want to play anymore.
	 */
	public void start() {
		boolean doorgaan = true;
		while (doorgaan) {
			reset();
			play();
			doorgaan = readBoolean("\n> Play another time? (y/n)?", "y", "n");
		}
	}

	/**
	 * Prints a question which can be answered by yes (true) or no (false).
	 * After prompting the question on standard out, this method reads a String
	 * from standard in and compares it to the parameters for yes and no. If the
	 * user inputs a different value, the prompt is repeated and te method reads
	 * input again.
	 * 
	 * @parom prompt the question to print
	 * @param yes
	 *            the String corresponding to a yes answer
	 * @param no
	 *            the String corresponding to a no answer
	 * @return true is the yes answer is typed, false if the no answer is typed
	 */
	private boolean readBoolean(String prompt, String yes, String no) {
		String answer;
		do {
			System.out.print(prompt);
			Scanner in = new Scanner(System.in);
			answer = in.hasNextLine() ? in.nextLine() : null;
			in.close();
		} while (answer == null || (!answer.equals(yes) && !answer.equals(no)));
		return answer.equals(yes);
	}

	/**
	 * Resets het spel. Het bord wordt geleegd en speler 0 mag weer beginnen.
	 */
	protected void reset() {
		current = 0;
		board.reset();
	}

	/**
	 * Plays the Tic Tac Toe game. <br>
	 * First the (still empty) board is shown. Then the game is played until it
	 * is over. Players can make a move one after the other. After each move,
	 * the changed game situation is printed.
	 */
	private void play() {
		update();
		while (!board.gameOver()) {
			players[current].makeMove(board, this);
			nextPlayer();
			update();
		}
		printResult();
	}

	/**
	 * Zeg dat de volgende speler aan de beurt is.
	 */
	protected void nextPlayer() {
		current = (current + 1) % NUMBER_PLAYERS;
	}

	/**
	 * Notify de observers zodat de view wordt aangepast.
	 */
	public void update() {
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Geeft het resultaat van het spel.
	 */
	// @ requires this.board.gameOver();
	private void printResult() {
		if (board.hasWinner()) {
			int playernum = 0;
			if (board.isWinner(players[0].getBall())) {
				playernum = 0;
			} else if (board.isWinner(players[1].getBall())) {
				playernum = 1;
			} else if (board.isWinner(players[2].getBall())) {
				playernum = 2;
			} else if (board.isWinner(players[3].getBall())) {
				playernum = 3;
			}
			view.label.setText("Speler " + players[playernum].getName() + " ("
					+ players[playernum].getBall().toString() + ") has won!");
			view.repaint();

		} else {
			view.label.setText("Draw. There is no winner!");
			view.repaint();
		}
		// De server gaat nu regelen dat de highscores worden toegevoegd.
		if (client == null) {
			for (int i = 0; i < NUMBER_PLAYERS; i++) {
				leaderboard.add(players[i].getName(),
						board.countBalls(players[i].getBall()));
				score[i] = board.countBalls(players[i].getBall());
				leaderboard.showBoard();
			}
			view.invalidate();
		}
	}

	/**
	 * Geeft het echte bord terug. Geen kopie.
	 * 
	 * @return het bord.
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Geeft de huidige speler terug.
	 * 
	 * @return de speler die aan de beurt is.
	 */
	public Player getCurrentPlayer() {
		return players[current];
	}

	/**
	 * Zorgt ervoor dat de regels worden toegepast zodra er een zet wordt
	 * gedaan. Hier gaat het vooral om het overnemen van andere ballen.
	 * 
	 * @param zet de zet die gedaan is.
	 */
	private void applyRules(int zet) {
		Set<Integer> toChange = Validatie.getPossibleTakeOvers(zet, board,
				getCurrentPlayer());

		// Wijzig nu alle vakjes die in toChange staan
		for (Integer i : toChange) {
			board.setField(i, getCurrentPlayer().getBall());
		}
	}

	/**
	 * Voer je beurt uit.
	 * @param i de zet die je wil doen.
	 */
	public void takeTurn(int i) {
		takeTurn(i, false);
	}

	public void takeTurn(int choice, boolean fromServer) {
		if (client == null
				|| (client.getClientName() != null && client.getClientName()
						.equals(getCurrentPlayer().getName())) || fromServer) {
			boolean valid = board.isField(choice) && board.isEmptyField(choice)
					&& Validatie.validMove(choice, board, getCurrentPlayer());
			if (!valid && !fromServer) {
				view.label
						.setText("This is not a valid move. Please try again \nIt's "
								+ getCurrentPlayer().getName()
								+ "'s ("
								+ getCurrentPlayer().getBall() + ") turn.");
				view.repaint();
				view.invalidate();
			} else {// Het is een valid move
				if (fromServer || client == null) {// Is het een commando van de
													// server of is het een
													// lokaal spel? Voer het dan
													// door op het bord
					board.setField(choice, getCurrentPlayer().getBall());
					applyRules(choice);
					nextPlayer();
					update();
					if (board.gameOver()) {
						printResult();
					}
				} else {// De client heeft een zet gedaan, stuur dit naar de
						// server.
					client.sendCommand(RolitControl.doeZet
							+ RolitConstants.msgDelim + choice);
					return;
				}
			}
		} else {
			view.label.setText("It is not your turn. It's "
					+ getCurrentPlayer().getName() + "'s ("
					+ getCurrentPlayer().getBall() + ") turn.");
			view.repaint();
			view.invalidate();
		}
	}

	/**
	 * Geeft een kopie van het bord.
	 * @return kopie van het bord.
	 */
	public Board getBoardCopy() {
		return board.deepCopy();
	}

	/**
	 * Geeft de winnaar terug als die er is.
	 * @return de winnaar als die er is.
	 */
	public String getWinner() {
		return getWinner(false);
	}

	public String getWinner(boolean vroegtijdigEinde) {
		for (Player p : players) {
			if (board.isWinner(p.getBall(), vroegtijdigEinde))
				return p.getName();
		}
		return null;
	}

	/**
	 * Zorgt ervoor dat de game wordt gestopt en de view wordt verwijderd.
	 */
	public void endGame() {
		view.dispose();
		this.deleteObservers();
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
