package exceptions;

import client.User;

@SuppressWarnings("serial")
public class NotSubscribedException extends Exception 
{
	public NotSubscribedException(User u, String product)
	{
		super(String.format("User %s is already subscribed to %s", u.getUserName(), product));
	}
}
