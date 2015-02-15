package Exceptions;

public class BadLoginException extends Exception
{
	private static final long serialVersionUID = 6797760693883067928L;

	public BadLoginException()
	{
		super();
	}
	
	public BadLoginException(String message)
	{
		super(message);
	}
}
