package project;

import java.util.Scanner;

/**
 * Abstract class for keeping a player in the Tic Tac Toe game. Module 2 lab
 * assignment.
 * 
 * 
 * @author Theo Ruys en Arend Rensink
 * @version $Revision: 1.4 $
 */
public class Player {

    // -- Instance variables -----------------------------------------

    private String name;
    private Ball Ball;
    private String publicKey;

    // -- Constructors -----------------------------------------------

    /*@
       requires theName != null;
       requires theBall == theBall.XX || theBall == theBall.OO;
       ensures this.gettheName() == theName;
       ensures this.gettheBall() == theBall;
     */
    /**
     * Creates a new Player object.
     * 
     */
    public Player(String theName, Ball theBall) {
        this.name = theName;
        this.Ball = theBall;
    }

    // -- Queries ----------------------------------------------------

    /**
     * Returns the name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Ball of the player.
     */
    public Ball getBall() {
        return Ball;
    }

    /*@
    requires board != null;
    ensures board.isField(\result) && board.isEmptyField(\result);

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
        System.out.println("Dit is de huidige keuze: "+choice);
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

    /*@
       requires board != null & !board.isFull();
     */
    /**
     * Makes a move on the board. <br>
     * 
     * @param bord
     *            the current board
     */
    public void makeMove(Board board) {
        int keuze = determineMove(board);
        board.setField(keuze, getBall());
    }
}
