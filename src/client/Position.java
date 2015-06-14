package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import exceptions.InvalidPriceOperation;
import prices.Price;
import prices.PriceFactory;

public class Position 
{
	private HashMap<String, Integer> holdings = new HashMap<>();
	
	private Price balance = PriceFactory.makeLimitPrice(0);
	
	private HashMap<String, Price> lastSales = new HashMap<>();
	
	public Position() { }
	
	public void updatePosition(String product, Price price, String side, int volume) throws InvalidPriceOperation
	{
		int adjustedVolume;
		
		if (side == "BUY")
		{
			adjustedVolume = volume;
		}
		else
		{
			adjustedVolume = -volume;
		}
		
		if (!holdings.containsKey(product))
		{
			holdings.put(product, adjustedVolume);
		}
		else
		{
			adjustedVolume += holdings.get(product);
			
			if (adjustedVolume == 0)
			{
				holdings.remove(product);
			}
			else
			{
				holdings.put(product, adjustedVolume);
			}
		}
		
		Price totalPrice = price.multiply(volume);
		
		if (side == "BUY")
		{
			balance = balance.subtract(totalPrice);
		}
		else
		{
			balance = balance.add(totalPrice);
		}
	}
	
	public void updateLastSale(String product, Price price)
	{
		lastSales.put(product, price);
	}
	
	public int getStockPositionVolume(String product)
	{
		if (!holdings.containsKey(product))
		{
			return 0;
		}
		
		return holdings.get(product);
	}
	
	public ArrayList<String> getHoldings()
	{
		ArrayList<String> symbols = new ArrayList<>(holdings.keySet());
		
		Collections.sort(symbols);
		
		return symbols;
	}
	
	public Price getStockPositionValue(String product) throws InvalidPriceOperation
	{
		if (!holdings.containsKey(product))
		{
			return PriceFactory.makeLimitPrice(0);
		}
		
		Price lastSalePrice = lastSales.get(product);
		
		if (lastSalePrice == null)
		{
			lastSalePrice = PriceFactory.makeLimitPrice(0);
		}
		
		Price positionValue = lastSalePrice.multiply(holdings.get(product));
		
		return positionValue;
	}
	
	public Price getAccountCosts()
	{
		return balance;
	}
	
	public Price getAllStockValue() throws InvalidPriceOperation
	{
		Price allStockValues = PriceFactory.makeLimitPrice(0);
		
		for (String product : holdings.keySet())
		{
			allStockValues = allStockValues.add(getStockPositionValue(product));
		}
		
		return allStockValues;
	}
	
	public Price getNetAccountValue() throws InvalidPriceOperation
	{
		return balance.add(getAllStockValue());
	}
}
