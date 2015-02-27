package exceptions;

/**
 * Exception class for when bad login credentials are given, either username,
 * password or file password.
 * 
 * @author Craig Thomson
 *
 */
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