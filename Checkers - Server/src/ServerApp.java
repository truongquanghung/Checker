import java.awt.BorderLayout;
import javax.swing.*;

import EnumConstants.Checkers;
import Session.HandleSession;

import java.io.*;
import java.net.*;
import java.util.Date;

public class ServerApp extends JFrame {
	
	private JScrollPane scroll;
	private JTextArea information;
	private JLabel title;
	
	private ServerSocket serverSocket;
	int sessionNo;
	
	public ServerApp(){
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		title = new JLabel("Server");
		information = new JTextArea();
		scroll = new JScrollPane(information);
		
		add(title,BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
	}	
	
	// Tạo Server và đợi Client
	public void startRunning(){
		try{
			
			PropertyManager pm = PropertyManager.getInstance();
			int port = pm.getPort();
			
			// Khởi tạo Server
			serverSocket = new ServerSocket(port);
			information.append(new Date() + ":- Server tạo ở cổng "+ port + " \n");
			sessionNo = 1;			
			
			while(true){
				
				information.append(new Date()+ ":- Session "+ sessionNo + " được bắt đầu\n");
				
				// Đợi người chơi 1
				Socket player1 = serverSocket.accept();
				information.append(new Date() + ":- Người chơi 1 tham gia qua IP: ");
				information.append(player1.getInetAddress().getHostAddress() + "\n");
				
				new DataOutputStream(player1.getOutputStream()).writeInt(Checkers.PLAYER_ONE.getValue());
				
				// Đợi người chơi 2
				Socket player2 = serverSocket.accept();
				information.append(new Date() + ":- Người chơi 2 tham gia qua IP: ");
				information.append(player2.getInetAddress().getHostAddress() +"\n");
				
				new DataOutputStream(player2.getOutputStream()).writeInt(Checkers.PLAYER_TWO.getValue());
				
				sessionNo++;
				
				// Tạo luồng cho 2 người chơi mới
				HandleSession task = new HandleSession(player1, player2);
				new Thread(task).start();
			}
		}catch(Exception ex){			
			ex.printStackTrace();
			System.exit(0);
		}				
	}
}
