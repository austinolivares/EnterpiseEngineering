package controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.Book;
import database.BookTableGateway;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import view.MainViewChange;

public class BookSearchController implements Controller {

	private static Logger logger = LogManager.getLogger();
	public BookTableGateway bookGateway;
    @FXML private TextField searchBooksField;
    public ObservableList<Book> books;
    
    public BookSearchController() {
    }
	
    @FXML
    void searchBooks(KeyEvent event) {
    	if(event.getCode().equals(KeyCode.ENTER))
    	{
			try {
	    		bookGateway = new BookTableGateway();
	    		books = bookGateway.searchBooks(searchBooksField.getText());
	    		logger.info("Returned list for search value: \"" + searchBooksField.getText() + "\"");
	    		
	    		new MainViewChange().setCenterView(MainViewChange.BOOK_LIST, books);
			} catch (IOException e) {
				logger.error("Failed to display list view");
			}
    	}
    }
}
