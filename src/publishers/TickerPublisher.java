package publishers;

import java.util.HashMap;
import java.util.HashSet;

import prices.Price;
import prices.PriceFactory;
import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.NotSubscribedException;

public final class TickerPublisher implements Publisher 
{	
	private Publisher delegate;
	
	private HashMap<String, Price> lastTradePrices = new HashMap<String, Price>();
	
	private static final TickerPublisher instance = new TickerPublisher();
	
	private TickerPublisher() 
	{
		delegate = PublisherFactory.build();
	}
	
	public static TickerPublisher getInstance()
	{
		return instance;
	}
	
	public synchronized void subscribe(User u, String product) throws AlreadySubscribedException
	{
		delegate.subscribe(u, product);
	}
	
	public synchronized void unSubscribe(User u, String product) throws NotSubscribedException
	{
		delegate.unSubscribe(u, product);
	}
	
	public HashMap<String, HashSet<User>> getSubscribeList()
	{
		return delegate.getSubscribeList();
	}
	
	public synchronized void publishTicker(String product, Price p)
	{
		char direction = ' ';
		
		if (lastTradePrices.containsKey(product))
		{
			Price lastTradePrice = lastTradePrices.remove(product);
			
			switch(p.compareTo(lastTradePrice))
			{
			case 0:
				direction = '=';
				break;
				
			case 1:
				direction = (char)8593;
				break;
				
			case -1:
				direction = (char)8595;
				break;
			}
		}
				
		lastTradePrices.put(product, p);
		
		HashSet<User> userList = getSubscribeList().get(product);
		
		if (userList == null) return;
		
		for (User u : userList)
		{
			u.acceptTicker(product, p, direction);
		}
	}
}
