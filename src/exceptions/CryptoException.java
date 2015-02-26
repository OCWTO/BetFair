package exceptions;

public class CryptoException extends Exception
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