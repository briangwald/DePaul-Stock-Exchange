package trading;

@SuppressWarnings("serial")
public class InvalidCancelledVolume extends Exception 
{
	public InvalidCancelledVolume(int newCancelledVolume, int remainingVolume, int originalVolume)
	{
		super(String.format("Requested new Cancelled Volume (%d) plus the Remaining Volume (%d) "
				+ "exceeds the tradable's Original Volume (%d)", newCancelledVolume, remainingVolume, originalVolume));
	}
}
