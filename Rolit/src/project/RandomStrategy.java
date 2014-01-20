package project;

import java.util.Set;

public class RandomStrategy extends Player {

	public RandomStrategy(String theName, project.Ball theBall) {
		super(theName, theBall);
		// TODO Auto-generated constructor stub
	}
	@Override
	public int determineMove(Board board) {
		Set<Integer> empty = Validatie.getFreeIndexes(board);
		for (Integer i: empty){
			if (Validatie.validMove(i, board, this)){

				return i;
			}
		}
		return 0;
    }
}
