package catchmind.game;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import catchmind.main.ClientMain;
import catchmind.main.MemberController;
import catchmind.vo.ChatVO;
import catchmind.vo.MemberVO;
import catchmind.vo.PaintVO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GameController implements Initializable , GameInterface{
	//-----그림 그리는 것과 관련된 fx:id값들-----
	@FXML private Canvas canvas;
	@FXML private Button btnClose, btnClear, btnEraser, btnStart, btnStop,
						 btnBlack, btnRed, btnGreen, btnBlue, btnYellow;
	@FXML private Slider slider;
	@FXML private ProgressBar timer;
	@FXML private Label lblAnswer;
	//******그림 그리는 것과 관련된 fx:id값들*******
	//------------- BGM -------------------------
	@FXML private Button muPlay, muPause, muStop;
	
	private boolean endOfMedia; // 파일의 끝까지 재생 완료를 확인할 flag
	private Media media;		// 재생해야할 resource 정보를 저장하는 객체
	private MediaPlayer mediaPlayer; // button을 통해 재생되는 resource를 제어하는 객체
	//************* BGM *************************
	//-----채팅 과 관련된 fx:id값들-----
	@FXML private Button btnEnter;
	@FXML private TextField chatArea;
	@FXML private TextArea chatResult;
	@FXML private TableView<MemberVO> userList; // tableview로 리스트 보여주게 만듬
	//******채팅 과 관련된 fx:id값들*******
	MemberVO name = MemberController.user;

	//-----그림그리는 관련 필드 -----
	GraphicsContext gc;
	private int color = 0;// 기본값 0 번 검정
	private double thickness = 1; // 기본값  굴기 1 
	Task<Void> task;
	private boolean boss = false; //true 이면 방장 , false이면 일반
	private String answer;
	private Thread t;
	//******그림그리는 관련 필드******
	
	//--------문제 내는 필드 -----------
	ArrayList<String> quiz = new ArrayList<String>(Arrays.asList(
			"사자성어","딸기","인어공주","타이타닉","올림픽","다크서클","세종대왕","일석이조","십중팔구","터미네이터",
			"오토바이","요리사","군인","야구","탁구","박명수","최기근"));
	//********문제 내는 필드 ***********
	
	//---------------initialize 정의 시작 -----------------
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		timer.setProgress(0);//시작버튼이 작동하게 하려면 프로그래스값 초기화
		ClientMain.thread.gameController = this; //뭔진 모르겠는데 안꼬일려면 해야됨
		Platform.runLater(()->{			// 추가
			chatArea.requestFocus();
			userListService();
		});
//---------------------------------그림그리는 관련------------------------
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);	
		gc.setLineWidth(1);			
		
		slider.setMin(1);	
		slider.setMax(100);
		
		
		btnStart.setOnMouseClicked(event->{
			if(event.getClickCount() == 1 && (timer.getProgress() == 1||timer.getProgress() == 0)) {//프로그래스바가 0이거나 1일때만 동작(게이지)
				System.out.println("시작버튼 클릭 이놈이 방장");
				PaintVO paint = new PaintVO();
				paint.setSignal(4);
				paint.setAnswer(quiz());
				ClientMain.thread.sendData(paint);
				boss = true;
			}else {
				System.out.println("이미 게임이 진행중입니다");
			}
		});
		btnStop.setOnAction(event->{
			if((timer.getProgress() != 1||timer.getProgress() != 0) && boss == true) {//프로그래스바가 0이거나 1아닐때만 동작(게임중일때만 동작)
				boss = false; //그림그리기 권한 종료 
				System.out.println("방장이 중지버튼 클릭 ");
				PaintVO paint = new PaintVO();
				paint.setSignal(5);
				ClientMain.thread.sendData(paint);
			}else {
				System.out.println("본인이 방장이 아닙니다");
			}
		});
		
		canvas.setOnMousePressed(event->{
			if(timer.getProgress() == 1||timer.getProgress() == 0||boss == true) {//프로그래스바가 0이거나 1일때만 동작(게이지)
			double x = event.getX();
			double y = event.getY();
			PaintVO paint = new PaintVO(x,y);
			paint.setSignal(2);
			paint.setColor(color);
			paint.setThickness(thickness);
			ClientMain.thread.sendData(paint);
			}else {
				System.out.println("이미 게임이 진행중입니다");
			}
		});
		
		canvas.setOnMouseDragged(event->{
			if(timer.getProgress() == 1||timer.getProgress() == 0||boss == true) {//프로그래스바가 0이거나 1일때만 동작(게이지)
			double x = event.getX();
			double y = event.getY();
			PaintVO paint = new PaintVO(x,y);
			paint.setSignal(3);
			paint.setColor(color);
			paint.setThickness(thickness);
			ClientMain.thread.sendData(paint);
			}else {
				System.out.println("이미 게임이 진행중입니다");
			}
		});
		btnClear.setOnAction(event->{
			if(timer.getProgress() == 1||timer.getProgress() == 0||boss == true) {//프로그래스바가 0이거나 1일때만 동작(게이지)
			PaintVO paint = new PaintVO();
			paint.setSignal(1);
			ClientMain.thread.sendData(paint);
			}else {
				System.out.println("이미 게임이 진행중입니다");
			}
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
			


		slider.valueProperty().addListener((ob,oldValue,newValue)->{
			int value = newValue.intValue();
			thickness = value/5 + 1;
		});
//****************************그림그리는 관련 ******************************
		
		
//----------------------------채팅 관련 ----------------------------------
		btnEnter.setOnAction(event->{
			if(boss == false) {
				String text = chatArea.getText();
				String nick = name.getMemberName();
				ChatVO chat = new ChatVO(nick,text,2);
				ClientMain.thread.sendData(chat);
				if(text.equals(answer)&&(timer.getProgress() != 1||timer.getProgress() != 0)) {
					correctAnswer();
				}
			}
			if(boss == true) {
				System.out.println("방장은 채팅불가합니다");
			}
			chatArea.clear();
		});
		
		chatArea.setOnKeyPressed(event->{
			if(event.getCode().equals(KeyCode.ENTER)) {
				btnEnter.fire();
			}
		});
		
//****************************채팅 관련 **********************************
		
		//----------종료 버튼 액션 ------
		btnClose.setOnAction(event->{
			Alert alert = new Alert(AlertType.CONFIRMATION); 
			alert.setTitle("게임 종료"); 
			alert.setHeaderText("잠깐! 게임을 종료하시겠습니까?"); 
			alert.setContentText("OK 버튼 클릭 시 게임 종료됩니다."); 
			Optional<ButtonType> result = alert.showAndWait();
			if(result.get() ==  ButtonType.OK) {
				Platform.exit();
			}
		});//******종료 버튼 액션 *********
	//---------------------------- 배경음악 관련 ------------------------------
		media = new Media(
			getClass().getResource("media/gameBGM.wav").toString()	
		);
		init(media);
		
		
	}//*****************initialize 정의 끝 ***********************
	//---------------------------- 배경음악 관련  메소드 ------------------------------
	private void correctSound() {
		Media mediacorrect = new Media(
				getClass().getResource("media/correct.MP3").toString()
		);
		MediaPlayer mediaPlayer1 = new MediaPlayer(mediacorrect);
		mediaPlayer1.play();
	}
	
	private void init(Media media) {
		if(mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer = null;
		}
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
		setMediaPlayer();
		
		// 재생버튼을 통해 음악실행
		muPlay.setOnAction(event->{
			if(endOfMedia) {
				mediaPlayer.stop();
			}
			endOfMedia = false;
			mediaPlayer.play();
		});
		
		muPause.setOnAction(event->{
			mediaPlayer.pause();
		});
		
		muStop.setOnAction(event->{
			mediaPlayer.stop();
		});
		
	}
	
		// MediaPlayer 초기화
	public void setMediaPlayer() {
		// 재생 준비가 완료 되었을때
		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				muPlay.setDisable(false);
				muPause.setDisable(true);
				muStop.setDisable(true);
			}
		});

		// play 상태 일때
		mediaPlayer.setOnPlaying(()->{
			muPlay.setDisable(true);
			muPause.setDisable(false);
			muStop.setDisable(false);
		});
			
		// 일시 정지
		mediaPlayer.setOnPaused(()->{
			muPlay.setDisable(false);
			muPause.setDisable(true);
			muStop.setDisable(false);
		});
			
		// endOfFile
		mediaPlayer.setOnEndOfMedia(()->{
			endOfMedia = true;
			mediaPlayer.stop();
			mediaPlayer.play();
		});
			
		// 중지 Stop
		mediaPlayer.setOnStopped(()->{
			// mediaPlayer에 등록된 미디어의 재생 시작 시간을 가져옴
			Duration duration = mediaPlayer.getStartTime();
			// 재생시간을 설정하는 method
			mediaPlayer.seek(duration);
			muPlay.setDisable(false);
			muPause.setDisable(true);
			muStop.setDisable(true);
		});
	}

