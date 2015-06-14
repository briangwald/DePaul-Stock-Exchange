package messages;

import prices.Price;
import exceptions.InvalidParameterException;

public class CancelMessage extends Message implements Comparable<CancelMessage>
{

	public CancelMessage(String userName, String productSymbol, Price price, int volume, String details, String side, String id)
			throws InvalidParameterException
	{
		super(userName, productSymbol, price, volume, details, side, id);
	}

	public int compareTo(CancelMessage cm) 
	{
		return this.getPrice().compareTo(cm.getPrice());
	}
	
}
