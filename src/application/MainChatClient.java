package application;
	
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/*Load the main FXML file for a client/the main chat window.
 *Client uses simple MVC model written in JavaFX.
 */
public class MainChatClient extends Application {
	
	private Pane root;
	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
	
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainChatClient.class.getResource("ChatClient.fxml"));
		
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		Application.launch();
	}
	
}
