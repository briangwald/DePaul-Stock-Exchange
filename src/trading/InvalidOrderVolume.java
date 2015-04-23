package trading;

@SuppressWarnings("serial")
public class InvalidOrderVolume extends Exception 
{
	public InvalidOrderVolume(int originalVolume)
	{
		super(String.format("Invalid Order Volume: %d", originalVolume));
	}
}
