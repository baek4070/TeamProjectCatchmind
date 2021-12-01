package catchmind.main;

import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientMain extends Application {
	
	public static Socket socket;
	public static MainThread thread;

	@Override
	public void start(Stage primaryStage) throws Exception {
		initClient();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void initClient() {
		new Thread(()->{
			try {
				socket = new Socket("192.168.1.171",8001);
				System.out.println("연결 성공");
				thread = new MainThread();
				thread.setDaemon(true);
				thread.start();
				Platform.runLater(()->{
					try {
						showMemberStage();
					} catch (Exception e) {}
				});
			} catch (Exception e) {
				System.out.println("연결 실패");
				Platform.runLater(()->{
					showAlert("연결실패\n다시 실행하라");
				});
			}
		}).start();;
	}

	public void showMemberStage(){
		try {
			System.out.println("showMemberStage 호출");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Root.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			System.out.println("show?");
			e.getMessage();
		}
		
	}

	public void showAlert(String text) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("알림");
		alert.setHeaderText(text);
		alert.setContentText("확인 : 재시도 \n취소 : 종료");
		alert.showAndWait();
		if(alert.getResult() == ButtonType.OK) {
			initClient();
		}else if(alert.getResult() == ButtonType.CANCEL) {
			Platform.exit();
		}
	}
}
