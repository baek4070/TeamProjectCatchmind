package game;

import java.io.Serializable;


public class PaintDTO implements Serializable{
	private int signal; // 1초기화 2전송 3종료
	private double x1, y1, x2, y2; //드래그시 선을 그을 좌표값
	private double stroke; // 두께설정
	private int color; // 012345 순서대로 검빨초파노 그리고 캔버스색(지우개)
	
	public void setStroke(double stroke) {this.stroke = stroke;}
	public void setColor(int color) {this.color = color;}
	public void setSignal(int signal) {this.signal = signal;}
	public void setX1(double x1){	this.x1 = x1;	}
	public void setY1(double y1){	this.y1 = y1;	}
	public void setX2(double x2){	this.x2 = x2;	}
	public void setY2(double y2){	this.y2 = y2;	}

	public double getStroke() {return stroke;}
	public int getColor() {return color;}
	public int getSignal() {return signal;}
	public double getX1(){	return x1;	}
	public double getY1(){	return y1;	}
	public double getX2(){	return x2;	}
	public double getY2(){	return y2;	}

}
