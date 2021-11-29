package game_demo.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("main.fxml")
		);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		// 무대 색상을 투명하게 지정
		// 무대를 모니터 중앙으로 지정
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
