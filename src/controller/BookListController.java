package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import book.Book;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import view.MainViewChange;

public class BookListController implements Initializable, Controller {
	
	private static Logger logger = LogManager.getLogger();
	@FXML private ListView<Book> bookList;
	private ObservableList<Book> books;

	public BookListController(ObservableList<Book> books){
		this.books = books;
	}
	
    @FXML
    void onBookListClicked(MouseEvent event) {
    	// checks for double-click, not just single
    	if(event.getClickCount() == 2)
    	{
	    	try {
	    		Book book = bookList.getSelectionModel().getSelectedItem();
	    		if(book != null) {
	    			new MainViewChange().setCenterView(MainViewChange.BOOK_DETAIL, book);
	    			logger.info(book + " clicked.");
	    		}
	    	} catch (IOException e) {
	    		logger.error("Author selected is invalid/null!");
	    	}
    	}
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.bookList.setItems(books);
	}

}
