package trading;
import exceptions.InvalidParameterException;
import prices.Price;


public class Order implements Tradable 
{
	private String userName;
	
	private String productSymbol;
	
	private String id;
	
	private String side;
	
	private Price orderPrice;
	
	private int originalVolume;
	
	private int remainingVolume;
	
	private int cancelledVolume;
	
	public Order(String userName, String productSymbol, Price orderPrice, int originalVolume, String side) 
			throws InvalidParameterException
			
	{
		this.setUserName(userName);
		this.setProductSymbol(productSymbol);
		this.setPrice(orderPrice);
		this.setOriginalVolume(originalVolume);
		this.setRemainingVolume(originalVolume);
		this.setSide(side);
		this.setId(userName + productSymbol + orderPrice.toString() + System.nanoTime());
	}
	
	@Override
	public String getProduct() 
	{
		return productSymbol;
	}

	@Override
	public Price getPrice() 
	{
		return orderPrice;
	}
	
	@Override
	public int getOriginalVolume() 
	{
		return originalVolume;
	}

	@Override
	public int getRemainingVolume() 
	{
		return remainingVolume;
	}

	@Override
	public int getCancelledVolume() 
	{
		return cancelledVolume;
	}

	@Override
	public void setCancelledVolume(int newCancelledVolume) throws InvalidParameterException 
	{
		if (newCancelledVolume > this.originalVolume || newCancelledVolume < 0)
		{
			throw new InvalidParameterException(String.format("Requested new Cancelled Volume (%d) plus the Remaining Volume (%d) "
					+ "exceeds the tradable's Original Volume (%d)", newCancelledVolume, remainingVolume, originalVolume));
		}
		
		this.cancelledVolume = newCancelledVolume;
	}

	@Override
	public void setRemainingVolume(int newRemainingVolume) throws InvalidParameterException 
	{
		if (newRemainingVolume > this.originalVolume || newRemainingVolume < 0)
		{
			throw new InvalidParameterException(String.format("Requested new Remaining Volume (%d) plus the Cancelled Volume (%d) "
					+ "exceeds the tradable's Original Volume (%d)", newRemainingVolume, cancelledVolume, originalVolume));
		}
		
		this.remainingVolume = newRemainingVolume;
	}
	
	private void setOriginalVolume(int originalVolume) throws InvalidParameterException
	{
		if (originalVolume <= 0)
		{
			throw new InvalidParameterException(String.format("Invalid Order Volume: %d", originalVolume));
		}
		
		this.originalVolume = originalVolume;
	}
	
	
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
	
	private void setSide(String side) throws InvalidParameterException
	{
		side = side.toUpperCase().trim();
		
		if (side == null || side == "" || (side != "BUY" && side != "SELL"))
		{
			throw new InvalidParameterException("Side must be \"BUY\" or \"SELL\"");
		}
		
		this.side = side;
	}
	
	private void setPrice(Price price) throws InvalidParameterException
	{
		if (price == null)
		{
			throw new InvalidParameterException("Price must be a valid Market or Limit Price");
		}
		
		this.orderPrice = price;
	}
	
	private void setId(String id) throws InvalidParameterException
	{
		if (id == null)
		{
			throw new InvalidParameterException("Id cannot be null");
		}
		
		this.id = id;
	}

	@Override
	public String getUser() 
	{
		return userName;
	}

	@Override
	public String getSide() 
	{
		return side;
	}

	@Override
	public String getId() 
	{
		return id;
	}
	
	@Override
	public boolean isQuote() 
	{
		return false;
	}
	
	public String toString()
	{
		return String.format("%s order: %s %d %s at %s (Original Vol: %d, CXL'd: %d), ID: %s", 
				userName, side, remainingVolume, productSymbol, orderPrice.toString(), originalVolume, cancelledVolume, id);
	}	

}
