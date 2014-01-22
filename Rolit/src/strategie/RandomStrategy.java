package strategie;

import rolit.Ball;
import rolit.Board;
import rolit.Player;

public class RandomStrategy extends Player {

	public RandomStrategy(String theName, rolit.Ball theBall) {
		super(theName, theBall);
		// TODO Auto-generated constructor stub
	}
	@Override
	public int determineMove(Board board) {
		return Strategys.selectRandom(Strategys.getValidMoves(board, this));
    }
}
