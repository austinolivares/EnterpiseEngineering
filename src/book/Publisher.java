package book;


import javafx.beans.property.SimpleStringProperty;

public class Publisher {
	
	private int id;
	private SimpleStringProperty publisherName;
	
	public Publisher() {
		setId(0);
		publisherName = new SimpleStringProperty();
	}
	
	public Publisher(int id, String publisherName) {
		this();
		
		setId(id);
		setPublisherName(publisherName);
	}
	
	// accessor methods
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPublisherName() {
		return publisherName.get();
	}
	public void setPublisherName(String publisherName) {
		this.publisherName.set(publisherName);
	}
	
	public SimpleStringProperty publisherNameProperty() {
		return publisherName;
	}
	
	public String toString() {
		return getId() + " " + getPublisherName();
	}
}
