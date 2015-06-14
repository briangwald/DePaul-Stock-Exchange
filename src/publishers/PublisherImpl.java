package publishers;

import java.util.HashMap;
import java.util.HashSet;

import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.NotSubscribedException;

public class PublisherImpl implements Publisher 
{
	private HashMap<String, HashSet<User>> subscribeList = new HashMap<String, HashSet<User>>();
		
	public HashMap<String, HashSet<User>> getSubscribeList()
	{
		return new HashMap<String, HashSet<User>>(subscribeList);
	}
		
	public synchronized void subscribe(User u, String product) throws AlreadySubscribedException
	{
		if (!subscribeList.containsKey(product))
		{
			HashSet<User> userList = new HashSet<User>();
			userList.add(u);
			subscribeList.put(product, userList);
			return;
		}
		
		HashSet<User> userList = subscribeList.get(product);
		
		if (userList.contains(u))
		{
			throw new AlreadySubscribedException(u, product);
		}
		else
		{
			userList.add(u);
		}
	}
	
	public synchronized void unSubscribe(User u, String product) throws NotSubscribedException
	{
		if (!subscribeList.containsKey(product))
		{
			throw new NotSubscribedException(u, product);
		}
		
		HashSet<User> userList = subscribeList.get(product);
		
		if(!userList.contains(u))
		{
			throw new NotSubscribedException(u, product);
		}
		else
		{
			userList.remove(u);
		}
	}
}
