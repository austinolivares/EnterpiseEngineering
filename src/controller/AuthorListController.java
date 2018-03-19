package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.Author;
import view.MainViewChange;

public class AuthorListController implements Initializable, Controller {
	
	private static Logger logger = LogManager.getLogger();
	@FXML private ListView<Author> authorList;
	private ObservableList<Author> authors;

	public AuthorListController(ObservableList<Author> authors){
		this.authors = authors;
	}
	
    @FXML
    void onAuthorListClicked(MouseEvent event) {
    	// checks for double-click, not just single
    	if(event.getClickCount() == 2)
    	{
	    	try {
	    		Author author = authorList.getSelectionModel().getSelectedItem();
	    		if(author != null) {
	    			new MainViewChange().setCenterView(MainViewChange.AUTHOR_DETAIL, author);
	    			logger.info(author + " clicked.");
	    		}
	    	} catch (IOException e) {
	    		logger.error("Author selected is invalid/null!");
	    	}
    	}
    }
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.authorList.setItems(authors);
	}

}
