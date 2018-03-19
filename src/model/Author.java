package model;

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import database.AuthorTableGateway;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Author {
	private static Logger logger = LogManager.getLogger(Author.class);
	
	private int id;
	private SimpleStringProperty firstName;
	private SimpleStringProperty lastName;
	private SimpleObjectProperty<LocalDate> dateOfBirth;
	private SimpleStringProperty gender;
	private SimpleStringProperty webSite;

	private AuthorTableGateway gateway;
	
	public Author() {
		setId(0);
		firstName = new SimpleStringProperty();
		lastName = new SimpleStringProperty();
		dateOfBirth = new SimpleObjectProperty<LocalDate>();
		gender = new SimpleStringProperty();
		webSite = new SimpleStringProperty();
	}
	
	public Author(int id, String firstName, String lastName, LocalDate dateOfBirth, String gender, String webSite) {
		this();
		
		setId(id);
		setFirstName(firstName);
		setLastName(lastName);
		setDateOfBirth(dateOfBirth);
		setGender(gender);
		setWebSite(webSite);
	}
	
	public void saveAuthor(Author author) {
		if(author.getFirstName() == null || author.getLastName() == null ||
				author.getDateOfBirth() == null || author.getGender() == null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Field was left blank, please retry.");
			alert.setContentText("All fields except website are necessary!");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.info("Field left blank.");
				throw new NullFieldException();
			}
		}
		
		
		if(!isValidName(author.getFirstName()))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("First name is invalid, please retry.");
			alert.setContentText("Must be between 1 and 100 characters long.");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("Input first name failed business rules.");
				throw new IllegalArgumentException();
			}
		} else if(!isValidName(author.getLastName()))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Last name is invalid, please retry.");
			alert.setContentText("Must be between 1 and 100 characters long.");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("Input last name failed business rules.");
				throw new IllegalArgumentException();
			}
		} else if(!isValidDateOfBirth(author.getDateOfBirth()))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Date of Birth is invalid, please retry.");
			alert.setContentText("Must be before today's date.");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("Input date of birth failed business rules.");
				throw new IllegalArgumentException();
			}
		} else if(!isValidGender(author.getGender()))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Gender is invalid, please retry.");
			alert.setContentText("It's a dropdown menu, how did you mess this up??");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("Input gender failed business rules.");
				throw new IllegalArgumentException();
			}
		} else if(!isValidWebSite(author.getWebSite()))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			alert.setHeaderText("Website is invalid, please retry.");
			alert.setContentText("Must be less than or equal to 100 characters long.");
			alert.showAndWait();
			if (alert.getResult() != null){
				logger.error("Input website failed business rules.");
				throw new IllegalArgumentException();
			}	
		// if all inputs are valid
		} else {
			gateway = new AuthorTableGateway();
			setFirstName(author.getFirstName());
			setLastName(author.getLastName());
			setDateOfBirth(author.getDateOfBirth());
			setGender(author.getGender());
			setWebSite(author.getWebSite());
			
			if(author.getId() == 0){
				author.setId(gateway.insertAuthor(author));
			}
			else
				gateway.updateAuthor(author);
			logger.info("Model saved: " + author);
			gateway.close();
		}
	}
	
	// validation methods
	public boolean isValidId(int id) {
		if(id >= 0)
			return true;
		else
			return false;
	}
	
	public boolean isValidName(String name) {
		if(name.length() <= 100 && name.length() >= 1)
			return true;
		else
			return false;
	}
	
	public boolean isValidDateOfBirth(LocalDate dateOfBirth) {
		if(dateOfBirth.compareTo(LocalDate.now()) < 0)
			return true;
		else
			return false;
	}
	
	public boolean isValidGender(String gender) {
		if(gender.equals("F") || gender.equals("M") || gender.equals("U"))
			return true;
		else
			return false;
	}
	
	public boolean isValidWebSite(String webSite) {
		if(webSite == null || webSite.length() <= 100)
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
	
	public String getFirstName() {
		return firstName.get();
	}
	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}
	
	public String getLastName() {
		return lastName.get();
	}
	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth.get();
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth.set(dateOfBirth);
	}
	
	public String getGender() {
		return gender.get();
	}
	public void setGender(String gender) {
		this.gender.set(gender);
	}
	
	public String getWebSite() {
		return webSite.get();
	}
	public void setWebSite(String webSite) {
		this.webSite.set(webSite);
	}
	
	public SimpleStringProperty firstNameProperty() {
		return firstName;
	}
	public SimpleStringProperty lastNameProperty() {
		return lastName;
	}
	public SimpleObjectProperty<LocalDate> dateOfBirthProperty() {
		return dateOfBirth;
	}
	public SimpleStringProperty genderProperty() {
		return gender;
	}
	public SimpleStringProperty webSiteProperty() {
		return webSite;
	}
	
	@Override
	public String toString() {
		//return getId() + " " + getFirstName() + " " + getLastName();
		return getId() + " " + getFirstName() + " " + getLastName() + " " + getDateOfBirth() + " " + getGender() + " " + getWebSite();
	}
}
