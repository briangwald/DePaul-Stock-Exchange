package messages;

import exceptions.InvalidParameterException;


public class MarketMessage 
{
	private String state;
	
	public MarketMessage(String state) throws InvalidParameterException
	{
		this.setState(state);
	}
	
	private void setState(String state) throws InvalidParameterException
	{
		if (state == null || state == "")
		{
			throw new InvalidParameterException("Market state cannot be null or empty");
		}
		
		state = state.toUpperCase().trim();
		
		if (state == "PREOPEN" || state == "OPEN" || state == "CLOSED")
		{
			this.state = state;
		}
		else
		{
			throw new InvalidParameterException("Market state must be PREOPEN, OPEN, or CLOSED");
		}
	}
	
	public String getState()
	{
		return state;
	}
	
	public String toString()
	{
		return String.format("Market State: %s", state);
	}
}
