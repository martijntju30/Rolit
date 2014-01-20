package project;

import java.util.HashSet;
import java.util.Set;

public class Validatie {

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
				System.out.println("Zet: " + zet + " en dit is een vrij veld!");
			}
		}
		boolean allowed = false;
		// Een move is pas valid als hij overneemt als dit kan.
		// Haal alle vrije vakjes op.
		Set<Integer> freeIndexes = getFreeIndexes(board);
		/** CAMILIO, Klopt wat ik heb gedaan?? *************************************************************************************************************************************************/
		System.out.println("Dit zijn alle vrije indexen: " + freeIndexes);
		// Kijk nu bij elke van deze vrije vakjes of deze ervoor zorgen dat ze
		// een andere blocken. Als dat zo is dat weet je dat er een move is om
		// anderen te blocken dus moet er geblockt worden
		boolean blockable = false;
		for (Integer i : freeIndexes) {
			Board boardCopy = board.deepCopy();
			boardCopy.setField(i, player.getBall());
			if (getPossibleTakeOvers(i, board, player).size() > 0) {
				System.out
						.println("De speler kan anderen blokkeren, dit moet hij dus ook doen. Dit kan met de zet "
								+ i
								+ ". Dit blokkeert: "
								+ getPossibleTakeOvers(i, board, player));
				blockable = true;
			}
		}
		// Kijk nu of de move iets blockt. Als beide ja is, dan is de move
		// valid.
		allowed = (getPossibleTakeOvers(zet, board, player).size() > 0 == blockable);
		System.out.println("Als je de zet " + zet + " doet, dan is dit valid:"
				+ valid + " en blockable: " + blockable + " en allowed: "
				+ allowed);
		// //////////////
		return valid && allowed;

	}

	public static Set<Integer> getFreeIndexes(Board b) {
		Set<Integer> resultList = new HashSet<Integer>();
		for (int i = 0; i < (Board.DIM * Board.DIM); i++) {
			if (b.isEmptyField(i)) {
				resultList.add(i);
			}
		}
		return resultList;
	}

	public static Set<Integer> getPossibleTakeOvers(int zet, Board board, Player player) {
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

	public static Set<Integer> getTakeOvers(int zet, Board board, Player player, int plusRow, int plusCol) {
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
