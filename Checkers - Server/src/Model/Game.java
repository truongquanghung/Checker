package Model;

import EnumConstants.*;

public class Game {
	private Square[][] squares;

	public Game() {
		squares = new Square[8][8];

		initializeSquares();
		assignPlayerIDs();
	}

	// Cài bàn cờ
	private void initializeSquares() {
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

	// Cài vị trí cho người chơi
	private void assignPlayerIDs() {

		// Hàng từ 0-2 cho người chơi 1
		for (int r = 0; r < 3; r++) {
			// Columns
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

	public Square[][] getSquares() {
		return this.squares;
	}

	public int getTotlaSquares() {
		return squares.length;
	}

	public Square getSquare(int from) {
		for (Square[] sRows : squares) {
			for (Square s : sRows) {
				if (s.getSquareID() == from) {
					return s;
				}

			}
		}
		return null;
	}

	// Kiểm tra kết thúc (có người chiến thắng)
	public boolean isOver() {
		int playerOne = 0;
		int playerTwo = 0;
		for (int r = 0; r < Checkers.NUM_ROWS.getValue(); r++) {
			for (int c = 0; c < Checkers.NUM_COLS.getValue(); c++) {
				if (squares[r][c].getPlayerID() == 1)
					playerOne++;

				if (squares[r][c].getPlayerID() == 2)
					playerTwo++;
			}
		}
		System.out.println(playerOne + " " + playerTwo);
		if (playerOne == 0 || playerTwo == 0) {
			return true;
		}
		return false;
	}
}
