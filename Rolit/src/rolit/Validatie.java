package rolit;

import java.util.HashSet;
import java.util.Set;

public class Validatie {

	/**
	 * Kijkt of een zet valide is. Een zet is valide als hij: - grenst aan een
	 * andere bal - op een vrij veld wordt gezet - ballen overneemt als dat kan
	 * 
	 * @param zet
	 *            De zet die gecontroleerd moet worden
	 * @param board
	 *            Het bord waarop de zet gecontroleerd moet worden
	 * @param player
	 *            De speler die de zet zou willen doen.
	 * @return true als de zet valide is.
	 */
	// @requires board != null && player!=null && board.isField(zet) &&
	// board.isEmptyField(zet);
	public static boolean validMove(int zet, Board board, Player player) {
		boolean valid = false;
		// Een move is pas valid als hij grenst aan een andere bal
		int[] indexes = new int[8];
		indexes[0] = board.index(board.getRow(zet) - 1, board.getCol(zet) - 1);
		indexes[1] = board.index(board.getRow(zet) - 1, board.getCol(zet));
		indexes[2] = board.index(board.getRow(zet) - 1, board.getCol(zet) + 1);
		indexes[3] = board.index(board.getRow(zet), board.getCol(zet) - 1);
		indexes[4] = board.index(board.getRow(zet), board.getCol(zet) + 1);
		indexes[5] = board.index(board.getRow(zet) + 1, board.getCol(zet) - 1);
		indexes[6] = board.index(board.getRow(zet) + 1, board.getCol(zet));
		indexes[7] = board.index(board.getRow(zet) + 1, board.getCol(zet) + 1);
		for (int i = 0; i < indexes.length; i++) {
			if (board.isField(indexes[i]) && !board.isEmptyField(indexes[i])) {
				valid = true;
			}
		}
		boolean allowed = false;
		// Een move is pas valid als hij overneemt als dit kan.
		// Haal alle vrije vakjes op.
		Set<Integer> freeIndexes = getFreeIndexes(board);
		// Kijk nu bij elke van deze vrije vakjes of deze ervoor zorgen dat ze
		// een andere blocken. Als dat zo is dat weet je dat er een move is om
		// anderen te blocken dus moet er geblockt worden
		boolean blockable = false;
		for (Integer i : freeIndexes) {
			Board boardCopy = board.deepCopy();
			boardCopy.setField(i, player.getBall());
			if (getPossibleTakeOvers(i, board, player).size() > 0) {
				blockable = true;
			}
		}
		// Kijk nu of de move iets blockt. Als beide ja is, dan is de move
		// valid.
		allowed = (getPossibleTakeOvers(zet, board, player).size() > 0 == blockable);
		return valid && allowed;

	}

	/**
	 * Haalt alle vrije indexen op van het bord b.
	 * 
	 * @param b
	 *            het bord waarvan de vrije indexen moeten worden bepaald.
	 * @return een set met vrije indexen/
	 */
	// @requires b!=null;
	public static Set<Integer> getFreeIndexes(Board b) {
		Set<Integer> resultList = new HashSet<Integer>();
		for (int i = 0; i < (Board.DIM * Board.DIM); i++) {
			if (b.isEmptyField(i)) {
				resultList.add(i);
			}
		}
		return resultList;
	}

	/**
	 * Kijkt of de speler met de zet andere ballen overneemt, en welke dit dan
	 * zijn.
	 * 
	 * @param zet
	 *            de zet
	 * @param board
	 *            het bord waarop gekeken wordt
	 * @param player
	 *            de speler die de zet (moet) doen.
	 * @return een set met alle mogelijke overnames.
	 */
	public static Set<Integer> getPossibleTakeOvers(int zet, Board board,
			Player player) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				Set<Integer> takeOvers = getTakeOvers(zet, board, player, i, j);
				if (takeOvers != null && takeOvers.size() > 0) {
					result.addAll(takeOvers);
				}
			}
		}
		return result;
	}

	/**
	 * Haalt alle overnames op die gedaan worden als je de zet doet op het bord
	 * en dan kijkt naar een bepaalde richting.
	 * 
	 * @param zet
	 *            de zet
	 * @param board
	 *            het bord
	 * @param player
	 *            de speler
	 * @param plusRow
	 *            de richtig naar boven of beneden
	 * @param plusCol
	 *            de richting naar links of naar rechts.
	 * @return de mogelijke overnames van de zet in de bepaalde richting.
	 */
	public static Set<Integer> getTakeOvers(int zet, Board board,
			Player player, int plusRow, int plusCol) {
		Set<Integer> toChange = new HashSet<Integer>();
		Ball playermark = player.getBall();
		int r = board.getRow(zet) + plusRow;
		int c = board.getCol(zet) + plusCol;
		while (board.isField(r, c) && !board.isEmptyField(r, c)) {
			if (!board.getField(r, c).equals(playermark)) {
				toChange.add(board.index(r, c));
			} else if (board.getField(r, c).equals(playermark)) {
				return toChange;
			} else {
				break;
			}

			if (plusRow > 0) {
				r++;
			} else if (plusRow < 0) {
				r--;
			}
			if (plusCol > 0) {
				c++;
			} else if (plusCol < 0) {
				c--;
			}
		}
		return null;
	}
}
