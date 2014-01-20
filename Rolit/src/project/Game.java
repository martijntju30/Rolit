package project;

import java.awt.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Scanner;
import java.util.Set;

/**
 * Class for maintaining the Tic Tac Toe game. Lab assignment Module 2
 * 
 * @author Theo Ruys en Arend Rensink
 * @version $Revision: 1.4 $
 */
public class Game extends Observable {

	// -- Instance variables -----------------------------------------

	public static int NUMBER_PLAYERS;

	/*
	 * @ private invariant board != null;
	 */
	/**
	 * The board.
	 */
	private Board board;
	private Rolit_view view;

	/*
	 * @ private invariant players.length == NUMBER_PLAYERS; private (\forall
	 * int i; 0 <= i && i < NUMBER_PLAYERS; players[i] != null);
	 */
	/**
	 * The 2 players of the game.
	 */
	private Player[] players;
	private int[] score;

	private Leaderboard leaderboard = new Leaderboard();

	/*
	 * @ private invariant 0 <= current && current < NUMBER_PLAYERS;
	 */
	/**
	 * Index of the current player.
	 */
	private int current;

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
	public Game(int numberofplayers, Player s0, Player s1, Player s2, Player s3) {
		board = new Board();
		NUMBER_PLAYERS = numberofplayers;
		players = new Player[NUMBER_PLAYERS];
		score = new int[NUMBER_PLAYERS];
		players[0] = s0;
		players[1] = s1;
		players[2] = s2;
		players[3] = s3;
		current = 0;
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

		leaderboard.add(players[0].getName(), score[0]);
		leaderboard.add(players[1].getName(), score[1]);
		leaderboard.add(players[2].getName(), score[2]);
		leaderboard.add(players[3].getName(), score[3]);
		leaderboard.showBoard();
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
		view = new Rolit_view(this);
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
	}

	// E{Spel.play}