//**************************** 배경음악 관련  메소드 끝 *******************************
	
//-----------------------리시브 데이터 메소드 모음 --------------------------
	// -----------PaintVO 객체 수신---------
	@Override
	public void receiveData(PaintVO vo) {
		//signal == 1 캔버스 초기화
		if(vo.getSignal() == 1) {
			resetCanvas();//실행 resetCanvas()
		}
		//signal == 2 마우스 클릭

		if(vo.getSignal() == 2) {
			System.out.println("클릭 전달");
			painting(vo);
		}
		//signal == 3 마우스 드래그
		if(vo.getSignal() == 3) { 
			System.out.println("드래그 전달");
			painting(vo);
		}
		//4번 게임 시작 신호
		if(vo.getSignal() == 4) {
			answer = vo.getAnswer();
			quiz.remove(answer);
			System.out.println(quiz);
			if(timer.getProgress() == 1||timer.getProgress() == 0) {//프로그래스바가 0이거나 1일때만 동작(게이지)
				resetCanvas();
				System.out.println("게임 시작!");
				timer.progressProperty().unbind();
				timer.setProgress(0);
				Platform.runLater(()->{//ui를 변경해주는거라 플랫폼 런레이터로 감싸주어야한다 
					progressBarStart(); 
				});
			}else {
				System.out.println("이미 게임이 진행중입니다");
			}
			if(boss == true) {
				Platform.runLater(()->{//ui를 변경해주는거라 플랫폼 런레이터로 감싸주어야한다 
				lblAnswer.setText(vo.getAnswer());  //방장만 문제 레이블 텍스트 변경
				});
			}
		}
		//5번 게임 강제종료 신호
		if(vo.getSignal() == 5) {
			gameStop();
			t.stop();
		}
		
	}// ***********PaintVO 객체 수신 종료***********
	
	// --------------ChatVO 객체 수신 ---------------
		public void receiveData(ChatVO vo) {
			
			// 유저목록
			if(vo.getSignal() == 1) {	// 수정
				Platform.runLater(()->{
					String name = vo.getName();
					chatResult.appendText(name+"님이 입장하셨습니다.\n");	
				});
			}
			
			// 채팅 view
			if(vo.getSignal() == 2) {
				String name = vo.getName();
				String text = vo.getText();
				Platform.runLater(()->{
					chatResult.appendText(name+ " : "+text+"\n");	
				});
			}
			if(vo.getSignal() == 3) {									//수정 + 4번 지움
				String outman = vo.getName();
				chatResult.appendText(outman+"님이 나갔습니다.\n");
			}
			if(vo.getSignal() == 5) {
				if(timer.getProgress() != 1||timer.getProgress() != 0){
					System.out.println("수신 정답");
					String name = vo.getName();
					String text = vo.getText();
					Platform.runLater(()->{
						chatResult.appendText("*************************\n"+name+ "님이 정답을 맞추셨습니다 !!\n*****************************\n ");
					});
					correctSound();//전원 정답소리 재생
					answer = null;
				}
			}
			
		}// ***********ChatVO 객체 수신 종료***********
	
