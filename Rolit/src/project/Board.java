package project;

/**
 * Game board for the Tic Tac Toe game. Module 2 lab assignment.
 * 
 * @author Theo Ruys en Arend Rensink
 * @version $Revision: 1.4 $
 */
public class Board {

	// -- Constants --------------------------------------------------

	public static final int DIM = 8;
	private static final String[] NUMBERING = { 
			" 00 | 01 | 02 | 03 | 04 | 05 | 06 | 07 ", "----+----+----+----+----+----+----+----", 
			" 08 | 09 | 10 | 11 | 12 | 13 | 14 | 15 ", "----+----+----+----+----+----+----+----", 
			" 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 ", "----+----+----+----+----+----+----+----",
			" 24 | 25 | 26 | 27 | 28 | 29 | 30 | 31 ", "----+----+----+----+----+----+----+----",
			" 32 | 33 | 34 | 35 | 36 | 37 | 38 | 39 ", "----+----+----+----+----+----+----+----",
			" 40 | 41 | 42 | 43 | 44 | 45 | 46 | 47 ", "----+----+----+----+----+----+----+----",
			" 48 | 49 | 50 | 51 | 52 | 53 | 54 | 55 ", "----+----+----+----+----+----+----+----",
			" 56 | 57 | 58 | 59 | 60 | 61 | 62 | 63 ", "----+----+----+----+----+----+----+----" };
	private static final String LINE = "--------+--------+--------+--------+--------+--------+--------+--------";
	private static final String DELIM = "      ";

	// -- Instance variables -----------------------------------------

	/*
	 * @ private invariant fields.length == DIM*DIM; invariant (\forall int i; 0
	 * <= i & i < DIM*DIM; getField(i) == Ball.EMPTY || getField(i) == Ball.XX
	 * || getField(i) == Ball.OO);
	 */
	/**
	 * The DIM by DIM fields of the Tic Tac Toe board. See NUMBERING for the
	 * coding of the fields.
	 */
	private Ball[] fields;

	// -- Constructors -----------------------------------------------

	/*
	 * @ ensures (\forall int i; 0 <= i & i < DIM * DIM; this.getField(i) ==
	 * Ball.EMPTY);
	 */
	/**
	 * Creates an empty board.
	 */
	// B{Bord-impl}
	public Board() {
		// E{Bord}
		fields = new Ball[DIM * DIM];
		reset();
		// B{Bord}
		// I{Bord} // [BODY-NOG-TOE-TE-VOEGEN]
	}

	// E{Bord-impl}
	// -- Queries ----------------------------------------------------

	/*
	 * @ ensures \result != this; ensures (\forall int i; 0 <= i & i < DIM *
	 * DIM; \result.getField(i) == this.getField(i));
	 */
	/**
	 * Creates a deep copy of this field.
	 */
	// B{Bord-impl}
	public Board deepCopy() {
		// E{Bord}
		Board copy = new Board();
		for (int i = 0; i < fields.length; i++) {
			copy.fields[i] = this.fields[i];
		}
		return copy;
		// B{Bord}
		// I{Bord} // [BODY-NOG-TOE-TE-VOEGEN]
		// I{Bord} return null;
	}

	// E{Bord-impl}

	/*
	 * @ requires 0 <= row & row < DIM; requires 0 <= col & col < DIM;
	 */
	/**
	 * Calculates the index in the linear array of fields from a (row, col)
	 * pair.
	 * 
	 * @return the index belonging to the (row,col)-field
	 */
	// B{Bord-impl}
	public int index(int row, int col) {
		// E{Bord}
		//Rijen die buiten het bord vallen moeten worden genegeerd, wat betekent -1 teruggeven
		if (row >=DIM || col >= DIM){
			return -1;
		}
		return DIM * row + col;
		// B{Bord}
		// I{Bord} // [BODY-NOG-TOE-TE-VOEGEN]
		// I{Bord} return 0;
	}
	
	public int getRow(int index){
		return index/DIM;
		
	}
	
	public int getCol(int index){
		return (index%DIM);
		
	}

	// E{Bord-impl}

