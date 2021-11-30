package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class MainController implements Initializable {

	@FXML private TextArea txtArea;
	@FXML private Button btnStartStop;
	// Client Socket 연결 관리
	ServerSocket server;
	// Client thread 관리
	public static ExecutorService threadPool;//공유자원으로 활용할수 있도록 스태틱
	public static MainController mc;//공유자원으로 활용할수 있도록 스태틱
	// 전체 Client 목록
	public static List<Client> clients;
	// 사용할 포트 번호
	public final int SERVER_PORT = 8001;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnStartStop.setOnAction((event)->{
			if(btnStartStop.getText().equals("Stop")) {
				//서버 중지
				stopServer();
				btnStartStop.setText("Start");
				
			}else {
				// 서버 실행
				initServer();
				btnStartStop.setText("Stop");
			}
		});
	}
	
	
	
	// Socket Server 초기화
	public void initServer() {
		threadPool = Executors.newFixedThreadPool(30);
		mc = this;
		clients = new ArrayList<>();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					appendText("서버 생성");
					server = new ServerSocket(SERVER_PORT);
					while (true) {
						appendText("Client 연결 대기중...");
						Socket client = server.accept();//연결 요청을 계속 보냄
						//엑셉트된 클라이언트의 연결정보를 address메소드를 통해 가져옴
						String client_ip = client.getInetAddress().getHostAddress();
						appendText(client_ip+"연결 완료");
						Client c = new Client(client);
						synchronized (clients) {
							clients.add(c);
						}
						appendText(clients.size()+" 생성 완료");
					}
				} catch (Exception e) {
					stopServer();
					System.out.println("서버 종료 : "+e.getMessage());
				}
				
			}
		};
		threadPool.submit(task); //연결된 클라이언트 스레드풀에 작업이 전달됨
		
	}
	
	// Server Stop
	public void stopServer() {
		appendText("서버 종료");
		try {
			if (clients!=null) {
				for(Client c : clients) {
					if(c.client != null && !c.client.isClosed()) {
						c.client.close();
					}
				}
			}
			clients.clear();
			
			if(server != null && !server.isClosed()) {
				server.close();
			}
		} catch (IOException e) {}
		finally {
			threadPool.shutdownNow();
		}
	}
	
	// Server Log
	public void appendText(String data) {
		Platform.runLater(()->{
			txtArea.appendText(data+"\n");
		});
	}
	

}










