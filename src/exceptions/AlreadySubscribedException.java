package exceptions;

import client.User;

@SuppressWarnings("serial")
public class AlreadySubscribedException extends Exception 
{
	public AlreadySubscribedException(User u, String product)
	{
		super(String.format("%s is already subscribed to %.", u.getUserName(), product));
	}
}
