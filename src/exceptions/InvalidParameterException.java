package exceptions;

@SuppressWarnings("serial")
public class InvalidParameterException extends Exception 
{
	public InvalidParameterException(String err)
	{
		super(err);
	}
}
