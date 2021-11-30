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
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	private List<PaintDTO> list;
	private Socket socket;
	GraphicsContext gc;
	Stage Stage;
	ArrayList<Thread> threadList;
	boolean isStart = true;
	Task<Void> task;
	private double x1, y1, x2, y2, z1, z2;
	private int color, thickness;
	
	public void initClient() {
		new Thread(()->{
			try {
				socket = new Socket("192.168.1.171", 1593);
				System.out.println("연결 성공");
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
		}).start();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		initClient();
		
		list = new ArrayList<PaintDTO>();
		
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);	
		gc.setLineWidth(1);			
		
		slider.setMin(1);	
		slider.setMax(100);
		
		canvas.setOnMousePressed(event->{
			z1 = event.getX();
			z2 = event.getY();
			gc.beginPath();		
			gc.lineTo(event.getX(), event.getY());
		});
		
		canvas.setOnMouseDragged(event->{
			x2 = event.getX();
			y2 = event.getY();
			gc.lineTo(x2, y2);
			gc.stroke();
			PaintDTO dto = new PaintDTO();
			dto.setX1(z1); // 아주 짧은 선을 그릴 첫 좌표 두개
			dto.setY1(z2);
			dto.setX2(x2); // 아주 짧은 선을 그릴 두번째 좌표 두개
			dto.setY2(y2);
			dto.setSignal(2); // 2는 정상메시지, 1은 초기화, 3은 퇴장 시그널
			dto.setColor(color); // 필드선언된 int color는 버튼액션에 반응해서 변경된 후 dto에 삽입된다.
			dto.setStroke(slider.getValue()/4);
			try {
				writer.writeObject(dto);
				writer.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			z1 = event.getX();
			z2 = event.getY();
		});
		
		// ------------------------수신부--------------------------------
				// -----------------------Thread----------------------
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {

						while (true) {
							try {
								// 서버로부터 좌표 4개를 담은 dto를 받는다
								
								
								
								PaintDTO dto = (PaintDTO) reader.readObject();

								if (dto.getSignal() == 1) {
								}

								if (dto.getSignal() == 2) {
									list.add(dto);
								}

								// dto의 signal이 3이면 종료한다.
								if (dto.getSignal() == 3) {
									writer.close();
									reader.close();
									socket.close();
									break;
								}

							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					}
				});
				t.setDaemon(true);
				t.start();
				// ------------------------수신부 끝--------------------------------
				// -----------------------End of Thread---------------------
				
		
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
			Thread th = new Thread(task);
			th.setDaemon(true);
			th.start();
		});
		
		btnBlack.setOnAction(e->{
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(1);
			color = 0;
		});
		
		btnRed.setOnAction(e->{
			gc.setStroke(Color.RED);
			gc.setLineWidth(1);
			color = 1;
		});
		
		btnYellow.setOnAction(e->{
			gc.setStroke(Color.YELLOW);
			gc.setLineWidth(1);
			color = 2;
		});
		
		btnBlue.setOnAction(e->{
			gc.setStroke(Color.BLUE);
			gc.setLineWidth(1);
			color = 3;
		});
		
		btnGreen.setOnAction(e->{
			gc.setStroke(Color.GREEN);
			gc.setLineWidth(1);
			color = 4;
		});
		
		btnEraser.setOnAction(e->{
			gc.setStroke(Color.WHITE);
			gc.setLineWidth(50);
			color = 5;
		});
			
		btnClear.setOnAction(event->{
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			// pick.setValue(Color.WHITE);
			slider.setValue(1);
			gc.setLineWidth(1);
			gc.setStroke(Color.BLACK);
			PaintDTO dto = new PaintDTO();
			dto.setSignal(1);
			try {
				writer.writeObject(dto);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
//		
//		pick.valueProperty().addListener(new ChangeListener<Color>() {
//			@Override
//			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
//				gc.setStroke(newValue);
//			}
//		});
//		
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
				exit();
				Platform.exit();
			}
		});
	}
	
	public void exit() {
		PaintDTO dto = new PaintDTO();
		dto.setSignal(3);
		try {
			writer.writeObject(dto);
			writer.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public ObjectOutputStream getWriter() {return writer;}
	public ObjectInputStream getReader() {return reader;}
	public Socket getSocket() {return socket;}
	public Canvas getCanvas() {return canvas;}

		
	
	
	
}