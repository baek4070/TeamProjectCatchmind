package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
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
import javafx.stage.Stage;

public class MainController implements Initializable {

	@FXML private Canvas canvas;
	@FXML private Button btnClose, btnClear, btnEraser, btnStart,
						 btnBlack, btnRed, btnGreen, btnBlue, btnYellow;
	@FXML private Slider slider;
	@FXML private ColorPicker pick;
	@FXML private ProgressBar timer;
	
	GraphicsContext gc;
	Stage Stage;
	ArrayList<Thread> threadList;
	boolean isStart = true;
	Task<Void> task;
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private final String serverIP = "192.168.0.23";
	private final int port = 1593;
	private List<PaintDTO> list;
	private Socket socket;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Runnable servertask = new Runnable() {
			@Override
			public void run() {
				PaintServer paintServer = new PaintServer();
			}
		};
		// 네트워크 연결
		try {
			socket = new Socket(serverIP, port);
			writer = new ObjectOutputStream(socket.getOutputStream());
			reader = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			System.out.println("서버 찾을 수 없음");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("서버 연결 실패");
			e.printStackTrace();
			System.exit(0);
		}
		
		
		
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);	
		gc.setLineWidth(1);			
		
		slider.setMin(1);	
		slider.setMax(100);
		
		canvas.setOnMousePressed(event->{
			gc.beginPath();		
			gc.lineTo(event.getX(), event.getY());
		});
		
		canvas.setOnMouseDragged(event->{
			double x = event.getX();
			double y = event.getY();
			gc.lineTo(x, y);
			gc.stroke();
		});
		
		btnStart.setOnAction(event->{
			task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					for(int i=0; i<101; i++) {
						if(task.isCancelled()) {
							System.out.println("isCancelled");
							break;
						}
						super.updateMessage(String.valueOf(i));
						super.updateProgress(i, 100);
						Thread.sleep(500);
					}
					return null;
				}
			};
			timer.progressProperty().bind(
				task.progressProperty()
			); 
			Thread t = new Thread(task);
			t.setDaemon(true);
			t.start();
		});
		
		btnBlack.setOnAction(e->{
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(1);
		});
		
		btnRed.setOnAction(e->{
			gc.setStroke(Color.RED);
			gc.setLineWidth(1);
		});
		
		btnYellow.setOnAction(e->{
			gc.setStroke(Color.YELLOW);
			gc.setLineWidth(1);
		});
		
		btnBlue.setOnAction(e->{
			gc.setStroke(Color.BLUE);
			gc.setLineWidth(1);
		});
		
		btnGreen.setOnAction(e->{
			gc.setStroke(Color.GREEN);
			gc.setLineWidth(1);
		});
		
		btnEraser.setOnAction(e->{
			gc.setStroke(Color.WHITE);
			gc.setLineWidth(50);
		});
			
		btnClear.setOnAction(event->{
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			// pick.setValue(Color.WHITE);
			slider.setValue(1);
			gc.setLineWidth(1);
			gc.setStroke(Color.BLACK);
		});

		slider.valueProperty().addListener((ob,oldValue,newValue)->{
			int value = newValue.intValue();
			double result = value/10;
			gc.setLineWidth(result);
		});
		
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
	
	
		
	
	
	
}
