package main;

import controller.AuthorListController;
import controller.MenuController;
import database.AuthorTableGateway;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Author;
import view.SingletonView;

/**
 * CS 4743 Assignment #3 by Austin Olivares
 * @author xzk795
 *
 */
public class AuthorListLauncher extends Application {

	private ObservableList<Author> authors;
	public AuthorTableGateway gateway;
	
	@Override
	public void init() throws Exception {
		super.init();
		
		// connects to database and gets list of all authors in DB
		gateway = new AuthorTableGateway();
		authors = gateway.getAuthors();
		gateway.close();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

	@Override
	public void start(Stage stage) throws Exception {
		// loads main view
		BorderPane rootPane = SingletonView.getInstance();
		
		// loads menu view, sets menu controller
		FXMLLoader menuLoader = new FXMLLoader(this.getClass().getResource("/view/MenuView.fxml"));
		menuLoader.setController(new MenuController());
		MenuBar menuBar = menuLoader.load();
		rootPane.setTop(menuBar);
		
		// loads book list view, sets controller, and sets to center pane
		FXMLLoader listLoader = new FXMLLoader(this.getClass().getResource("/view/AuthorListView.fxml"));
		listLoader.setController(new AuthorListController(authors));
		ListView<Author> listView = listLoader.load();
		rootPane.setCenter(listView);
		
		Scene scene = new Scene(rootPane, 600, 400);
		stage.setScene(scene);
		stage.setTitle("Author List");
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
