package clientEnServer;

/**
 * Een interface voor de GUI's van client en server
 * @author  Theo Ruys
 */
interface MessageUI {
	/**
	 * Laat het bericht toevoegen aan de berichten in het tekstvak berichten.
	 * @param msg Het bericht dat moet worden toegevoegd.
	 */
	//@requires msg !=null;
	public void addMessage(String msg);
}
