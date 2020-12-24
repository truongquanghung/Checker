package Session;

import EnumConstants.Checkers;
import Model.Game;
import Model.Player;
import Model.Square;

import java.net.*;

public class HandleSession implements Runnable {

	private Game checkers;
	private Player player1;
	private Player player2;

	private boolean continueToPlay = true;

	// Khởi tạo luồng
	public HandleSession(Socket p1, Socket p2) {
		player1 = new Player(p1);
		player2 = new Player(p2);
		checkers = new Game();
	}

	public void run() {

		try {
			player1.sendData(1);

			while (continueToPlay) {
				
				// Đợi lượt người chơi 1
				while (true) {
					int from = player1.receiveData();
					int to = player1.receiveData();
					System.out.println("after break " + from + " " + to);
					if (from != -1) {
						checkStatus(from, to);
						updateGameModel(from, to);
						
						// Gửi dữ liệu cho người chơi 2
						int fromStatus = player2.sendData(from);
						int toStatus = player2.sendData(to);
						checkStatus(fromStatus, toStatus);

						// Trả về kết quả nếu kết thúc cho người chơi 2
						if (checkers.isOver()) {
							System.out.println("player 1");
							player2.sendData(Checkers.YOU_LOSE.getValue());
						}

						// Trả về kết quả nếu kết thúc cho người chơi 1
						if (checkers.isOver()) {
							player1.sendData(Checkers.YOU_WIN.getValue());
							continueToPlay = false;
							break;
						}
					} else {
						player2.sendData(from);
						break;
					}

				}
				
				// Đợi lượt người chơi 2
				while (true) {
					int from = player2.receiveData();
					int to = player2.receiveData();
					System.out.println("Second break " + from + " " + to);
					if (from != -1) {
						checkStatus(from, to);
						updateGameModel(from, to);

						// Gửi dữ liệu cho người chơi 1
						int fromStatus = player1.sendData(from);
						int toStatus = player1.sendData(to);
						checkStatus(fromStatus, toStatus);
						
						// Trả về kết quả nếu kết thúc cho người chơi 1
						if (checkers.isOver()) {
							System.out.println("player 2");
							player1.sendData(Checkers.YOU_LOSE.getValue()); // Game Over notification
						}

						// Trả về kết quả nếu kết thúc cho người chơi 2
						if (checkers.isOver()) {
							player2.sendData(Checkers.YOU_WIN.getValue());
							continueToPlay = false;
							break;
						}

					} else {
						player1.sendData(from);
						break;
					}
				}
			}

		} catch (Exception ex) {
			System.out.println("Ngắt kết nối");

			if (player1.isOnline())
				player1.closeConnection();

			if (player2.isOnline())
				player2.closeConnection();
			return;
		}
	}

	// Kiểm tra kết nối
	private void checkStatus(int status, int status2) throws Exception {
		if (status == 99 || status2 == 99) {
			throw new Exception("Mất kết nối");
		}
	}

	// Cập nhật trạng thái bàn cờ 
	private void updateGameModel(int from, int to) {
		Square fromSquare = checkers.getSquare(from);
		Square toSquare = checkers.getSquare(to);
		toSquare.setPlayerID(fromSquare.getPlayerID());
		fromSquare.setPlayerID(Checkers.EMPTY_SQUARE.getValue());

		checkCrossJump(fromSquare, toSquare);
	}

	// Kiểm tra nhảy ăn quân
	private void checkCrossJump(Square from, Square to) {
		if (Math.abs(from.getSquareRow() - to.getSquareRow()) == 2) {
			int middleRow = (from.getSquareRow() + to.getSquareRow()) / 2;
			int middleCol = (from.getSquareCol() + to.getSquareCol()) / 2;

			Square middleSquare = checkers.getSquare((middleRow * 8) + middleCol + 1);
			middleSquare.setPlayerID(Checkers.EMPTY_SQUARE.getValue());
		}
	}
}