package project;

import clientEnServer.ClientGUI;
import clientEnServer.ServerGUI;


public class OnlineTest {

	public static void main(String[] args) {
		ClientGUI client1 = new ClientGUI();
		client1.tfName.setText("A");
		client1.tfSpelers.setText("2");
		ClientGUI client2 = new ClientGUI();
		client2.tfName.setText("B");
		client2.tfSpelers.setText("2");
//		ClientGUI client3 = new ClientGUI();
//		client3.tfName.setText("C");
//		client3.tfSpelers.setText("3");
		new ServerGUI();
	}

}
