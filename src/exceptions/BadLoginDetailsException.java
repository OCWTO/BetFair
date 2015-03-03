package exceptions;

/**
 * Exception class for when bad login credentials are given, either username,
 * password or file password.
 * 
 * @author Craig Thomson
 *
 */
public class BadLoginDetailsException extends RuntimeException
{
	private static final long serialVersionUID = 6797760693883067928L;

	public BadLoginDetailsException()
	{
		super();
	}

	public BadLoginDetailsException(String message)
	{
		super(message);
	}
}