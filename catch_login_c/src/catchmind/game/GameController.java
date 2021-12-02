package catchmind.game;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import catchmind.main.ClientMain;
import catchmind.vo.PaintVO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

public class GameController implements Initializable , GameInterface{

	@FXML private Canvas canvas;
	@FXML private Button btnClose, btnClear, btnEraser, btnStart,
						 btnBlack, btnRed, btnGreen, btnBlue, btnYellow;
	@FXML private Slider slider;
	@FXML private ColorPicker pick;
	@FXML private ProgressBar timer;
	
	private List<PaintVO> list;
	GraphicsContext gc;
	private int color = 0;// 기본값 0 번 검정
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ClientMain.thread.gameController = this;
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);	
		gc.setLineWidth(1);			
		
		slider.setMin(1);	
		slider.setMax(100);
		
		canvas.setOnMousePressed(event->{
			double x = event.getX();
			double y = event.getY();
			PaintVO paint = new PaintVO(x,y);
			paint.setSignal(2);
			paint.setColor(color);
			ClientMain.thread.sendData(paint);
		});
		
		canvas.setOnMouseDragged(event->{
			double x = event.getX();
			double y = event.getY();
			PaintVO paint = new PaintVO(x,y);
			paint.setSignal(3);
			paint.setColor(color);
			ClientMain.thread.sendData(paint);
		});
		
		btnStart.setOnAction(event->{
			
		});
		
		btnStart.setOnAction(event->{
			
		});
		
		btnBlack.setOnAction(e->{
			color = 0;
		});
		
		
		btnRed.setOnAction(e->{
			color = 1;
		});
		
		btnYellow.setOnAction(e->{
			color = 2;
		});
		
		btnBlue.setOnAction(e->{
			color = 3;
		});
		
		btnGreen.setOnAction(e->{
			color = 4;
		});
		
		btnEraser.setOnAction(e->{
			color = 5;
		});
			
		btnClear.setOnAction(event->{
			ResetCanvas();
		});


		slider.valueProperty().addListener((ob,oldValue,newValue)->{
			int value = newValue.intValue();
			double result = value/10;
			gc.setLineWidth(result);
		});
		//종료 버튼 액션 
		btnClose.setOnAction(event->{
			Alert alert = new Alert(AlertType.CONFIRMATION); 
			alert.setTitle("게임 종료"); 
			alert.setHeaderText("잠깐! 게임을 종료하시겠습니까?"); 
			alert.setContentText("OK 버튼 클릭 시 게임 종료됩니다."); 
			Optional<ButtonType> result = alert.showAndWait();
			if(result.get() ==  ButtonType.OK) {
				Platform.exit();
			}
		});
	}
	@Override
	public void receiveData(PaintVO vo) {
		//signal == 1 캔버스 초기화
		if(vo.getSignal() == 1) {
			ResetCanvas();
		}
		//signal == 2 마우스 클릭

		if(vo.getSignal() == 2) {
			System.out.println("클릭 전달");
		}
		//signal == 3 마우스 드래그
		if(vo.getSignal() == 3) {
			System.out.println("드래그 전달");
		}
		
	}
	@Override
	public void Painting(PaintVO vo) {
		gc = canvas.getGraphicsContext2D();
		for (int i = 0; i < list.size() - 1; i++) {
			if (list.get(i).isPaintBool()) {
				int color = list.get(i).getColor();
				Color penColor = Color.BLACK;
				switch(color) {
				case 0 : penColor = Color.BLACK; break;
				case 1 : penColor = Color.RED; break;
				case 2 : penColor = Color.BLUE; break;
				case 3 : penColor = Color.GREEN; break;
				case 4 : penColor = Color.YELLOW; break;
				case 5 : penColor = Color.WHITE; break;
				}
				gc.setStroke(penColor);
				gc.strokeLine(list.get(i).getX(), list.get(i).getY(), list.get(i + 1).getX(), list.get(i + 1).getY());
			}
		}
	}
	@Override
	public void ResetCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}


	
		
	
	
	
}
