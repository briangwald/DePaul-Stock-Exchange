package trading;

import prices.Price;
import exceptions.InvalidParameterException;


public interface Tradable 
{
	public String getProduct();
	
	public Price getPrice();
	
	public int getOriginalVolume();
	
	public int getRemainingVolume();
	
	public int getCancelledVolume();
	
	public void setCancelledVolume(int newCancelledVolume) throws InvalidParameterException;
	
	public void setRemainingVolume(int newRemainingVolume) throws InvalidParameterException;
			
	public String getUser();
	
	public String getSide();
		
	public String getId();
	
	public boolean isQuote();

	public String toString();
}
