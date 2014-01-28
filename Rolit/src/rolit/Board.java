package rolit;

/**
 * Bord voor Rolit
 * 
 * @author Martijn & Camilio
 * @author Theo Ruys en Arend Rensink
 */
public class Board {

	public static final int DIM = 8;
	private static final String[] NUMBERING = {
			" 00 | 01 | 02 | 03 | 04 | 05 | 06 | 07 ",
			"----+----+----+----+----+----+----+----",
			" 08 | 09 | 10 | 11 | 12 | 13 | 14 | 15 ",
			"----+----+----+----+----+----+----+----",
			" 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 ",
			"----+----+----+----+----+----+----+----",
			" 24 | 25 | 26 | 27 | 28 | 29 | 30 | 31 ",
			"----+----+----+----+----+----+----+----",
			" 32 | 33 | 34 | 35 | 36 | 37 | 38 | 39 ",
			"----+----+----+----+----+----+----+----",
			" 40 | 41 | 42 | 43 | 44 | 45 | 46 | 47 ",
			"----+----+----+----+----+----+----+----",
			" 48 | 49 | 50 | 51 | 52 | 53 | 54 | 55 ",
			"----+----+----+----+----+----+----+----",
			" 56 | 57 | 58 | 59 | 60 | 61 | 62 | 63 ",
			"----+----+----+----+----+----+----+----" };
	private static final String LINE = "--------+--------+--------+--------+--------+--------+--------+--------";
	private static final String DELIM = "      ";
	private Ball[] fields;

	/**
	 * Maakt een nieuw bord. Vult de fieldsarray en zet de standaard waarden.
	 */
	// @ ensures (\forall int i; 0 <= i & i < DIM * DIM; this.getField(i)
	// ==Ball.EMPTY);
	public Board() {
		fields = new Ball[DIM * DIM];
		reset();
	}

	/**
	 * Maakt een kopie van het bord zodat je kan klooien wat je wil, maar het
	 * echte bord niet wordt aangepast.
	 */
	// @ensures \result != this; ensures (\forall int i; 0 <= i & i < DIM * DIM;
	// \result.getField(i) == this.getField(i));
	public Board deepCopy() {
		Board copy = new Board();
		for (int i = 0; i < fields.length; i++) {
			copy.fields[i] = this.fields[i];
		}
		return copy;
	}

	/**
	 * Zoekt de index van een rij en kolom.
	 * 
	 * @return de index
	 */
	// @ requires 0 <= row & row < DIM; requires 0 <= col & col < DIM;
	public int index(int row, int col) {
		// Rijen die buiten het bord vallen moeten worden genegeerd, wat
		// betekent -1 teruggeven
		if (row >= DIM || col >= DIM) {
			return -1;
		}
		return DIM * row + col;
	}

	/**
	 * Geeft de rij terug van de index waar het bij hoort.
	 * 
	 * @param index
	 *            de index waarvan je de rij wilt weten
	 * @return de rij.
	 */
	// @requires isField(index);
	// @ensures \result <=DIM;
	public int getRow(int index) {
		return index / DIM;
	}

	/**
	 * Geeft de kolom terug van de index waar het bij hoort.
	 * 
	 * @param index
	 *            de index waarvan je de kolom wilt weten
	 * @return de kolom.
	 */
	// @requires isField(index);
	// @ensures \result <=DIM;
	public int getCol(int index) {
		return (index % DIM);
	}

	/**
	 * Geeft true als ix een index is op het veld.
	 * 
	 * @return true bij een echte index, false bij een index buiten het veld.
	 */
	// @ ensures \result == (0 <= ix && ix < DIM * DIM);
	// @pure;
	public boolean isField(int ix) {
		return (0 <= ix) && (ix < DIM * DIM);
	}

	/**
	 * Geeft true als row,col een index is op het veld.
	 * 
	 * @return true bij een echte index, false bij een index buiten het veld.
	 */
	// @ensures \result == (0 <= row && row < DIM && 0 <= col && col < DIM);
	// @pure;
	public boolean isField(int row, int col) {
		return (0 <= row) && (row < DIM) && (0 <= col) && (col < DIM);
	}

	/**
	 * Geeft de inhoud van veld i.
	 * 
	 * @param i
	 *            De index van het veld.
	 * @return De Ball op het veld.
	 */
	// @ requires this.isField(i);
	public Ball getField(int i) {
		return fields[i];
	}

	/**
	 * geeft de inhoud van het veld op row, col.
	 * 
	 * @param row
	 *            De rij
	 * @param col
	 *            De kolom
	 * @return De ball op het veld.
	 */
	// @ requires this.isField(row,col);
	public Ball getField(int row, int col) {
		return fields[index(row, col)];
	}

	/**
	 * Geeft true terug als het veld i leeg is.
	 * 
	 * @param i
	 *            De index
	 * @return true als het een leeg veld is.
	 */
	// @requires this.isField(i);
	// @ensures \result == (this.getField(i) == Ball.EMPTY);
	public boolean isEmptyField(int i) {
		return getField(i) == Ball.EMPTY;
	}

	/**
	 * Geeft true terug als het veld row, col leeg is.
	 * 
	 * @param row
	 *            De rij
	 * @param col
	 *            De kolom
	 * @return true als het een leeg veld is.
	 */
	// @requires this.isField(row,col);
	// @ensures \result == (this.getField(row,col) == Ball.EMPTY);
	/* @pure */
	public boolean isEmptyField(int row, int col) {
		return isEmptyField(index(row, col));
	}

