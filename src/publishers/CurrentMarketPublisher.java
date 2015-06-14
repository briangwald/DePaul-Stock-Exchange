package publishers;

import java.util.HashMap;
import java.util.HashSet;

import prices.Price;
import prices.PriceFactory;
import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.NotSubscribedException;

public final class CurrentMarketPublisher implements Publisher
{	
	private Publisher delegate;
	
	private static final CurrentMarketPublisher instance = new CurrentMarketPublisher();
	
	private CurrentMarketPublisher() 
	{
		delegate = PublisherFactory.build();
	}
	
	public static CurrentMarketPublisher getInstance()
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
	
	public synchronized void publishCurrentMarket(MarketDataDTO md)
	{
		HashSet<User> userList = getSubscribeList().get(md.product);
		
		if(userList == null) return;
		
		for(User u : userList)
		{
			Price buyPrice = md.buyPrice;
			if (buyPrice == null)
			{
				buyPrice = PriceFactory.makeLimitPrice(0);
			}
			
			Price sellPrice = md.sellPrice;
			if (sellPrice == null)
			{
				sellPrice = PriceFactory.makeLimitPrice(0);
			}
			
			u.acceptCurrentMarket(md.product, buyPrice, md.buyVolume, sellPrice, md.sellVolume);
		}
	}
}
