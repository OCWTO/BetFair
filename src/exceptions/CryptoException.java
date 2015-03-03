package exceptions;

/**
 * Exception class for when a bad certificate file password is given.
 * @author Craig Thomson
 *
 */
public class CryptoException extends RuntimeException
{
	private static final long serialVersionUID = 8678004041404827757L;

	public CryptoException()
	{
		super();
	}
	
	public CryptoException(String message)
	{
		super(message);
	}
}