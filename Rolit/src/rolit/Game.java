package rolit;

import java.awt.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Scanner;
import java.util.Set;

import strategie.SmartStrategy;
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

	// -- Instance variables -----------------------------------------

	public int NUMBER_PLAYERS = 0;

	/*
	 * @ private invariant board != null;
	 */
	/**
	 * The board.
	 */
	private Board board;
	public Rolit_view view;

	/*
	 * @ private invariant players.length == NUMBER_PLAYERS; private (\forall
	 * int i; 0 <= i && i < NUMBER_PLAYERS; players[i] != null);
	 */
	/**
	 * The 2 players of the game.
	 */
	private Player[] players;
	private int[] score;

	private Leaderboard leaderboard;

	/*
	 * @ private invariant 0 <= current && current < NUMBER_PLAYERS;
	 */
	/**
	 * Index of the current player.
	 */
	private int current;

	private Client client;

	// -- Constructors -----------------------------------------------
	/*
	 * @ requires s0 != null; requires s1 != null;
	 */
	/**
	 * Creates a new Game object.
	 * 
	 * @param s0
	 *            the first player
	 * @param s1
	 *            the second player
	 */
	public Game(Player s0, Player s1, Player s2, Player s3, Client client,
			Leaderboard leaderboard) {
		this.client = client;
		this.leaderboard = leaderboard;
		board = new Board();
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

		players = new Player[NUMBER_PLAYERS];
		score = new int[NUMBER_PLAYERS];
		if (s0 != null && !s0.getName().equals("null")) {
			if (s0.getName().startsWith("ai_")){
				players[0] = new SmartStrategy(s0.getName(), s0.getBall());
				this.addObserver((SmartStrategy) players[0]);
			}
			else {
				players[0] = s0;
			}
		}
		if (s1 != null && !s1.getName().equals("null")) {
			if (s1.getName().startsWith("ai_")){
				players[1] = new SmartStrategy(s1.getName(), s1.getBall());
				this.addObserver((SmartStrategy) players[1]);
			}
			else {
				players[1] = s1;
			}
		}
		if (s2 != null && !s2.getName().equals("null")) {
			if (s2.getName().startsWith("ai_")){
				players[2] = new SmartStrategy(s2.getName(), s2.getBall());
				this.addObserver((SmartStrategy) players[2]);
			}
			else {
				players[2] = s2;
			}
		}
		if (s3 != null && !s3.getName().equals("null")) {
			if (s3.getName().startsWith("ai_")){
				players[3] = new SmartStrategy(s3.getName(), s3.getBall());
				this.addObserver((SmartStrategy) players[3]);
			}
			else {
				players[3] = s3;
			}
		}

		current = 0;
		//if (client != null) {
			view = new Rolit_view(this, client);
		//}
		update();
	}

	// -- Commands ---------------------------------------------------

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
	 * Resets the game. <br>
	 * The board is emptied and player[0] becomes the current player.
	 */
	protected void reset() {
		current = 0;
		board.reset();
	}

	// B{Spel.play}
	/**
	 * Plays the Tic Tac Toe game. <br>
	 * First the (still empty) board is shown. Then the game is played until it
	 * is over. Players can make a move one after the other. After each move,
	 * the changed game situation is printed.
	 */
	private void play() {
		// E{Spel}
		update();
		while (!board.gameOver()) {
			players[current].makeMove(board, this);
			nextPlayer();
			update();
		}
		printResult();
		// B{Spel}
		// I{Spel} // [BODY-NOG-TOE-TE-VOEGEN]
	}

	protected void nextPlayer() {
		current = (current + 1) % NUMBER_PLAYERS;
		this.setChanged();
		this.notifyObservers(current+"");
	}

	// E{Spel.play}

	/**
	 * Prints the game situation.
	 */
	public void update() {
		this.setChanged();
		this.notifyObservers();
	}

	/*
	 * @ requires this.board.gameOver();
	 */

	/**
	 * Prints the result of the last game. <br>
	 */
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
		if (client == null) {
			System.out.println("De server zal nu de scores aan de highscore toevoegen");
			for (int i = 0; i < NUMBER_PLAYERS; i++) {
				leaderboard.add(players[i].getName(),
						board.countBalls(players[i].getBall()));
				score[i] = board.countBalls(players[i].getBall());
				leaderboard.showBoard();
			}
			view.invalidate();
			System.out.println("De server is klaar");
		}
	}

	public Board getBoard() {
		return board;
	}

	public Player getCurrentPlayer() {
		return players[current];
	}

	private void applyRules(int zet) {
		Set<Integer> toChange = Validatie.getPossibleTakeOvers(zet, board,
				getCurrentPlayer());

		// Wijzig nu alle vakjes die in toChange staan
		for (Integer i : toChange) {
			board.setField(i, getCurrentPlayer().getBall());
		}
	}

	public void takeTurn(int i) {
		takeTurn(i, false);
	}

	public void takeTurn(int choice, boolean fromServer) {
		if (client == null
				|| (client.getClientName() != null && client.getClientName()
						.equals(getCurrentPlayer().getName())) || fromServer) {
			boolean valid = board.isField(choice) && board.isEmptyField(choice)
					&& Validatie.validMove(choice, board, getCurrentPlayer());
			if (!valid) {
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

	public Board getBoardCopy() {
		return board.deepCopy();
	}
}
