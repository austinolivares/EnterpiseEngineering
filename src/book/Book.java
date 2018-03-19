package book;

import java.time.LocalDate;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import database.BookTableGateway;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.NullFieldException;

public class Book {
	private static Logger logger = LogManager.getLogger(Book.class);

	private int id;
	private SimpleStringProperty title;
	private SimpleStringProperty summary;
	private SimpleIntegerProperty yearPublished;
	private SimpleObjectProperty<Publisher> publisher;
	private SimpleStringProperty isbn;
	private SimpleObjectProperty<LocalDate> dateAdded;
	
	private BookTableGateway gateway;
	
	public Book() {
		setId(0);
		title = new SimpleStringProperty();
		summary = new SimpleStringProperty();
		yearPublished = new SimpleIntegerProperty();
		publisher = new SimpleObjectProperty<Publisher>();
		isbn = new SimpleStringProperty();
		dateAdded = new SimpleObjectProperty<LocalDate>();
	}
	
	public Book(int id, String title, String summary, int yearPublished, Publisher publisher, String isbn, LocalDate dateAdded) {
		this();
		
		setId(id);
		setTitle(title);
		setSummary(summary);
		setYearPublished(yearPublished);
		setPublisher(publisher);
		setIsbn(isbn);
		setDateAdded(dateAdded);
	}
	
	public void saveBook(Book book) {
		if(book.getTitle() == null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Title was left blank, please retry.");
			alert.setContentText("Title field is necessary!");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.info("Field left blank.");
				throw new NullFieldException();
			}
		}
		
		
		if(!isValidTitle(book.getTitle()))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Title is invalid, please retry.");
			alert.setContentText("Must be between 1 and 255 characters long.");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("Input title failed business rules.");
				throw new IllegalArgumentException();
			}
		} else if(!isValidSummary(book.getSummary()))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Summary is invalid, please retry.");
			alert.setContentText("Must be less than 65536 characters long.");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("Input summary failed business rules.");
				throw new IllegalArgumentException();
			}
		} else if(!isValidYearPublished(book.getYearPublished()))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Year Published is invalid, please retry.");
			alert.setContentText("Must be on or before current year.");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("Input year published failed business rules.");
				throw new IllegalArgumentException();
			}
		} else if(!isValidIsbn(book.getIsbn()))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Isbn is invalid, please retry.");
			alert.setContentText("Must be less than 14 characters long.");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("Input gender failed business rules.");
				throw new IllegalArgumentException();
			}
		// if all inputs are valid
		} else {
			gateway = new BookTableGateway();
			setTitle(book.getTitle());
			setSummary(book.getSummary());
			setYearPublished(book.getYearPublished());
			setPublisher(book.getPublisher());
			setIsbn(book.getIsbn());
			
			if(book.getId() == 0){
				book.setId(gateway.insertBook(book));
			}
			else
				gateway.updateBook(book);
			logger.info("Model saved: " + book);
			gateway.close();
		}
	}
	
	// validation methods
	public boolean isValidTitle(String title) {
		if(title.length() <= 100 && title.length() >= 1)
			return true;
		else
			return false;
	}
	
	public boolean isValidSummary(String summary) {
		if(summary == null || summary.length() < 65536)
			return true;
		else
			return false;
	}
	
	public boolean isValidYearPublished(int yearPublished) {
		if(yearPublished > Calendar.getInstance().get(Calendar.YEAR))
			return false;
		else
			return true;
	}
	
	public boolean isValidIsbn(String isbn) {
		if(isbn == null || isbn.length() <= 13)
			return true;
		else
			return false;
	}
	
	// accessors methods
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title.get();
	}
	public void setTitle(String title) {
		this.title.set(title);
	}
	
	public String getSummary() {
		return summary.get();
	}
	public void setSummary(String summary) {
		this.summary.set(summary);
	}
	
	public int getYearPublished() {
		return yearPublished.get();
	}
	public void setYearPublished(int yearPublished) {
		this.yearPublished.set(yearPublished);
	}
	
	public Publisher getPublisher() {
		return publisher.get();
	}
	public void setPublisher(Publisher publisher) {
		this.publisher.set(publisher);
	}
	
	public String getIsbn() {
		return isbn.get();
	}
	public void setIsbn(String isbn) {
		this.isbn.set(isbn);
	}
	
	public LocalDate getDateAdded() {
		return dateAdded.get();
	}
	public void setDateAdded(LocalDate dateAdded) {
		this.dateAdded.set(dateAdded);
	}
	
	public SimpleStringProperty titleProperty() {
		return title;
	}
	public SimpleStringProperty summaryProperty() {
		return summary;
	}
	public SimpleIntegerProperty yearPublishedProperty() {
		return yearPublished;
	}
	public SimpleObjectProperty<Publisher> publisherProperty() {
		return publisher;
	}
	public SimpleStringProperty isbnProperty() {
		return isbn;
	}
	public SimpleObjectProperty<LocalDate> dateAddedProperty() {
		return dateAdded;
	}
	
	public String toString() {
		return getId() + " " + getTitle() + " " + getYearPublished() + " " + getPublisher() + " " + getIsbn() + " " + getDateAdded();
	}
}
