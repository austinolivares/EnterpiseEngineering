package book;

import java.time.LocalDate;

public class BookAuditTrail {
	
	private int id;
	private LocalDate dateAdded;
	private String message;
	
	//public BookAuditTrail() {
	//
	//}
	
	public BookAuditTrail(int id, LocalDate dateAdded, String message) {
		//this();
		setId(id);
		setDateAdded(dateAdded);
		setMessage(message);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(LocalDate dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString() {
		return getId() + " " + getDateAdded() + " " + getMessage();
	}
}
