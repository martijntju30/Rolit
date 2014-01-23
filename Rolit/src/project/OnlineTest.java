package project;

import clientEnServer.ClientGUI;
import clientEnServer.ServerGUI;


public class OnlineTest {

	public static void main(String[] args) {
		ClientGUI client1 = new ClientGUI();
		client1.tfName.setText("player_camilio");
		client1.tfPass.setText("gezelligheid");
		client1.tfSpelers.setText("2");
		ClientGUI client2 = new ClientGUI();
		client2.tfName.setText("player_martijn");
		client2.tfPass.setText("lol");
		client2.tfSpelers.setText("2");
//		ClientGUI client3 = new ClientGUI();
//		client3.tfName.setText("C");
//		client3.tfSpelers.setText("3");
		new ServerGUI();
	}

}
