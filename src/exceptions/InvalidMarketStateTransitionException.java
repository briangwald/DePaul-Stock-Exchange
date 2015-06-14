package exceptions;

@SuppressWarnings("serial")
public class InvalidMarketStateTransitionException extends Exception 
{
	public InvalidMarketStateTransitionException(String err)
	{
		super(err);
	}
}
