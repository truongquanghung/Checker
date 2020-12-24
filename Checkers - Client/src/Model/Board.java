package Model;

import java.util.LinkedList;

import EnumConstants.Checkers;

public class Board {

	private Square[][] squares;

	public Board() {
		squares = new Square[8][8];
		setSquares();
		assignPlayerIDs();
	}

	// Cài bàn cờ 
	private void setSquares() {
		boolean rowInitialFilled, isFilled;
		int count = 0;

		// Hàng
		for (int r = 0; r < Checkers.NUM_ROWS.getValue(); r++) {
			rowInitialFilled = (r % 2 == 1) ? true : false;

			// Cột
			for (int c = 0; c < Checkers.NUM_COLS.getValue(); c++) {
				isFilled = (rowInitialFilled && c % 2 == 0) ? true : (!rowInitialFilled && c % 2 == 1) ? true : false;
				count++;
				squares[r][c] = new Square(count, r, c, isFilled);
			}
		}
	}

	public Square[][] getSquares() {
		return this.squares;
	}

	public int getTotlaSquares() {
		return squares.length;
	}

	// Cài vị trí cho người chơi
	private void assignPlayerIDs() {

		// Hàng từ 0-2 cho người chơi 1
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < Checkers.NUM_COLS.getValue(); c++) {
				if (squares[r][c].getIsFilled()) {
					squares[r][c].setPlayerID(Checkers.PLAYER_ONE.getValue());
				}
			}
		}

		// Hàng từ 5-7 cho người chơi 2
		for (int r = 5; r < 8; r++) {
			// Columns
			for (int c = 0; c < Checkers.NUM_COLS.getValue(); c++) {
				if (squares[r][c].getIsFilled()) {
					squares[r][c].setPlayerID(Checkers.PLAYER_TWO.getValue());
				}
			}
		}
	}

	// Tìm các ô phù hợp có thể đi
	public LinkedList<Square> findPlayableSquares(Square selectedSquare) {

		LinkedList<Square> playableSquares = new LinkedList<Square>();

		int selectedRow = selectedSquare.getSquareRow();
		int selectedCol = selectedSquare.getSquareCol();

		int movableRow = (selectedSquare.getPlayerID() == 1) ? selectedRow + 1 : selectedRow - 1;

		twoFrontSquares(playableSquares, movableRow, selectedCol);
		crossJumpFront(playableSquares, (selectedSquare.getPlayerID() == 1) ? movableRow + 1 : movableRow - 1,
				selectedCol, movableRow);
		if (selectedSquare.isKing()) {
			movableRow = (selectedSquare.getPlayerID() == 1) ? selectedRow - 1 : selectedRow + 1;
			twoFrontSquares(playableSquares, movableRow, selectedCol);
			crossJumpFront(playableSquares, (selectedSquare.getPlayerID() == 1) ? movableRow - 1 : movableRow + 1,
					selectedCol, movableRow);
		}
		return playableSquares;
	}

	// Kiểm tra 2 ô chéo trước ô đang đứng
	private void twoFrontSquares(LinkedList<Square> pack, int movableRow, int selectedCol) {

		if (movableRow >= 0 && movableRow < 8) {
			// Góc phải
			if (selectedCol >= 0 && selectedCol < 7) {
				Square rightCorner = squares[movableRow][selectedCol + 1];
				if (rightCorner.getPlayerID() == 0) {
					rightCorner.setPossibleToMove(true);
					pack.add(rightCorner);
				}
			}

			// Góc trái
			if (selectedCol > 0 && selectedCol <= 8) {
				Square leftCorner = squares[movableRow][selectedCol - 1];
				if (leftCorner.getPlayerID() == 0) {
					leftCorner.setPossibleToMove(true);
					pack.add(leftCorner);
				}
			}
		}
	}

	// Kiểm tra khả năng nhảy ăn quân 
	private void crossJumpFront(LinkedList<Square> pack, int movableRow, int selectedCol, int middleRow) {

		int middleCol;

		if (movableRow >= 0 && movableRow < 8) {
			// Phía bên chéo phải
			if (selectedCol >= 0 && selectedCol < 6) {
				Square rightCorner = squares[movableRow][selectedCol + 2];
				middleCol = (selectedCol + selectedCol + 2) / 2;
				if (rightCorner.getPlayerID() == 0 && isOpponentInbetween(middleRow, middleCol)) {
					rightCorner.setPossibleToMove(true);
					pack.add(rightCorner);
				}
			}

			// Phía bên chéo trái
			if (selectedCol > 1 && selectedCol <= 7) {
				Square leftCorner = squares[movableRow][selectedCol - 2];
				middleCol = (selectedCol + selectedCol - 2) / 2;
				if (leftCorner.getPlayerID() == 0 && isOpponentInbetween(middleRow, middleCol)) {
					leftCorner.setPossibleToMove(true);
					pack.add(leftCorner);
				}
			}
		}
	}

	// Kiểm tra quân đối phương nằm trong đường nhảy
	private boolean isOpponentInbetween(int row, int col) {
		return squares[row][col].isOpponentSquare();
	}
}
