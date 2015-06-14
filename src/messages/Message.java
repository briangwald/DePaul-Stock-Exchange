package messages;

import prices.Price;
import exceptions.InvalidParameterException;

public abstract class Message 
{
	public Message(String userName, String productSymbol, Price price, int volume, String details, String side, String id) 
			throws InvalidParameterException
	{
		this.setUserName(userName);
		this.setProductSymbol(productSymbol);
		this.setPrice(price);
		this.setVolume(volume);
		this.setDetails(details);
		this.setSide(side);
		this.setId(id);
	}
	
	private String userName;
	
	private String productSymbol;
	
	private Price price;
	
	private int volume;
	
	private String details;
	
	private String side;
	
	private String id;
	
	private void setUserName(String userName) throws InvalidParameterException
	{
		if (userName == null || userName == "")
		{
			throw new InvalidParameterException("User names cannot be empty or null");
		}

		this.userName = userName.toUpperCase().trim();
		
	}
	
	private void setProductSymbol(String productSymbol) throws InvalidParameterException
	{
		if (productSymbol == null || productSymbol == "")
		{
			throw new InvalidParameterException("Product symbols cannot be empty or null");
		}

		this.productSymbol = productSymbol.toUpperCase().trim();
	}
	
	private void setPrice(Price price) throws InvalidParameterException
	{
		if (price == null)
		{
			throw new InvalidParameterException("Price cannot be null");
		}
		
		this.price = price;
	}
	
	public void setVolume(int volume) throws InvalidParameterException
	{
		if (volume < 0)
		{
			throw new InvalidParameterException("Volume cannot be less than 0");
		}
		
		this.volume = volume;
	}
	
	public void setDetails(String details) throws InvalidParameterException
	{
		if (details == null)
		{
			throw new InvalidParameterException("Details cannot be null");
		}
		
		this.details = details;
	}
	
	private void setSide(String side) throws InvalidParameterException
	{
		side = side.toUpperCase().trim();
		
		if (side == null || side == "" || (side != "BUY" && side != "SELL"))
		{
			throw new InvalidParameterException("Side must be \"BUY\" or \"SELL\"");
		}
		
		this.side = side;
	}
	
	private void setId(String id) throws InvalidParameterException
	{
		if (id == null)
		{
			throw new InvalidParameterException("Id cannot be null");
		}
		
		this.id = id;
	}
	
	public String getUser()
	{
		return userName;
	}
	
	public String getProduct()
	{
		return productSymbol;
	}
	
	public Price getPrice()
	{
		return price;
	}
	
	public int getVolume()
	{
		return volume;
	}
	
	public String getDetails()
	{
		return details;
	}
	
	public String getSide()
	{
		return side;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String toString()
	{
		return String.format("User: %s, Product: %s, Price: %s, Volume: %d, Details: %s, "
				+ "Side: %s, Id: %s", userName, productSymbol, price.toString(), volume,
				details, side, id);
	}
}
