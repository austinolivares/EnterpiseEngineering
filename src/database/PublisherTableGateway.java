package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import book.Publisher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PublisherTableGateway {
	private static Logger logger = LogManager.getLogger(PublisherTableGateway.class);
	private Connection conn = null;
	
	public PublisherTableGateway() {
		try {
			connectToDatabase();
		} catch(Exception e) {
			throw new DatabaseException(e);
		}
	}
	
	public Publisher getPublisherById(int id) {
		//create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String query = "SELECT * FROM Publisher "
					+ " WHERE id = ?";
			st = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setInt(1, id);

			rs = st.executeQuery();
			rs.next();
			
			return new Publisher(id, rs.getString("publisher_name"));
		} catch (SQLException e) {
			logger.error("Get Publisher By Id SQL query failed");
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
	
	public ObservableList<Publisher> getPublishers() {
		//create a prepared statement using an SQL query
		PreparedStatement st = null;
		ResultSet rs = null;
		ObservableList<Publisher> publishers = FXCollections.observableArrayList();
		try {
			//fetch records 
			//don't need a parameterized query here since no user-provided input
			//is being passed to the db server
			String query = "SELECT * FROM Publisher";
			st = conn.prepareStatement(query);
			rs = st.executeQuery();
			
			//iterate over the result set using the next() method
			//note: result set is not initially at first row. we have to manually move it to row 1
			while(rs.next()) {
				publishers.add(new Publisher(rs.getInt("id"), rs.getString("publisher_name")));
			}
			return publishers;
		} catch (SQLException e) {
			logger.error("Get Publishers SQL query failed");
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
