package Model;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Player{
	private Socket socket;
	private DataInputStream fromPlayer;
	private DataOutputStream toPlayer;
	
	public Player(Socket s){
		this.socket = s;
		
		try{
			fromPlayer = new DataInputStream(socket.getInputStream());
			toPlayer = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	// Gửi dữ liệu
	public int sendData(int data){
		try {
			this.toPlayer.writeInt(data);
			return 1; 
		} catch (IOException e) {
			return 99;
		}		
	}
	
	// Nhận dữ liệu
	public int receiveData(){
		int data = 0;
		try{
			data = this.fromPlayer.readInt();
			return data;
		}catch (IOException e) {
			return 99;
		}
	}
	
	// Ngắt kết nối
	public void closeConnection(){
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Kiểm tra trực tuyến
	public boolean isOnline(){
		return socket.isConnected();
	}
}
