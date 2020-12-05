package View;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import Model.Square;
import EnumConstants.*;
import Handler.MyMouseListener;

// Giao diện quân cờ 
public class SquarePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private Square square;
	private Border thickBorder = BorderFactory.createLineBorder(Colors.PURPLE.getColor(),5);
	private boolean focused;
	private MouseHandler handler;
	
	public SquarePanel(Square s){
		this.square = s;
		this.focused = false;
		handler = new MouseHandler();
		
		setListener();		
	}
	
	protected void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponents(g2);
		
		g2.setColor(Colors.BLACK.getColor());
		if(square.getIsFilled()){
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
		
		int squareplayerID = square.getPlayerID();
		if(isClicked()){
			g2.setColor(Colors.getFocusedColor(squareplayerID));
			paint(g2);
		}else{
			if(squareplayerID==1 || squareplayerID == 2){
				if(focused){
					g2.setColor(Colors.getFocusedColor(squareplayerID));
				}else{
					g2.setColor(Colors.getMyDefaultColor(squareplayerID));					
				}
				paint(g2);
			}
		}
		
		// Hiện khả năng đi
		if(square.isPossibleToMove()){
			if(focused){				
				setBorder(thickBorder);
			}else{				
				setBorder(null);
			}
		}else{
			setBorder(null);
		}
		
		// Hiện Vua
		if(square.isKing() && square.getIsFilled()){
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Arial",Font.BOLD,25));
			g2.drawString("K", getWidth()/2-8, getHeight()/2+8);
		}
	}
	
	public void setListener(){
		if(square.isPossibleToMove() || square.getPlayerID()==SessionVariable.myID.getValue()){
			this.removeMouseListener(handler);
			this.addMouseListener(handler);
		}else{
			this.removeMouseListener(handler);
		}
	}
	
	public void setListner(MyMouseListener MyListner){
		setListener();
		if(square.isPossibleToMove() || square.getPlayerID()==SessionVariable.myID.getValue()){
			this.removeMouseListener(MyListner);
			this.addMouseListener(MyListner);
		}else{
			this.removeMouseListener(MyListner);
		}
	}
	
	public Square getSquare(){
		return this.square;
	}
	
	public boolean isClicked(){
		return this.square.isSelected();
	}
	
	public void resetClicked(){
		this.square.setSelected(false);
	}
	
	public void setClicked(){
		this.square.setSelected(true);
	}
	
	
	private void paint(Graphics2D g2){
		int padding= 24;
		g2.fillOval(padding/2, padding/2, getWidth()-padding, getHeight()-padding);			
	}
	
	class MouseHandler extends MouseAdapter {
		
		public void mouseEntered(MouseEvent e){	
			super.mouseEntered(e);
			focused = true;
			repaint();
		}
		
		public void mouseExited(MouseEvent e){
			super.mouseExited(e);
			focused = false;
			repaint();
		}
		
		public void mousePressed(MouseEvent e) {
		}
	}
}
