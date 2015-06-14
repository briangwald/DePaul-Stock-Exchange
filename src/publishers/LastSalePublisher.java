package publishers;

import java.util.HashMap;
import java.util.HashSet;

import prices.Price;
import prices.PriceFactory;
import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.NotSubscribedException;

public final class LastSalePublisher implements Publisher
{	
	private Publisher delegate;
	
	private final static LastSalePublisher instance = new LastSalePublisher();
	
	private LastSalePublisher() 
	{
		delegate = PublisherFactory.build();
	}
	
	public static LastSalePublisher getInstance()
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
	
	public synchronized void publishLastSale(String product, Price p, int v)
	{
		HashSet<User> userList = getSubscribeList().get(product);
		
		if (userList == null) return;
		
		for(User u : userList)
		{	
			if (p == null)
			{
				p = PriceFactory.makeLimitPrice(0);
			}
			
			u.acceptLastSale(product, p, v);
		}
		
		TickerPublisher.getInstance().publishTicker(product, p);
	}
}
