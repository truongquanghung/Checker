package Handler;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import Model.Square;
import View.SquarePanel;

public class MyMouseListener extends MouseAdapter{
	
	private SquarePanel squarePanel;
	private Controller controller;
	
	public void setController(Controller c){
		this.controller = c;
	}
	
	// Kiểm tra lượt khi nhấn chuột
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		
		
		try{			
			if(controller.isHisTurn()){
				ToggleSelectPiece(e);
			}else{
				JOptionPane.showMessageDialog(null, "Không phải lượt của bạn!",
					"Cảnh báo!", JOptionPane.ERROR_MESSAGE, null);
			}
		}catch(Exception ex){
			System.out.println("Error");
		}	
		
		
	}
	
	// Đảo trạng thái ô nhấn
	private void ToggleSelectPiece(MouseEvent e){
		try{
			squarePanel = (SquarePanel) e.getSource();
			Square s = squarePanel.getSquare();
			
			if(s.isSelected()){
				System.out.println("deselect - "+s.getSquareID());
				controller.squareDeselected();				
			}
			//else select
			else{
				System.out.println("select - "+s.getSquareID());
				controller.squareSelected(s);
			}
		}catch(Exception ex){
			System.out.println("error");
		}
	}
}
