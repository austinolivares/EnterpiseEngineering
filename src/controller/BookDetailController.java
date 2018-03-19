package controller;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.Book;
import book.Publisher;
import database.BookTableGateway;
import database.PublisherTableGateway;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.util.converter.NumberStringConverter;
import model.NullFieldException;
import view.MainViewChange;

public class BookDetailController implements Initializable, Controller {

	private static Logger logger = LogManager.getLogger(BookDetailController.class);
	private Book book;
	private Book bookCopy = new Book();
	public BookTableGateway bookGateway;
	public PublisherTableGateway publisherGateway;
    @FXML private TextField bookTitle;
    @FXML private TextArea bookSummary;
    @FXML private TextField bookYearPublished;
    @FXML private ComboBox<Publisher> bookPublisher;
    @FXML private TextField bookIsbn;
    @FXML private DatePicker bookDateAdded;
    @FXML private Button saveBook;
    @FXML private Button deleteBook;
	
	public BookDetailController(Book book) {
		this.book = book;
		setBookCopy();
	}
    
    private void setBookCopy(){
    	this.bookCopy.setId(book.getId());
		this.bookCopy.setTitle(book.getTitle());
		this.bookCopy.setSummary(book.getSummary());
		this.bookCopy.setYearPublished(book.getYearPublished());
		this.bookCopy.setPublisher(book.getPublisher());
		this.bookCopy.setIsbn(book.getIsbn());
		this.bookCopy.setDateAdded(book.getDateAdded());
    }
    
    @FXML
    void onDeleteBookClicked(ActionEvent event){
    	logger.info("Book deleted: " + bookCopy);
    	bookGateway = new BookTableGateway();
    	bookGateway.deleteBook(bookCopy);
    	ObservableList<Book> books = bookGateway.getBooks();
    	bookGateway.close();
    	
    	try {
    		new MainViewChange().setCenterView(MainViewChange.BOOK_LIST, books);
		} catch(IOException e) {
			logger.error("Error loading book list.");
		}
    }

    @FXML
    void onSaveBookClicked(ActionEvent event) {
    	try{
    		BookTableGateway gateway = new BookTableGateway();
    		if(bookCopy.getId() == 0) {
    			book.saveBook(bookCopy);
    			gateway.insertBookAudit(bookCopy, "Book Inserted");
    		}
    		else {
    			if(book.getTitle() != bookCopy.getTitle()) {
    				gateway.insertBookAudit(bookCopy, "Title changed from " + book.getTitle() + " to " + bookCopy.getTitle());
    				logger.info("Title changed from " + book.getTitle() + " to " + bookCopy.getTitle());
    			}
    			if(book.getSummary() != bookCopy.getSummary()) {
    				gateway.insertBookAudit(bookCopy, "Summary changed from " + book.getSummary() + " to " + bookCopy.getSummary());
    				logger.info("Summary changed from " + book.getSummary() + " to " + bookCopy.getSummary());
    			}
    			if(book.getYearPublished() != bookCopy.getYearPublished()) {
    				gateway.insertBookAudit(bookCopy, "Year Published changed from " + book.getYearPublished() + " to " + bookCopy.getYearPublished());
    				logger.info("Year Published changed from " + book.getYearPublished() + " to " + bookCopy.getYearPublished());
    			}
	    		if(book.getPublisher() != bookCopy.getPublisher()) {
	    			gateway.insertBookAudit(bookCopy, "Publisher changed from " + book.getPublisher() + " to " + bookCopy.getPublisher());
	    			logger.info("Publisher changed from " + book.getPublisher() + " to " + bookCopy.getPublisher());
	    		}
	    		if(book.getIsbn() != bookCopy.getIsbn()) {
	    			gateway.insertBookAudit(bookCopy, "ISBN changed from " + book.getIsbn() + " to " + bookCopy.getIsbn());
	    			logger.info("ISBN changed from " + book.getIsbn() + " to " + bookCopy.getIsbn());
	    		}
	    		book.saveBook(bookCopy);
    		}
    	}
    	catch(IllegalArgumentException e){
    		logger.error("Book not saved.");
    		setBookCopy();
    	}
    	catch(NullFieldException e){
    		logger.error("Book not saved.");
    	}
    }
    
    @FXML
    void onAuditTrailClicked(ActionEvent event) {
    	logger.info("Audit Trail clicked.");
    	
    	if(bookCopy.getId() != 0) {
	    	try {
				new MainViewChange().setCenterView(MainViewChange.BOOK_AUDIT_TRAIL, bookCopy);
			} catch (IOException e) {
				logger.error("Error loading book audit trail.");
			}
    	}
    	else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Save Book before viewing Audit Trail!");
			alert.setContentText("Since the book has not been saved or edited, no audit trail exists yet.");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("User attempted to visit audit trail view without saving book.");
			}
    	}
    }

	public void initialize(URL location, ResourceBundle resources) {
		publisherGateway = new PublisherTableGateway();
		
		bookTitle.textProperty().bindBidirectional(bookCopy.titleProperty());
		bookSummary.textProperty().bindBidirectional(bookCopy.summaryProperty());
		
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(false);
		bookYearPublished.textProperty().bindBidirectional(bookCopy.yearPublishedProperty(), new NumberStringConverter(format));

		bookPublisher.getItems().removeAll(bookPublisher.getItems());
		bookPublisher.getItems().addAll(publisherGateway.getPublishers());
		bookPublisher.getSelectionModel().select(bookCopy.getPublisher());
		bookPublisher.valueProperty().bindBidirectional(bookCopy.publisherProperty());
		
		if(bookCopy.getPublisher() == null)
			bookPublisher.getSelectionModel().select(publisherGateway.getPublisherById(1));
		
		bookIsbn.textProperty().bindBidirectional(bookCopy.isbnProperty());
		bookDateAdded.valueProperty().bindBidirectional(bookCopy.dateAddedProperty());
		
		publisherGateway.close();
	}
}
