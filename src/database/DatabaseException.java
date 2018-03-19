package database;

public class DatabaseException extends RuntimeException {
	private static final long serialVersionUID = 656263806739014602L;

	public DatabaseException(Exception e) {
		super(e);
	}
}
