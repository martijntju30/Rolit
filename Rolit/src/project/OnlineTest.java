package project;

import ss.week7.chatbox.ClientGUI;
import ss.week7.chatbox.ServerGUI;

public class OnlineTest {

	public static void main(String[] args) {
		ClientGUI client1 = new ClientGUI();
		client1.tfName.setText("A");
		client1.tfSpelers.setText("2");
		ClientGUI client2 = new ClientGUI();
		client2.tfName.setText("B");
		client2.tfSpelers.setText("2");
		new ServerGUI();
	}

}