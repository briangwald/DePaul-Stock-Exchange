package trading;
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
	
	public Order(String userName, String productSymbol, Price orderPrice, int originalVolume, String side) throws InvalidOrderVolume
	{
		if (originalVolume <= 0)
		{
			throw new InvalidOrderVolume(originalVolume);
		}
		
		this.userName = userName;
		this.productSymbol = productSymbol;
		this.orderPrice = orderPrice;
		this.originalVolume = originalVolume;
		this.remainingVolume = originalVolume;
		this.side = side;
		this.id = userName + productSymbol + orderPrice.toString() + System.nanoTime();
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
	public void setCancelledVolume(int newCancelledVolume) throws InvalidCancelledVolume 
	{
		if (newCancelledVolume > this.originalVolume || newCancelledVolume < 0)
		{
			throw new InvalidCancelledVolume(newCancelledVolume, this.remainingVolume, this.originalVolume);
		}
		
		this.cancelledVolume = newCancelledVolume;
	}

	@Override
	public void setRemainingVolume(int newRemainingVolume) throws InvalidRemainingVolume 
	{
		if (newRemainingVolume > this.originalVolume || newRemainingVolume < 0)
		{
			throw new InvalidRemainingVolume(newRemainingVolume, this.cancelledVolume, this.originalVolume);
		}
		
		this.remainingVolume = newRemainingVolume;
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