	/**
	 * Controleert of het hele bord vol is.
	 * 
	 * @return true als alle velden niet leeg zijn.
	 */
	// @ ensures \result == (\forall int i; i <= 0 & i < DIM * DIM;
	// this.getField(i) != Ball.EMPTY);
	// @pure;
	public boolean isFull() {
		for (int i = 0; i < fields.length; i++) {
			// Kijkt of elk veld leeg is, als er een veld leeg is, dan is het
			// bord dus niet vol.
			if (isEmptyField(i)) {
				return false;
			}
		}
		// Er is geen leeg veld gevonden. Dus alle velden zijn vol.
		return true;
	}

	/**
	 * Geeft true terug als het bord vol is.
	 * 
	 * @return true als het spel afgelopen is.
	 */
	// @ ensures \result == this.isFull();
	// @pure;
	public boolean gameOver() {
		return isFull();
	}

	/**
	 * Kijkt of de speler met ball m heeft gewonnen.
	 * 
	 * @param m
	 *            De ball
	 * @return true als de speler met die ball heeft gewonnen.
	 */
	// @pure;
	public boolean isWinner(Ball m) {
		return isWinner(m, false);
	}

	public boolean isWinner(Ball m, boolean vroegtijdigEinde) {
		if (isFull() || vroegtijdigEinde) {
			// Tel eerst hoeveel ballen iedere speler heeft.
			int balls_yellow = countBalls(Ball.YELLOW);
			int balls_blue = countBalls(Ball.BLUE);
			int balls_green = countBalls(Ball.GREEN);
			int balls_red = countBalls(Ball.RED);

			int balls_m = countBalls(m);

			// Kijk nu wie de meeste ballen heeft
			int balls_max = Math.max(Math.max(balls_green, balls_red),
					Math.max(balls_blue, balls_yellow));
			return (balls_m == balls_max || !isDraw(balls_yellow, balls_blue,
					balls_green, balls_red, balls_max));
		}
		return false;
	}

	/**
	 * Kijkt of er gelijk spel is.
	 * 
	 * @param balls_yellow
	 *            aantal gele ballen op het veld
	 * @param balls_blue
	 *            aantal blauwe ballen op het veld
	 * @param balls_green
	 *            aantal groen ballen op het veld
	 * @param balls_red
	 *            aantal rode ballen op het veld
	 * @param balls_max
	 *            het maximum van de bovenstaande aantallen.
	 * @return true als de twee hoogste aantallen gelijk zijn.
	 */
	private boolean isDraw(int balls_yellow, int balls_blue, int balls_green,
			int balls_red, int balls_max) {
		// Zet de aantallen in een array.
		int[] arr = new int[4];
		arr[0] = balls_yellow;
		arr[1] = balls_blue;
		arr[2] = balls_green;
		arr[3] = balls_red;
		// Kijk of twee van deze vier gelijk is aan elkaar
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				// Als twee gelijk zijn en gelijk zijn aan het maximum en niet
				// dezelfde zijn (dus niet allebei geel) dan is er dus een
				// gelijk spel.
				if (arr[i] == arr[j] && arr[i] == balls_max && i != j) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Telt het aantal ballen op het veld van een bepaalde ball kleur.
	 * 
	 * @param m
	 *            de ball die we tellen.
	 * @return het aantal ballen op het veld.
	 */
	// @requires m.equals(Ball.RED) || m.equals(Ball.GREEN) ||
	// m.equals(Ball.YELLOW) || m.equals(Ball.BLUE);
	// @ensures \result >=0 && \result <=64;
	public int countBalls(Ball m) {
		// Ga alle velden langs en kijk of het een bal van m is
		int counter = 0;
		for (int i = 0; i < (fields.length); i++) {
			if (getField(i).equals(m)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Kijkt of er een winnaar is.
	 * 
	 * @return als een van de vier ballen de winnaar is, dan is er dus een
	 *         winnaar.
	 */
	// @pure;
	public boolean hasWinner() {
		return isWinner(Ball.BLUE) || isWinner(Ball.GREEN)
				|| isWinner(Ball.RED) || isWinner(Ball.YELLOW);
	}

	/**
	 * Geeft de stringrepresentatie van het bord.
	 * 
	 * @return het bord als string.
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

	/**
	 * Geeft een mooi uitgelijnde string terug
	 * 
	 * @param str
	 *            de string om uit te lijnen
	 * @return de uitgelijnde string.
	 */
	public static String format(String str) {
		return String.format("%1$6s", str);
	}

	/**
	 * Zet het bord naar de startsituatie.
	 */
	public void reset() {
		// Maakt alle velden leeg.
		for (int i = 0; i < fields.length; i++) {
			setField(i, Ball.EMPTY);
		}
		// Legt de eerste 4 ballen vast neer.
		setField(27, Ball.RED);
		setField(28, Ball.YELLOW);
		setField(35, Ball.BLUE);
		setField(36, Ball.GREEN);
	}

	/**
	 * Zet de ball m op het veld i
	 * 
	 * @param i
	 *            de index
	 * @param m
	 *            de Ball die moet worden neergelegd.
	 */
	// @ requires this.isField(i); ensures this.getField(i) == m;
	public void setField(int i, Ball m) {
		fields[i] = m;
	}

	/**
	 * Zet de ball m op het veld row, col
	 * 
	 * @param row
	 *            de rij
	 * @param col
	 *            de kolom.
	 * @param m
	 *            de Ball die moet worden neergelegd.
	 */
	// @ requires this.isField(index(row,col)); ensures this.getField(index(row,col)) == m;
	public void setField(int row, int col, Ball m) {
		setField(index(row, col), m);
	}
}