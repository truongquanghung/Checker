import javax.swing.*;

import Handler.Controller;
import Handler.MyMouseListener;
import Model.Player;
import View.BoardPanel;

import java.io.*;
import java.net.*;

public class ClientApp extends JFrame {

	private static final long serialVersionUID = 1L;

	private String address;
	private int port;

	// Model
	private Player player;

	// View
	private BoardPanel boardPanel;

	// Kết nối TCP/IP
	private Socket connection;
	private DataInputStream fromServer;
	private DataOutputStream toServer;

	// Constructor
	public ClientApp() {

		try {
			PropertyManager pm = PropertyManager.getInstance();
			address = pm.getAddress();
			port = pm.getPort();

			String name = (String) JOptionPane.showInputDialog(null, "Mời nhập tên người chơi:", "Kết nối tới Server",
					JOptionPane.OK_CANCEL_OPTION);

			if (name != null && name.length() > 0) {
				player = new Player(name);
				connect();
			} else {
				JOptionPane.showMessageDialog(null, "Tên không hợp lệ!", "Lỗi!", JOptionPane.ERROR_MESSAGE,
						null);
				System.exit(0);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Sai địa chỉ IP của Server!", "Lỗi!", JOptionPane.ERROR_MESSAGE,
					null);
			System.exit(0);
		}

	}
	
	// Kết nối server 
	private void connect() {

		try {
			connection = new Socket(address, port);

			fromServer = new DataInputStream(connection.getInputStream());
			toServer = new DataOutputStream(connection.getOutputStream());

			player.setPlayerID(fromServer.readInt());

			Controller task = new Controller(player, fromServer, toServer);
			setup(task);

			new Thread(task).start();
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Không cùng mạng kiểm tra lại IP!", "Lỗi",
					JOptionPane.ERROR_MESSAGE, null);
			System.exit(0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Không thể kết nối tới Server!", "Lỗi",
					JOptionPane.ERROR_MESSAGE, null);
			System.exit(0);
		}
	}

	// Cài đặt giao diện
	private void setup(Controller c) {
		MyMouseListener listener = new MyMouseListener();
		listener.setController(c);

		boardPanel = new BoardPanel(listener);
		c.setBoardPanel(boardPanel);
		add(boardPanel);
	}
}
