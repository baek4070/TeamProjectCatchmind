package main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerMain extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(
				getClass().getResource("Server.fxml")
			);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle("Catchmind Server");
			primaryStage.setScene(scene);
			MainController server = loader.getController();
			//stop 이 아닌 닫기버튼으로 종료하였으시 스레드가 계쏙 돌고있는걸 안전하게 종료시킨다
			primaryStage.setOnCloseRequest(event->{
				server.stopServer();
			});
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}







