package prices;

@SuppressWarnings("serial")
public class InvalidPriceOperation extends Exception
{
	public InvalidPriceOperation(String err)
	{
		super(err);
	}
}
