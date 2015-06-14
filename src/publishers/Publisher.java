package publishers;

import java.util.HashMap;
import java.util.HashSet;

import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.NotSubscribedException;

public interface Publisher 
{
	public void subscribe(User u, String product) throws AlreadySubscribedException;
	
	public void unSubscribe(User u, String product) throws NotSubscribedException;

	public HashMap<String, HashSet<User>> getSubscribeList();
}
