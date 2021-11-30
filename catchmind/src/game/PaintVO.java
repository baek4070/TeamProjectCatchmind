package game;

import javafx.scene.paint.Color;

public class PaintVO {
	private int signal;
	private double x; 
	private double y;
	private boolean paintBool;
	private int color;
	//signal 제외하고 매개변수로 받는 생성자 
	public PaintVO(double x, double y, boolean paintBool, int color) {
		this.x = x;
		this.y = y;
		this.paintBool = paintBool;
		this.color = color;
	}
	//다 넘겨받는 생성자
	public PaintVO(int signal, double x, double y, boolean paintBool, int color) {
		this.signal = signal;
		this.x = x;
		this.y = y;
		this.paintBool = paintBool;
		this.color = color;
	}
	//-----------게터세터 모음 ------------
	public int getSignal() {
		return signal;
	}
	public void setSignal(int signal) {
		this.signal = signal;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public boolean isPaintBool() {
		return paintBool;
	}
	public void setPaintBool(boolean paintBool) {
		this.paintBool = paintBool;
	}
	public Color getColor(int color) {
		Color penColor = Color.BLACK;
		switch(color) {
		case 0 : penColor = Color.BLACK; break;
		case 1 : penColor = Color.RED; break;
		case 2 : penColor = Color.BLUE; break;
		case 3 : penColor = Color.GREEN; break;
		case 4 : penColor = Color.YELLOW; break;
		case 5 : penColor = Color.WHITE; break;
		}
		return penColor;
	}
	public void setColor(int color) {
		this.color = color;
	}
	
	//-----------게터 세터 끝 ----------------
	
	
	
	
}