	/**
	 * Prints the game situation.
	 */
	private void update() {
		// view.showBoard();
		System.out.println("\ncurrent game situation: \n\n" + board.toString()
				+ "\n");
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
			System.out.println("Speler " + players[playernum].getName() + " ("
					+ players[playernum].getBall().toString() + ") has won!");
			view.label.setText("Speler " + players[playernum].getName() + " ("
					+ players[playernum].getBall().toString() + ") has won!");
			view.repaint();
			score[playernum]++;

		} else {
			System.out.println("Draw. There is no winner!");
			view.label.setText("Draw. There is no winner!");
			view.repaint();
		}
	}

	protected Board getBoard() {
		return board;
	}

	protected Player getCurrentPlayer() {
		return players[current];
	}

	private void applyRules(int zet) {
		Set<Integer> toChange = Validatie.getPossibleTakeOvers(zet, board, getCurrentPlayer());

		// Wijzig nu alle vakjes die in toChange staan
		for (Integer i: toChange){
			board.setField(i, getCurrentPlayer().getBall());
		}
	}

	public void takeTurn(int i) {
		int choice = i;
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
		} else {
			board.setField(choice, getCurrentPlayer().getBall());
			applyRules(i);
			nextPlayer();
			update();
			if (board.gameOver()) {
				printResult();
			}
		}
	}

	

	/*
	 * private Set<Integer> getPossibleTakeOvers(int zet) { /** CAMILIO, KUN JIJ
	 * DIT TESTEN? Ik heb dit al wel gedaan maar misschien heb jij hier ook nog
	 * ideeën over -/ Set<Integer> toChange = new HashSet<Integer>(); Ball
	 * playermark = getCurrentPlayer().getBall(); // Check voor vakje linksboven
	 * int lbr = board.getRow(zet) - 1; // LinksBovenRow int lbc =
	 * board.getCol(zet) - 1; // LinksBovenCol while (board.isField(lbr, lbc) &&
	 * !board.isEmptyField(lbr, lbc)) { if (board.getField(lbr,
	 * lbc).equals(playermark)) { toChange.add(board.index(lbr, lbc)); break; }
	 * lbr--; lbc--; } // Check voor vakje boven int br = board.getRow(zet) - 1;
	 * // BovenRow int bc = board.getCol(zet); // BovenCol while
	 * (board.isField(br, bc) && !board.isEmptyField(br, bc)) { if
	 * (board.getField(br, bc).equals(playermark)) {
	 * toChange.add(board.index(br, bc)); break; } br--; }
	 * 
	 * // Check voor vakje rechtsboven
	 * 
	 * int rbr = board.getRow(zet) - 1; // RechtsBovenRow int rbc =
	 * board.getCol(zet) + 1; // LinksBovenCol while (board.isField(rbr, rbc) &&
	 * !board.isEmptyField(rbr, rbc)) { if (board.getField(rbr,
	 * rbc).equals(playermark)) { toChange.add(board.index(rbr, rbc)); break; }
	 * rbr--; rbc++; } // Check voor vakje links
	 * 
	 * int lr = board.getRow(zet); // LinksRow int lc = board.getCol(zet) - 1;
	 * // LinksCol while (board.isField(lr, lc) && !board.isEmptyField(lr, lc))
	 * { if (board.getField(lr, lc).equals(playermark)) {
	 * toChange.add(board.index(lr, lc)); break; } lc--; } // Check voor vakje
	 * rechts
	 * 
	 * int rr = board.getRow(zet); // RechtsRow int rc = board.getCol(zet) + 1;
	 * // RechtsCol while (board.isField(rr, rc) && !board.isEmptyField(rr, rc))
	 * { if (board.getField(rr, rc).equals(playermark)) {
	 * toChange.add(board.index(rr, rc)); break; } rc++; } // Check voor vakje
	 * linksonder
	 * 
	 * int lor = board.getRow(zet) + 1; // LinksOnderRow int loc =
	 * board.getCol(zet) - 1; // LinksOnderCol while (board.isField(lor, loc) &&
	 * !board.isEmptyField(lor, loc)) { if (board.getField(lor,
	 * loc).equals(playermark)) { toChange.add(board.index(lor, loc)); break; }
	 * lor++; loc--; } // Check voor vakje onder
	 * 
	 * int or = board.getRow(zet) + 1; // OnderRow int oc = board.getCol(zet);
	 * // OnderCol while (board.isField(or, oc) && !board.isEmptyField(or, oc))
	 * { if (board.getField(or, oc).equals(playermark)) {
	 * toChange.add(board.index(or, oc)); break; } or++; } // Check voor vakje
	 * rechtsonder
	 * 
	 * int ror = board.getRow(zet) + 1; // RechtsOnderRow int roc =
	 * board.getCol(zet) + 1; // RechtsOnderCol while (board.isField(ror, roc)
	 * && !board.isEmptyField(ror, roc)) { if (board.getField(ror,
	 * roc).equals(playermark)) { toChange.add(board.index(ror, roc)); break; }
	 * ror++; roc++; } // Geef nu alle indexen terug return toChange; }
	 */

	public Board getBoardCopy() {
		return board.deepCopy();
	}

	// public static void main (String[] args){
	// Game g = new Game(new Player("A", Ball.BLUE), new Player("B", Ball.BLUE),
	// new Player("C", Ball.BLUE), new Player("D", Ball.BLUE));
	// for (int i = 0; i<64; i++){
	// g.board.setField(i, Ball.BLUE);
	// }
	// Set<Integer> takeOvers = g.getPossibleTakeOvers(44);
	// System.out.println(g.board.toString());
	// System.out.println(Arrays.toString(takeOvers.toArray()));
	// }

	/*
	 * private void applyRules(int zet) { //Als er een zet wordt gedaan dan moet
	 * gecontroleerd worden of er op een van de omliggende vakjes een eigen
	 * kleur ligt. //Haal eerst de indexen van de vakjes op. System.out.println(
	 * "__________________________________________________________________");
	 * System.out.println("Apply rules:"); int[] indexes = new int [8];
	 * indexes[0] = board.index(board.getRow(zet)-2, board.getCol(zet)-2);
	 * indexes[1] = board.index(board.getRow(zet)-2, board.getCol(zet));
	 * indexes[2] = board.index(board.getRow(zet)-2, board.getCol(zet)+2);
	 * indexes[3] = board.index(board.getRow(zet), board.getCol(zet)-2);
	 * indexes[4] = board.index(board.getRow(zet), board.getCol(zet)+2);
	 * indexes[5] = board.index(board.getRow(zet)+2, board.getCol(zet)-2);
	 * indexes[6] = board.index(board.getRow(zet)+2, board.getCol(zet));
	 * indexes[7] = board.index(board.getRow(zet)+2, board.getCol(zet)+2);
	 * 
	 * System.out.println(Arrays.toString(indexes)); //Kijk nu welke vakjes ook
	 * echt velden zijn die gevuld zijn met eigen kleur. LinkedList<Integer>
	 * velden = new LinkedList<Integer>(); for (int i =0; i<indexes.length;
	 * i++){ if (board.isField(indexes[i]) &&
	 * board.getField(indexes[i]).equals(getCurrentPlayer().getBall())) { switch
	 * (i){ case 0: if (board.isField( board.getRow(zet)-1, board.getCol(zet)-1)
	 * && !board.isEmptyField( board.getRow(zet)-1, board.getCol(zet)-1)) {
	 * velden.add(board.index(board.getRow(zet)-1, board.getCol(zet)-1)); }
	 * break; case 1: if (board.isField( board.getRow(zet)-1, board.getCol(zet))
	 * && !board.isEmptyField( board.getRow(zet)-1, board.getCol(zet))) {
	 * velden.add(board.index(board.getRow(zet)-1, board.getCol(zet))); } break;
	 * case 2: if (board.isField( board.getRow(zet)-1, board.getCol(zet)+1) &&
	 * !board.isEmptyField( board.getRow(zet)-1, board.getCol(zet)+1)) {
	 * velden.add(board.index(board.getRow(zet)-1, board.getCol(zet)+1)); }
	 * break; case 3: if (board.isField( board.getRow(zet), board.getCol(zet)-1)
	 * && !board.isEmptyField( board.getRow(zet), board.getCol(zet)-1)) {
	 * velden.add(board.index(board.getRow(zet), board.getCol(zet)-1)); } break;
	 * case 4: if (board.isField( board.getRow(zet), board.getCol(zet)+1) &&
	 * !board.isEmptyField( board.getRow(zet), board.getCol(zet)+1)) {
	 * velden.add(board.index(board.getRow(zet), board.getCol(zet)+1)); } break;
	 * case 5: if (board.isField( board.getRow(zet)+1, board.getCol(zet)-1) &&
	 * !board.isEmptyField( board.getRow(zet)+1, board.getCol(zet)-1)) {
	 * velden.add(board.index(board.getRow(zet)+1, board.getCol(zet)-1)); }
	 * break; case 6: if (board.isField( board.getRow(zet)+1, board.getCol(zet))
	 * && !board.isEmptyField( board.getRow(zet)+1, board.getCol(zet))) {
	 * velden.add(board.index(board.getRow(zet)+1, board.getCol(zet))); } break;
	 * case 7: if (board.isField( board.getRow(zet)+1, board.getCol(zet)+1) &&
	 * !board.isEmptyField( board.getRow(zet)+1, board.getCol(zet)+1)) {
	 * velden.add(board.index(board.getRow(zet)+1, board.getCol(zet)+1)); }
	 * break; } } } System.out.println(Arrays.toString(velden.toArray())); //Nu
	 * hebben we een lijst met velden die we moeten veranderen naar de kleur van
	 * de huidige speler for (int i = 0; i<velden.size(); i++){
	 * board.setField(velden.get(i), getCurrentPlayer().getBall()); }
	 * System.out.
	 * println("__________________________________________________________________"
	 * ); }
	 */
}
