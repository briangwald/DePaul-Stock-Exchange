package publishers;

import java.util.HashMap;
import java.util.HashSet;

import messages.CancelMessage;
import messages.FillMessage;
import messages.MarketMessage;
import prices.Price;
import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.NotSubscribedException;

public class MessagePublisher implements Publisher 
{	
	private Publisher delegate;
	
	private static MessagePublisher instance = new MessagePublisher();
	
	private MessagePublisher() 
	{
		delegate = PublisherFactory.build();
	}
	
	public static MessagePublisher getInstance()
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
	
	public synchronized void publishCancel(CancelMessage cm)
	{
		HashSet<User> userList = getSubscribeList().get(cm.getProduct());
		
		if (userList == null) return;
				
		for (User u : userList)
		{
			if (u.getUserName() == cm.getUser())
			{
				u.acceptMessage(cm);
				return;
			}
		}
	}
	
	public synchronized void publishFill(FillMessage fm)
	{
		HashSet<User> userList = getSubscribeList().get(fm.getProduct());
		
		if (userList == null) return;
		
		for (User u : userList)
		{
			if (u.getUserName() == fm.getUser())
			{
				u.acceptMessage(fm);
				return;
			}
		}
	}
	
	public synchronized void publishMarketMessage(MarketMessage mm)
	{
		for (HashSet<User> userList : getSubscribeList().values())
		{
			for (User u : userList)
			{
				u.acceptMarketMessage(mm.toString());
			}
		}
	}
}
