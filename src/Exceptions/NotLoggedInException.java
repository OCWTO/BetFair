package Exceptions;

public class NotLoggedInException extends Exception
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