	/*
	 * @ ensures \result == (0 <= ix && ix < DIM * DIM);
	 */
	/**
	 * Returns true if <code>ix</code> is a valid index of a field on tbe board.
	 * 
	 * @return <code>true</code> if <code>0 <= ix < DIM*DIM</code>
	 */
	// B{Bord-impl}
	/* @pure */
	public boolean isField(int ix) {
		// E{Bord}
		return (0 <= ix) && (ix < DIM * DIM);
		// B{Bord}
		// I{Bord} // [BODY-NOG-TOE-TE-VOEGEN]
		// I{Bord} return false;
	}

	// E{Bord-impl}
	/*
	 * @ ensures \result == (0 <= row && row < DIM && 0 <= col && col < DIM);
	 */
	/**
	 * Returns true of the (row,col) pair refers to a valid field on the board.
	 * 
	 * @return true if <code>0 <= row < DIM && 0 <= col < DIM</code>
	 */
	// B{Bord-impl}
	/* @pure */
	public boolean isField(int row, int col) {
		// E{Bord}
		return (0 <= row) && (row < DIM) && (0 <= col) && (col < DIM);
		// B{Bord}
		// I{Bord} // [BODY-NOG-TOE-TE-VOEGEN]
		// I{Bord} return false;
	}

	// E{Bord-impl}

	/*
	 * @ requires this.isField(i); ensures \result == Ball.EMPTY || \result ==
	 * Ball.XX || \result == Ball.OO;
	 */
	/**
	 * Returns the content of the field <code>i</code>.
	 * 
	 * @param i
	 *            the number of the field (see NUMBERING)
	 * @return the Ball on the field
	 */
	// B{Bord-impl}
	public Ball getField(int i) {
		// E{Bord}
		return fields[i];
		// B{Bord}
		// I{Bord} // [BODY-NOG-TOE-TE-VOEGEN]
		// I{Bord} return null;
	}

	// E{Bord-impl}
	/*
	 * @ requires this.isField(row,col); ensures \result == Ball.EMPTY ||
	 * \result == Ball.XX || \result == Ball.OO;
	 */
	/**
	 * Returns the content of the field referred to by the (row,col) pair.
	 * 
	 * @param row
	 *            the row of the field
	 * @param col
	 *            the column of the field
	 * @return the Ball on the field
	 */
	public Ball getField(int row, int col) {
		return fields[index(row, col)];
	}

	/*
	 * @ requires this.isField(i); ensures \result == (this.getField(i) ==
	 * Ball.EMPTY);
	 */
	/**
	 * Returns true if the field <code>i</code> is empty.
	 * 
	 * @param i
	 *            the index of the field (see NUMBERING)
	 * @return true if the field is empty
	 */
	public boolean isEmptyField(int i) {
		return getField(i) == Ball.EMPTY;
	}

	/*
	 * @ requires this.isField(row,col); ensures \result ==
	 * (this.getField(row,col) == Ball.EMPTY);
	 */
	/**
	 * Returns true if the field referred to by the (row,col) pair it empty.
	 * 
	 * @param row
	 *            the row of the field
	 * @param col
	 *            the column of the field
	 * @return true if the field is empty
	 */
	/* @pure */
	public boolean isEmptyField(int row, int col) {
		return isEmptyField(index(row, col));
	}

	/*
	 * @ ensures \result == (\forall int i; i <= 0 & i < DIM * DIM;
	 * this.getField(i) != Ball.EMPTY);
	 */
	/**
	 * Tests if the whole board is full.
	 * 
	 * @return true if all fields are occupied
	 */
	/* @pure */
	public boolean isFull() {
		// E{Bord}
		boolean result = true;
		for (int i = 0; i < fields.length; i++) {
			if (isEmptyField(i)) {
				result = false;
			}
		}
		return result;
	}

	/*
	 * @ ensures \result == this.isFull() || this.hasWinner();
	 */
	/**
	 * Returns true if the game is over. The game is over when there is a winner
	 * or the whole board is full.
	 * 
	 * @return true if the game is over
	 */
	/* @pure */
	public boolean gameOver() {
		return isFull();
	}

