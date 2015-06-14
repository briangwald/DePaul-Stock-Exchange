package client;

import exceptions.InvalidParameterException;

public class TradableUserData 
{
	private String userName;
	
	private String symbol;
	
	private String side;
	
	private String orderId;
	
	public TradableUserData(String userName, String symbol, String side, String id) throws InvalidParameterException
	{
		setUserName(userName);
		setSymbol(symbol);
		setSide(side);
		setId(id);
	}
	
	private void setUserName(String user) throws InvalidParameterException
	{
		if (user == null || user == "")
		{
			throw new InvalidParameterException("Username cannot be Null or empty.");
		}
		
		userName = user;
	}
	
	private void setSymbol(String sym) throws InvalidParameterException
	{
		if (sym == null || sym == "")
		{
			throw new InvalidParameterException("Symbol cannot be Null or empty.");
		}
		
		symbol = sym;
	}
	
	private void setSide(String s) throws InvalidParameterException
	{
		if (s == null || s== "")
		{
			throw new InvalidParameterException("Side cannot be Null or empty");
		}
		
		s = s.toUpperCase().trim();

		if (s != "BUY" && s != "SELL")
		{
			throw new InvalidParameterException("Side must be \"BUY\" or \"SELL\".");
		}
		
		side = s;
	}
	
	private void setId(String id) throws InvalidParameterException
	{
		if (id == null || id == "")
		{
			throw new InvalidParameterException("Order Id cannot be null or empty.");
		}
		
		orderId = id;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
	public String getSide()
	{
		return side;
	}
	
	public String getOrderId()
	{
		return orderId;
	}
	
	public String toString()
	{
		return String.format("User %s, %s %s (%s)", userName, side, symbol, orderId);
	}
}
