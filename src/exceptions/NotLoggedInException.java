package exceptions;

/**
 * Exception class for when user attempts a request whilst not logged in
 * 
 * @author Craig Thomson
 *
 */
public class NotLoggedInException extends RuntimeException
{
	private static final long serialVersionUID = -2184603801477251653L;

	public NotLoggedInException()
	{
		super();
	}

	public NotLoggedInException(String message)
	{
		super(message);
	}
}