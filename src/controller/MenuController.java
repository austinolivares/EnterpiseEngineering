package controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import book.Book;
import database.AuthorTableGateway;
import database.BookTableGateway;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import model.Author;
import view.MainViewChange;

public class MenuController implements Initializable {
	private static Logger logger = LogManager.getLogger();
	@FXML private MenuBar menuBar;
	@FXML private MenuItem menuAuthorList;
	@FXML private MenuItem menuAddAuthor;
	@FXML private MenuItem menuBookList;
	@FXML private MenuItem menuAddBook;
	@FXML private MenuItem menuQuit;
	private ObservableList<Author> authors;
	private ObservableList<Book> books;
	public AuthorTableGateway authorGateway;
	public BookTableGateway bookGateway;
	
	public MenuController()
	{		
	}
	
	@FXML
	void handleMenuAction(ActionEvent event)
	{
		if(event.getSource() == menuAuthorList)
		{
			logger.info("Author List View selected from menu bar.");
			
			// reloads author list if updated
			authorGateway = new AuthorTableGateway();
			authors = authorGateway.getAuthors();
			authorGateway.close();

			try {
				new MainViewChange().setCenterView(MainViewChange.AUTHOR_LIST, authors);
			} catch(IOException e) {
				logger.error("Error loading author list.");
			}
		}
		else if(event.getSource() == menuAddAuthor)
		{
			logger.info("Add Author selected from menu bar.");
			try {
				new MainViewChange().setCenterView(MainViewChange.AUTHOR_DETAIL, new Author());
			} catch(IOException e) {
				logger.error("Error adding author.");
			}
		}
		else if(event.getSource() == menuBookList)
		{
			logger.info("Book List View selected from menu bar.");
			
			// reloads book list if updated
			bookGateway = new BookTableGateway();
			books = bookGateway.getBooks();
			bookGateway.close();

			try {
				new MainViewChange().setCenterView(MainViewChange.BOOK_LIST, books);
				new MainViewChange().setBottomView(MainViewChange.BOOK_SEARCH, null);
			} catch(IOException e) {
				logger.error("Error loading book list.");
			}
		}
		else if(event.getSource() == menuAddBook)
		{
			logger.info("Add Book selected from menu bar.");
			try {
				new MainViewChange().setCenterView(MainViewChange.BOOK_DETAIL, new Book());
			} catch(IOException e) {
				logger.error("Error adding book.");
			}
		}
		else if(event.getSource() == menuQuit)
		{
			logger.info("Quit from menu bar.");
			System.exit(0);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menuBar.setFocusTraversable(true);
	}

}
