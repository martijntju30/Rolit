package rolit;

import java.util.Scanner;

/**
 * Een spelerobject
 * 
 * @author Martijn & Camilio
 * @author Theo Ruys en Arend Rensink
 */
public class Player {

	private String name;
	private Ball Ball;

	/**
	 * Maakt een nieuwe speler.
	 * 
	 */
	/*
	 * @ requires theName != null; requires theBall !=null; ensures
	 * this.getName() == theName; ensures this.getBall() == theBall;
	 */
	public Player(String theName, Ball theBall) {
		this.name = theName;
		this.Ball = theBall;
	}

	/**
	 * Geeft de naam van de speler.
	 */
	// @pure;
	public String getName() {
		return name;
	}

	/**
	 * Geeft de ball van de speler.
	 */
	// @pure;
	public Ball getBall() {
		return Ball;
	}

	/*
	 * @ requires board != null; ensures board.isField(\result) &&
	 * board.isEmptyField(\result);
	 */
	/**
	 * Asks the user to input the field where to place the next mark. This is
	 * done using the standard input/output. \
	 * 
	 * @param board
	 *            the game board
	 * @return the player's chosen field
	 */
	public int determineMove(Board board) {
		String prompt = "> " + getName() + " (" + getBall().toString() + ")"
				+ ", what is your choice? ";
		int choice = readInt(prompt);
		System.out.println("Dit is de huidige keuze: " + choice);
		boolean valid = board.isField(choice) && board.isEmptyField(choice);
		while (!valid) {
			System.out.println("ERROR: field " + choice
					+ " is no valid choice.");
			choice = readInt(prompt);
			valid = board.isField(choice) && board.isEmptyField(choice);
		}
		return choice;
	}

	/**
	 * Writes a prompt to standard out and tries to read an int value from
	 * standard in. This is repeated until an int value is entered.
	 * 
	 * @param prompt
	 *            the question to prompt the user
	 * @return the first int value which is entered by the user
	 */
	@SuppressWarnings("resource")
	private int readInt(String prompt) {
		int value = 0;
		boolean intRead = false;
		do {
			System.out.print(prompt);
			String line = (new Scanner(System.in)).nextLine();
			Scanner scannerLine = new Scanner(line);
			if (scannerLine.hasNextInt()) {
				intRead = true;
				value = scannerLine.nextInt();
			}
		} while (!intRead);

		return value;
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Makes a move on the board. <br>
	 * 
	 * @param bord
	 *            the current board
	 */
	// @ requires board != null & !board.isFull();
	public void makeMove(Board board, Game game) {
		if (game.getCurrentPlayer() == this) {
			int keuze = determineMove(board);
			game.takeTurn(keuze);
		}
	}

	/**
	 * Als een speler wordt geprint, dan geeft dit de naam van de speler.
	 */
	public String toString() {
		return getName();
	}
}