//************************리시브 데이터 메소드 모음***************************
	
	
	
	@Override
	public void painting(PaintVO vo) {
				
				Color penColor = Color.BLACK;
				switch(vo.getColor()) {
				case 0 : penColor = Color.BLACK; break;
				case 1 : penColor = Color.RED; break;
				case 2 : penColor = Color.YELLOW; break;
				case 3 : penColor = Color.BLUE; break;
				case 4 : penColor = Color.GREEN; break;
				case 5 : penColor = Color.WHITE; break;
				}
				gc.setStroke(penColor);
				gc.setLineWidth(vo.getThickness());
				if(vo.getSignal() == 2) {
					gc.beginPath();	//선그리기 시작
					gc.lineTo(vo.getX(),vo.getY());
				}else if (vo.getSignal() == 3) {
					gc.lineTo(vo.getX(), vo.getY());
					gc.stroke();
				}
			
	}
	@Override
	public void resetCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public void progressBarStart() {
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
					System.out.println(timer.getProgress());
				}
				System.out.println("한판 끝");
				resetCanvas();
				boss = false; //그림그리기 권한 종료 
				Platform.runLater(()->{//ui를 변경해주는거라 플랫폼 런레이터로 감싸주어야한다 
					lblAnswer.setText("");
				});
				return null;
				
			}
			
		};
		timer.progressProperty().bind(
			task.progressProperty()
		); 
		t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
	
	public void gameStop() {
		boss = false; //그림그리기 권한 종료 
		timer.progressProperty().unbind();
		timer.setProgress(0);
		resetCanvas();
		Platform.runLater(()->{//ui를 변경해주는거라 플랫폼 런레이터로 감싸주어야한다 
			lblAnswer.setText("");
		});
	}
	
	private void userListService() {		// 추가
		TableColumn<MemberVO, String> columnNum = new TableColumn<>("유저번호");
		columnNum.setStyle("-fx-alignment:center;");
		columnNum.setPrefWidth(55);
		columnNum.setCellValueFactory(new PropertyValueFactory<>("memberNum"));
		TableColumn<MemberVO, String> columnName = new TableColumn<>("닉네임");
		columnName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
		columnName.setPrefWidth(150);
		userList.getColumns().add(0,columnNum);
		userList.getColumns().add(1,columnName);
		ClientMain.thread.sendData(0);
	}

	public void receiveData(Object obj) {		// 추가
		userList.setItems(FXCollections.observableArrayList((ArrayList<MemberVO>)obj));
	}
	
	public String quiz() {
		int index = (int)(Math.random()*quiz.size());
		
		if(quiz.size() != 0) {
			answer = quiz.get(index);
			return answer;
		}	
		return "문제 없음";
	}
	public void correctAnswer() {
		System.out.println("정답입니다 ");
		String text = answer;
		String nick = name.getMemberName();
		ChatVO chat = new ChatVO(nick,text,5);
		System.out.println(chat);
		ClientMain.thread.sendData(chat);
		PaintVO paint = new PaintVO();
		paint.setSignal(5);
		ClientMain.thread.sendData(paint);
	}
	
	
	
}
