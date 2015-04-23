package prices;

import java.util.HashMap;


public class PriceFactory 
{
	private static HashMap<Long, Price> prices = new HashMap<Long, Price>();
	
	public static Price makeLimitPrice(long value)
	{
		if (!prices.containsKey(value))
		{
			Price p = new Price(value);
			
			prices.put(value, p);
			
			return p;
		}
		
		return prices.get(value);
	}
	
	public static Price makeLimitPrice(String value)
	{
		value = value.replaceAll("[$,]", "");
		
		double doubleValue = Double.parseDouble(value);
		
		long longValue = (long)(doubleValue * 100);
		
		return makeLimitPrice(longValue);
	}
	
	public static Price makeMarketPrice()
	{
		if (!prices.containsKey(null))
		{
			Price p = new Price();
			
			prices.put(null, p);
			
			return p;
		}
		return prices.get(null);
	}
	
}
