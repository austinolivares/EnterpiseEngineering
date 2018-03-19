package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import book.Book;
import book.BookAuditTrail;
import book.Publisher;

public class BookTableGateway {
	private static Logger logger = LogManager.getLogger(BookTableGateway.class);
	private Connection conn = null;
	public PublisherTableGateway publisherGateway;
	
	public BookTableGateway() {
		try {
			connectToDatabase();
		} catch(Exception e) {
			throw new DatabaseException(e);
		}
	}
	
	public void insertBookAudit(Book book, String message) {
		// create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// insert a record 
			String query = "INSERT INTO book_audit_trail " +
					" (id, book_id, date_added, entry_msg) " +
					" VALUES (NULL, ?, CURRENT_TIMESTAMP, ?)";
			st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setInt(1, book.getId());
			st.setString(2, message);
			st.executeUpdate();

			rs = st.getGeneratedKeys();

			if(rs != null && rs.next()) {
				logger.info("New book audit record inserted at row " + rs.getInt(1));
			}
			
		} catch (SQLException e) {
			logger.error("Insert Book SQL query failed");
			throw new DatabaseException(e);
		} finally {
			//be sure to close things properly if they are open, regardless of exception
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Closing failed");
				throw new DatabaseException(e);
			}
		}
	}
	
	public ObservableList<BookAuditTrail> getBookAuditTrailList(Book book) {
		//create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		ObservableList<BookAuditTrail> bookAuditTrailList = FXCollections.observableArrayList();

		try {
			//fetch records 
			//don't need a parameterized query here since no user-provided input
			//is being passed to the db server
			String query = "select audit.*, book.* "
					+ " from Book book "
					+ " inner join book_audit_trail audit on book.id = audit.book_id "
					+ " where book.id = ?";
			st = conn.prepareStatement(query);
			st.setInt(1, book.getId());
			
			rs = st.executeQuery();
			
			//iterate over the result set using the next() method
			//note: result set is not initially at first row. we have to manually move it to row 1
			while(rs.next()) {
				// Date must be converted to LocalDate
				Date date = rs.getDate("date_added");
				LocalDate dateAdded = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
				
				bookAuditTrailList.add(new BookAuditTrail(rs.getInt("id"), dateAdded, rs.getString("entry_msg")));
			}
			return bookAuditTrailList;
		} catch (SQLException e) {
			logger.error("Get Book Audit Trails SQL query failed");
			throw new DatabaseException(e);
		} finally {
			//be sure to close things properly if they are open, regardless of exception
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Closing failed");
				throw new DatabaseException(e);
			}
		}
	}
	
	public ObservableList<Book> searchBooks(String search) {
		//create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		ObservableList<Book> books = FXCollections.observableArrayList();

		search = "%" + search + "%";
		try {
			//fetch records 
			//don't need a parameterized query here since no user-provided input
			//is being passed to the db server
			String query =  "SELECT * FROM Book WHERE Book.title LIKE ?";
			st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, search);
			
			rs = st.executeQuery();
			
			//iterate over the result set using the next() method
			//note: result set is not initially at first row. we have to manually move it to row 1
			while(rs.next()) {
				// Date must be converted to LocalDate
				Date date = rs.getDate("date_added");
				LocalDate dateAdded = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
				
				publisherGateway = new PublisherTableGateway();
				Publisher publisher = publisherGateway.getPublisherById(rs.getInt("publisher_id"));
				publisherGateway.close();
				
				books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("summary"),
						rs.getInt("year_published"), publisher, rs.getString("isbn"), dateAdded));
			}
			return books;
		} catch (SQLException e) {
			logger.error("Get Books SQL query failed");
			throw new DatabaseException(e);
		} finally {
			//be sure to close things properly if they are open, regardless of exception
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Closing failed");
				throw new DatabaseException(e);
			}
		}
	}
	
	public void deleteBook(Book book) {
		// create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// insert a record 
			String query = "DELETE FROM Book WHERE Book.id = ?";
			st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setInt(1, book.getId());

			st.executeUpdate();
		} catch (SQLException e) {
			logger.error("Delete Book SQL query failed");
			throw new DatabaseException(e);
		} finally {
			//be sure to close things properly if they are open, regardless of exception
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Closing failed");
				throw new DatabaseException(e);
			}
		}
	}
	
	public int insertBook(Book book) {
		// create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// insert a record 
			String query = "INSERT INTO Book " +
					" (id, title, summary, year_published, publisher_id, isbn, date_added) " +
					" VALUES (NULL, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
			st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, book.getYearPublished());
			st.setInt(4, book.getPublisher().getId());
			st.setString(5, book.getIsbn());
			st.executeUpdate();

			rs = st.getGeneratedKeys();

			if(rs != null && rs.next()) {
				logger.info("New record inserted at row " + rs.getInt(1));
				return rs.getInt(1);
			}
			
		} catch (SQLException e) {
			logger.error("Insert Book SQL query failed");
			throw new DatabaseException(e);
		} finally {
			//be sure to close things properly if they are open, regardless of exception
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Closing failed");
				throw new DatabaseException(e);
			}
		}
		// indicates insert failed, will not reach this though, it's mostly to get rid of return error
		return -1;
	}
	
	public void updateBook(Book book) {
		//create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String query = "UPDATE Book SET "
					+ " title = ?, summary = ?, year_published = ?, publisher_id = ?, isbn = ? "
					+ " WHERE id = ?";
			st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, book.getYearPublished());
			st.setInt(4, book.getPublisher().getId());
			st.setString(5, book.getIsbn());
			st.setInt(6, book.getId());
			st.executeUpdate();			
		} catch (SQLException e) {
			logger.error("Update Book SQL query failed");
			throw new DatabaseException(e);
		} finally {
			//be sure to close things properly if they are open, regardless of exception
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Closing failed");
				throw new DatabaseException(e);
			}
		}
	}
	
	public ObservableList<Book> getBooks() {
		//create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		ObservableList<Book> books = FXCollections.observableArrayList();

		try {
			//fetch records 
			//don't need a parameterized query here since no user-provided input
			//is being passed to the db server
			String query = "SELECT * FROM Book";
			st = conn.prepareStatement(query);
			
			rs = st.executeQuery();
			
			//iterate over the result set using the next() method
			//note: result set is not initially at first row. we have to manually move it to row 1
			while(rs.next()) {
				// Date must be converted to LocalDate
				Date date = rs.getDate("date_added");
				LocalDate dateAdded = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
				
				publisherGateway = new PublisherTableGateway();
				Publisher publisher = publisherGateway.getPublisherById(rs.getInt("publisher_id"));
				publisherGateway.close();
				
				books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("summary"),
						rs.getInt("year_published"), publisher, rs.getString("isbn"), dateAdded));
			}
			return books;
		} catch (SQLException e) {
			logger.error("Get Books SQL query failed");
			throw new DatabaseException(e);
		} finally {
			//be sure to close things properly if they are open, regardless of exception
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Closing failed");
				throw new DatabaseException(e);
			}
		}
	}
	
	public void connectToDatabase() throws IOException, SQLException {
		//read db credentials from properties file
		Properties props = new Properties();
		FileInputStream fis = null;
        fis = new FileInputStream("database_properties");
        props.load(fis);
        fis.close();
        
        //create the datasource
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(props.getProperty("MYSQL_DB_URL"));
        ds.setUser(props.getProperty("MYSQL_DB_USERNAME"));
        ds.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));

		//create the connection
		conn = ds.getConnection();
	}
	
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
}
