package project;

public class Spel {

	public static void main(String[] args) {
			//Player p1 = new Player("p1", Ball.BLUE);
			Player p1 = new Player("p1", Ball.BLUE);
			Player p2 = new Player("p2", Ball.YELLOW);
			Player p3 = new Player("p3", Ball.GREEN);
			Player p4 = new Player("p4", Ball.RED);
			Game gam = new Game(4, p1, p2, p3, p4);
			gam.start();
		}
}
