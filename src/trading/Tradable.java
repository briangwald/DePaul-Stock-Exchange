package trading;

import prices.Price;


public interface Tradable 
{
	public String getProduct();
	
	public Price getPrice();
	
	public int getOriginalVolume();
	
	public int getRemainingVolume();
	
	public int getCancelledVolume();
	
	public void setCancelledVolume(int newCancelledVolume) throws InvalidCancelledVolume;
	
	public void setRemainingVolume(int newRemainingVolume) throws InvalidRemainingVolume;
	
	public String getUser();
	
	public String getSide();
		
	public String getId();
	
	public boolean isQuote();

	public String toString();
}
