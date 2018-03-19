package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.Book;
import book.BookAuditTrail;
import database.BookTableGateway;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
//import book.BookAuditTrail;
//import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
//import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import view.MainViewChange;

public class BookAuditTrailController implements Initializable, Controller {
	private static Logger logger = LogManager.getLogger();
	
    @FXML private TextField bookAuditTrailLabel;
    @FXML private Button backButton;
    @FXML private ListView<BookAuditTrail> bookAuditTrailList;
    private Book book;
    private ObservableList<BookAuditTrail> bookAuditList;
    private BookTableGateway gateway;
	
	public BookAuditTrailController(Book book) {
		this.book = book;		
	}
	
    @FXML
    void backToBookDetailView(ActionEvent event) {
    	logger.info("Back button clicked");
    	
    	try {
			new MainViewChange().setCenterView(MainViewChange.BOOK_DETAIL, book);
		} catch (IOException e) {
			logger.error("Error going back to book detail view");
		}
    }
	
	//@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.bookAuditTrailLabel.setText("Audit Trail for: " + book);
		
		gateway = new BookTableGateway();
		bookAuditList = gateway.getBookAuditTrailList(book);
		gateway.close();
		
		this.bookAuditTrailList.setItems(bookAuditList);
	}
}
