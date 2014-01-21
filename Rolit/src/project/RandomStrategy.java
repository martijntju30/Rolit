package project;

public class RandomStrategy extends Player {

	public RandomStrategy(String theName, project.Ball theBall) {
		super(theName, theBall);
		// TODO Auto-generated constructor stub
	}
	@Override
	public int determineMove(Board board) {
		return Strategys.getRandom(board, this);
    }
}
