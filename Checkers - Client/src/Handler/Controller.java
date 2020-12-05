package Handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import EnumConstants.Checkers;
import Model.Player;
import Model.Square;
import View.BoardPanel;

public class Controller implements Runnable {
	private boolean continueToPlay;
	private boolean waitingForAction;
	private int isOver;

	// Khai báo biến để kết nối mạng
	private DataInputStream fromServer;
	private DataOutputStream toServer;

	private BoardPanel boardPanel;
	private Player player;

	// Khai báo biến để lấy dữ liệu
	private LinkedList<Square> selectedSquares;
	private LinkedList<Square> playableSquares;
	// private LinkedList<Square> crossableSquares;

	public Controller(Player player, DataInputStream input, DataOutputStream output) {
		this.player = player;
		this.fromServer = input;
		this.toServer = output;

		selectedSquares = new LinkedList<Square>();
		playableSquares = new LinkedList<Square>();
	}

	public void setBoardPanel(BoardPanel panel) {
		this.boardPanel = panel;
	}

	@Override
	public void run() {
		continueToPlay = true;
		waitingForAction = true;
		isOver = 0;

		try {
			// Cài lượt chơi
			if (player.getPlayerID() == Checkers.PLAYER_ONE.getValue()) {
				fromServer.readInt();
				player.setMyTurn(true);
			}
			
			// Phân lượt chơi
			while (continueToPlay && isOver == 0) {
				if (player.getPlayerID() == Checkers.PLAYER_ONE.getValue()) {
					waitForPlayerAction();
					if (isOver == 0)
						receiveInfoFromServer();
				} else if (player.getPlayerID() == Checkers.PLAYER_TWO.getValue()) {
					receiveInfoFromServer();
					if (isOver == 0)
						waitForPlayerAction();
				}
			}
			
			// Kiểm tra thắng thua
			if (isOver != 0) {
				if (isOver == 1) {
					JOptionPane.showMessageDialog(null, "Chúc mừng "+player.getName()+ " bạn đã thắng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE, null);
					System.exit(0);
				} else {
					JOptionPane.showMessageDialog(null, "Chia buồn "+player.getName()+" bạn đã thua!", "Thông báo", JOptionPane.INFORMATION_MESSAGE,
							null);
					System.exit(0);
				}

			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Mất kết nối!", "Lỗi!", JOptionPane.ERROR_MESSAGE, null);
			System.exit(0);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Kết nối bị gian đoạn!", "Lỗi", JOptionPane.ERROR_MESSAGE, null);
		}
	}

	// Nhận thông tin từ server
	private void receiveInfoFromServer() throws IOException {
		player.setMyTurn(false);
		while (true) {
			int from = fromServer.readInt();
			if (from == -1)
				break;
			if (from == Checkers.YOU_LOSE.getValue()) {
				from = fromServer.readInt();
				int to = fromServer.readInt();
				updateReceivedInfo(from, to);
				isOver = -1;
				break;
			} else if (from == Checkers.YOU_WIN.getValue()) {
				isOver = 1;
				continueToPlay = false;
				break;
			} else {
				int to = fromServer.readInt();
				updateReceivedInfo(from, to);
			}
		}
	}

	// Gửi thông tin lên server 
	private void sendMove(Square from, Square to) {
		try {
			toServer.writeInt(from.getSquareID());
			toServer.writeInt(to.getSquareID());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Đợi đến lượt chơi của bản thân
	private void waitForPlayerAction() throws InterruptedException {
		player.setMyTurn(true);
		while (waitingForAction) {
			Thread.sleep(100);
		}
		waitingForAction = true;
	}

	// Di chuyển quân cờ
	public void move(Square from, Square to) {
		to.setPlayerID(from.getPlayerID());
		from.setPlayerID(Checkers.EMPTY_SQUARE.getValue());
		checkCrossJump(from, to);
		checkKing(from, to);
		squareDeselected();

		try {
			sendMove(from, to);
		} catch (Exception e) {
			System.out.println("Sending failed");
		}
	}

	// Đánh dấu quân đã chọn
	public void squareSelected(Square s) {

		if (selectedSquares.isEmpty()) {
			addToSelected(s);
		} else if (selectedSquares.size() >= 1) {
			if (playableSquares.contains(s)) {
				Square tmp = selectedSquares.getFirst();
				move(selectedSquares.getFirst(), s);
				if (Math.abs(s.getSquareRow() - tmp.getSquareRow()) == 2)
					while (checkContinue(s) != null) {
						tmp = checkContinue(s);
						move(s, tmp);
						s = tmp;
					}
				try {
					toServer.writeInt(-1);
					toServer.writeInt(-1);
				} catch (Exception e) {
				}
				playableSquares.clear();
				boardPanel.repaintPanels();
				waitingForAction = false;
			} else {
				squareDeselected();
				addToSelected(s);
			}
		}
	}

	// Kiểm tra khả năng ăn liên tục 
	private Square checkContinue(Square s) {
		LinkedList<Square> tmp = boardPanel.getPlayableSquares(s);
		for (Square square : tmp) {
			if (Math.abs(s.getSquareRow() - square.getSquareRow()) == 2)
				return square;
		}
		return null;
	}

	// Thêm vào danh sách ô chọn
	private void addToSelected(Square s) {
		s.setSelected(true);
		selectedSquares.add(s);
		getPlayableSquares(s);
	}

	// Loại khỏi danh sách ô chọn
	public void squareDeselected() {

		for (Square square : selectedSquares)
			square.setSelected(false);

		selectedSquares.clear();

		for (Square square : playableSquares) {
			square.setPossibleToMove(false);
		}

		playableSquares.clear();
		boardPanel.repaintPanels();
	}

	// Liệt kê các ô có thể đi
	private void getPlayableSquares(Square s) {
		playableSquares.clear();
		playableSquares = boardPanel.getPlayableSquares(s);

		for (Square square : playableSquares) {
			System.out.println(square.getSquareID());
		}
		boardPanel.repaintPanels();
	}

	// Kiểm tra lượt đi 
	public boolean isHisTurn() {
		return player.isMyTurn();
	}

	// Kiểm tra khả năng ăn quân đối phương
	private void checkCrossJump(Square from, Square to) {
		if (Math.abs(from.getSquareRow() - to.getSquareRow()) == 2) {
			int middleRow = (from.getSquareRow() + to.getSquareRow()) / 2;
			int middleCol = (from.getSquareCol() + to.getSquareCol()) / 2;

			Square middleSquare = boardPanel.getSquare((middleRow * 8) + middleCol + 1);
			middleSquare.setPlayerID(Checkers.EMPTY_SQUARE.getValue());
			middleSquare.removeKing();
		}
	}

	// Kiểm tra phong vua (đi hết bàn cờ)
	private void checkKing(Square from, Square movedSquare) {
		if (from.isKing()) {
			movedSquare.setKing();
			from.removeKing();
		} else if (movedSquare.getSquareRow() == 7 && movedSquare.getPlayerID() == Checkers.PLAYER_ONE.getValue()) {
			movedSquare.setKing();
		} else if (movedSquare.getSquareRow() == 0 && movedSquare.getPlayerID() == Checkers.PLAYER_TWO.getValue()) {
			movedSquare.setKing();
		}
	}

	// Cập nhật trạng thái bàn cờ sau khi nhận dữ liệu từ server
	private void updateReceivedInfo(int from, int to) {
		Square fromSquare = boardPanel.getSquare(from);
		Square toSquare = boardPanel.getSquare(to);
		toSquare.setPlayerID(fromSquare.getPlayerID());
		fromSquare.setPlayerID(Checkers.EMPTY_SQUARE.getValue());
		checkCrossJump(fromSquare, toSquare);
		checkKing(fromSquare, toSquare);
		boardPanel.repaintPanels();
	}
}
