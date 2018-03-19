package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import database.AuthorTableGateway;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.Author;
import model.NullFieldException;
import view.MainViewChange;

public class AuthorDetailController implements Initializable, Controller {

	private static Logger logger = LogManager.getLogger(AuthorDetailController.class);
	private Author author;
	private Author authorCopy = new Author();
	public AuthorTableGateway gateway;
    @FXML private TextField authorFirstName;
    @FXML private TextField authorLastName;
    @FXML private DatePicker authorDateOfBirth;
    @FXML private ChoiceBox<String> authorGender;
    @FXML private TextField authorWebsite;
    @FXML private Button saveAuthor;
    @FXML private Button deleteAuthor;
	
	public AuthorDetailController(Author author) {
		this.author = author;
		setAuthorCopy();
	}
    
    private void setAuthorCopy(){
    	this.authorCopy.setId(author.getId());
		this.authorCopy.setFirstName(author.getFirstName());
		this.authorCopy.setLastName(author.getLastName());
		this.authorCopy.setDateOfBirth(author.getDateOfBirth());
		this.authorCopy.setGender(author.getGender());
		this.authorCopy.setWebSite(author.getWebSite());
    }
    
    @FXML
    void onDeleteAuthorClicked(ActionEvent event){
    	logger.info("Author deleted: " + authorCopy);
    	gateway = new AuthorTableGateway();
    	gateway.deleteAuthor(authorCopy);
    	ObservableList<Author> authors = gateway.getAuthors();
    	gateway.close();
    	
    	try {
    		new MainViewChange().setCenterView(MainViewChange.AUTHOR_LIST, authors);
    	} catch(IOException e) {
    		logger.error("Error loading author list.");
    	}
    }

    @FXML
    void onSaveAuthorClicked(ActionEvent event) {
    	try{
    		author.saveAuthor(authorCopy);
    	}
    	catch(IllegalArgumentException e){
    		logger.error("Author not saved.");
    		setAuthorCopy();
    	}
    	catch(NullFieldException e){
    		logger.error("Author not saved.");
    	}
    }

	public void initialize(URL location, ResourceBundle resources) {		
		authorFirstName.textProperty().bindBidirectional(authorCopy.firstNameProperty());
		authorLastName.textProperty().bindBidirectional(authorCopy.lastNameProperty());
		authorDateOfBirth.valueProperty().bindBidirectional(authorCopy.dateOfBirthProperty());

		authorGender.getItems().removeAll(authorGender.getItems());
		authorGender.getItems().addAll("M", "F", "U");
		authorGender.getSelectionModel().select(authorCopy.getGender());
		authorGender.valueProperty().bindBidirectional(authorCopy.genderProperty());
		
		if(authorCopy.getGender() == null)
			authorGender.getSelectionModel().select("U");
		
		authorWebsite.textProperty().bindBidirectional(authorCopy.webSiteProperty());
	}
}