	/**
	 * Checks if the Ball <code>m</code> has won. A Ball wins if it controls at
	 * least one row, column or diagonal.
	 * 
	 * @param m
	 *            the Ball of interest
	 * @return true if the Ball has won
	 */
	/* @pure */
	public boolean isWinner(Ball m) {
		if (isFull()) {
			// Tel eerst hoeveel ballen iedere speler heeft.
			int balls_yellow = countBalls(Ball.YELLOW);
			int balls_blue = countBalls(Ball.BLUE);
			int balls_green = countBalls(Ball.GREEN);
			int balls_red = countBalls(Ball.RED);

			int balls_m = countBalls(m);

			// Kijk nu wie de meeste ballen heeft
			int balls_max = Math.max(Math.max(balls_green, balls_red),
					Math.max(balls_blue, balls_yellow));
			return (balls_max == balls_m) && (!((balls_yellow == balls_blue) == (balls_green == balls_red)));
		}
		return false;
	}

	private int countBalls(Ball m) {
		// Ga alle velden langs en kijk of het een bal van m is
		int counter = 0;
		for (int i = 0; i < (fields.length); i++) {
			if (getField(i).equals(m)) {
				counter++;
			}
		}
		return counter;
	}
	
	/*
	 * @ ensures \result == isWinner(Ball.XX) | \result == isWinner(Ball.OO);
	 */
	/**
	 * Returns true if the game has a winner. This is the case when one of the
	 * Balls controls at least one row, column or diagonal.
	 * 
	 * @return true if the board has a winner.
	 */
	/* @pure */
	public boolean hasWinner() {
		// E{Bord}
		return isWinner(Ball.BLUE) || isWinner(Ball.GREEN)
				|| isWinner(Ball.RED) || isWinner(Ball.YELLOW);
	}

	/**
	 * Returns a String representation of this board. In addition to the current
	 * situation, the String also shows the numbering of the fields.
	 * 
	 * @return the game situation as String
	 */
	public String toString() {
		String s = "";
		for (int i = 0; i < DIM; i++) {
			String row = "";
			for (int j = 0; j < DIM; j++) {
				row = row + " " + format(getField(i, j).toString()) + " ";
				if (j < DIM - 1) {
					row = row + "|";
				}
			}
			s = s + row + DELIM + NUMBERING[i * 2];
			if (i < DIM - 1) {
				s = s + "\n" + LINE + DELIM + NUMBERING[i * 2 + 1] + "\n";
			}
		}
		return s;
	}
	public static String format(String str){
		return String.format("%1$6s", str);
	}

	// -- Commands ---------------------------------------------------

	/*
	 * @ ensures (\forall int i; 0 <= i & i < DIM * DIM; this.getField(i) ==
	 * Ball.EMPTY);
	 */
	/**
	 * Empties all fields of this board (i.e., let them refer to the value
	 * Ball.EMPTY).
	 */
	public void reset() {
		for (int i = 0; i < fields.length; i++) {
			setField(i, Ball.EMPTY);
		}
		setField(27, Ball.BLUE);
		setField(28, Ball.YELLOW);
		setField(35, Ball.GREEN);
		setField(36, Ball.RED);
	}

	/*
	 * @ requires this.isField(i); ensures this.getField(i) == m;
	 */
	/**
	 * Sets the content of field <code>i</code> to the Ball <code>m</code>.
	 * 
	 * @param i
	 *            the field number (see NUMBERING)
	 * @param m
	 *            the Ball to be placed
	 */
	public void setField(int i, Ball m) {
		fields[i] = m;
	}

	/*
	 * @ requires this.isField(row,col); ensures this.getField(row,col) == m;
	 */
	/**
	 * Sets the content of the field represented by the (row,col) pair to the
	 * Ball <code>m</code>.
	 * 
	 * @param row
	 *            the field's row
	 * @param col
	 *            the field's column
	 * @param m
	 *            the Ball to be placed
	 */
	public void setField(int row, int col, Ball m) {
		setField(index(row, col), m);
	}
	
	public static void main (String[] args){
		Board bord = new Board();
		bord.reset();
		System.out.println(bord);
	}
}