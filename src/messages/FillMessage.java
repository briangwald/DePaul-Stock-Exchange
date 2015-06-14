package messages;

import prices.Price;
import exceptions.InvalidParameterException;

public class FillMessage extends Message implements Comparable<FillMessage>
{
	public FillMessage(String userName, String productSymbol, Price price, int volume, String details, String side, String id)
			throws InvalidParameterException
	{
		super(userName, productSymbol, price, volume, details, side, id);
	}

	public int compareTo(FillMessage fm) 
	{
		return this.getPrice().compareTo(fm.getPrice());
	}
}
