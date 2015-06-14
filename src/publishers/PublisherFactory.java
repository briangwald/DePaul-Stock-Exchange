package publishers;

public class PublisherFactory 
{
	public static Publisher build()
	{
		return new PublisherImpl();
	}
}
