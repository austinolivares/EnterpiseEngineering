package view;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class SingletonView extends BorderPane{
	private static BorderPane singleton = null;
		
	private SingletonView() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("MainView.fxml"));
			BorderPane rootPane = loader.load();
			singleton = rootPane;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//nice and simple when app is single-threaded
	public static BorderPane getInstance() {
		if(singleton == null) {
			new SingletonView();
		}

		return singleton;
	}
	
}