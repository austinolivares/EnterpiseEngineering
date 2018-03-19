package view;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.Book;
import controller.AuthorDetailController;
import controller.AuthorListController;
import controller.BookAuditTrailController;
import controller.BookDetailController;
import controller.BookListController;
import controller.BookSearchController;
import controller.Controller;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.Author;

public class MainViewChange {
	private static Logger logger = LogManager.getLogger();
	public static final int NULL = -1;
	public static final int AUTHOR_LIST = 1;
	public static final int AUTHOR_DETAIL = 2;
	public static final int BOOK_LIST = 3;
	public static final int BOOK_DETAIL = 4;
	public static final int BOOK_SEARCH = 5;
	public static final int BOOK_AUDIT_TRAIL = 6;
	public static final int BOOK_AUDIT_TRAIL_LIST = 7;
	
	public MainViewChange() {
		
	}
	
	@SuppressWarnings("unchecked")
	public void setCenterView(int viewType, Object arg) throws IOException{
		try {
			Controller controller = null;
			URL fxmlFile = null;
			switch(viewType) {
				case AUTHOR_LIST:
					fxmlFile = this.getClass().getResource("/view/AuthorListView.fxml");
					controller = new AuthorListController((ObservableList<Author>) arg);
					//SingletonView.getInstance().setBottom(null);
					break;
				case AUTHOR_DETAIL:
					fxmlFile = this.getClass().getResource("/view/AuthorDetailView.fxml");
					controller = new AuthorDetailController((Author)arg);
					SingletonView.getInstance().setBottom(null);
					break;
				case BOOK_LIST:
					fxmlFile = this.getClass().getResource("/view/BookListView.fxml");
					controller = new BookListController((ObservableList<Book>) arg);
					break;
				case BOOK_DETAIL:
					fxmlFile = this.getClass().getResource("/view/BookDetailView.fxml");
					controller = new BookDetailController((Book)arg);
					SingletonView.getInstance().setBottom(null);
					break;
				case BOOK_AUDIT_TRAIL:
					fxmlFile = this.getClass().getResource("/view/BookAuditTrailView.fxml");
					controller = new BookAuditTrailController((Book)arg);
					break;
			}
		
			FXMLLoader loader = new FXMLLoader(fxmlFile);
			loader.setController(controller);
		
			Parent viewNode = loader.load();
			SingletonView.getInstance().setCenter(viewNode);
		} catch (IOException e) {
			logger.error("Error changing center view.");
			throw e;
		}
	}
	
	public void setBottomView(int viewType, Object arg) {
		try {
			Controller controller = null;
			URL fxmlFile = null;
			switch(viewType) {
				case BOOK_SEARCH:
					fxmlFile = this.getClass().getResource("/view/BookSearchView.fxml");
					controller = new BookSearchController();
					break;
			}
			
			FXMLLoader loader = new FXMLLoader(fxmlFile);
			loader.setController(controller);
		
			Parent viewNode = loader.load();
			SingletonView.getInstance().setBottom(viewNode);
		} catch(IOException e) {
			logger.error("Error changing bottom view.");
		}
	}
}
