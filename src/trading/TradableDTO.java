package trading;

import prices.Price;


public class TradableDTO
{
	public String product;
	
	public Price price;
	
	public int originalVolume;
	
	public int remainingVolume;
	
	public int cancelledVolume;
	
	public String userName;
	
	public String side;
	
	public boolean isQuote;
	
	public String id;
	
	public TradableDTO(String product, Price price, int originalVolume, int remainingVolume, int cancelledVolume, 
			String userName, String side, boolean isQuote, String id)
			{
				this.product = product;
				this.price = price;
				this.originalVolume = originalVolume;
				this.remainingVolume = remainingVolume;
				this.cancelledVolume = cancelledVolume;
				this.userName = userName;
				this.side = side;
				this.isQuote = isQuote;
				this.id = id;
			}

	public String toString()
	{
		return String.format("Product: %s, Price: %s, OriginalVolume: %d, RemainingVolume: %d, CancelledVolume: %d "
				+ "User: %s, Side: %s, IsQuote: %b, Id: %s", product, price, originalVolume, remainingVolume,
				cancelledVolume, userName, side, isQuote, id);				
	}
}
