package trading;

import prices.Price;


public class QuoteSide implements Tradable 
{
	private String userName;
	
	private String productSymbol;
	
	private String id;
	
	private String side;
	
	private Price sidePrice;
	
	private int originalVolume;
	
	private int remainingVolume;
	
	private int cancelledVolume;
	
	public QuoteSide(String userName, String productSymbol, Price sidePrice, int originalVolume, String side) throws InvalidOrderVolume
	{
		if (originalVolume <= 0)
		{
			throw new InvalidOrderVolume(originalVolume);
		}
		
		this.userName = userName;
		this.productSymbol = productSymbol;
		this.sidePrice = sidePrice;
		this.originalVolume = originalVolume;
		this.remainingVolume = originalVolume;
		this.side = side;
		this.id = userName + productSymbol + System.nanoTime();
	}
	
	public QuoteSide(QuoteSide qs)
	{
		this.userName = qs.userName;
		this.productSymbol = qs.productSymbol;
		this.sidePrice = qs.sidePrice;
		this.originalVolume = qs.originalVolume;
		this.remainingVolume = qs.remainingVolume;
		this.cancelledVolume = qs.cancelledVolume;
		this.side = qs.side;
		this.id = userName + productSymbol + System.nanoTime();
	}
	
	@Override
	public String getProduct() 
	{
		return productSymbol;
	}

	@Override
	public Price getPrice() 
	{
		return sidePrice;
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
		if (newCancelledVolume > this.cancelledVolume || newCancelledVolume < 0)
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
		return true;
	}
	
	public String toString()
	{
		return String.format("%s x %d (Original Vol: %d, CXL'd: %d) [%s]", 
				sidePrice.toString(), remainingVolume, originalVolume, cancelledVolume, id);
	}
	
}
