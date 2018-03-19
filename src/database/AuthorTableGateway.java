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

import model.Author;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class AuthorTableGateway {
	private static Logger logger = LogManager.getLogger(AuthorTableGateway.class);
	private Connection conn = null;
	
	public AuthorTableGateway() {
		try {
			connectToDatabase();
		} catch(Exception e) {
			throw new DatabaseException(e);
		}
	}
	
	public void deleteAuthor(Author author) {
		// create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// insert a record 
			String query = "DELETE FROM Authors WHERE Authors.id = ?";
			st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setInt(1, author.getId());

			st.executeUpdate();
		} catch (SQLException e) {
			logger.error("Insert Author SQL query failed");
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
	
	public int insertAuthor(Author author) {
		// create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// insert a record 
			String query = "INSERT INTO Authors " +
					" (id, first_name, last_name, dob, gender, web_site) " +
					" VALUES (NULL, ?, ?, ?, ?, ?)";
			st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, author.getFirstName());
			st.setString(2, author.getLastName());
			st.setDate(3, java.sql.Date.valueOf(author.getDateOfBirth()));
			st.setString(4, author.getGender());
			st.setString(5, author.getWebSite());
			st.executeUpdate();

			rs = st.getGeneratedKeys();

			if(rs != null && rs.next()) {
				logger.info("New record inserted at row " + rs.getInt(1));
				return rs.getInt(1);
			}
			
		} catch (SQLException e) {
			logger.error("Insert Author SQL query failed");
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
	
	public void updateAuthor(Author author) {
		//create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String query = "UPDATE Authors SET "
					+ " first_name = ?, last_name = ?, dob = ?, gender = ?, web_site = ? "
					+ " WHERE id = ?";
			st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, author.getFirstName());
			st.setString(2, author.getLastName());
			st.setDate(3, java.sql.Date.valueOf(author.getDateOfBirth()));
			st.setString(4, author.getGender());
			st.setString(5, author.getWebSite());
			st.setInt(6, author.getId());
			st.executeUpdate();			
		} catch (SQLException e) {
			logger.error("Update Author SQL query failed");
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
	
	public ObservableList<Author> getAuthors() {
		//create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		ObservableList<Author> authors = FXCollections.observableArrayList();
		try {
			//fetch records 
			//don't need a parameterized query here since no user-provided input
			//is being passed to the db server
			String query = "SELECT * FROM Authors";
			st = conn.prepareStatement(query);
			
			rs = st.executeQuery();
			
			//iterate over the result set using the next() method
			//note: result set is not initially at first row. we have to manually move it to row 1
			while(rs.next()) {
				// Date must be converted to LocalDate
				Date date = rs.getDate("dob");
				LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
				
				authors.add(new Author(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"),
						localDate, rs.getString("gender"), rs.getString("web_site")));
			}
			return authors;
		} catch (SQLException e) {
			logger.error("get Authors SQL query failed");
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
