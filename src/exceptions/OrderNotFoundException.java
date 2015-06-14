package exceptions;

@SuppressWarnings("serial")
public class OrderNotFoundException extends Exception 
{
	public OrderNotFoundException(String err)
	{
		super(err);
	}
}
