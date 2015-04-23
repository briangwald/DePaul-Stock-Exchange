package trading;

@SuppressWarnings("serial")
public class InvalidRemainingVolume extends Exception 
{
	public InvalidRemainingVolume(int newRemainingVolume, int cancelledVolume, int originalVolume)
	{
		super(String.format("Requested new Remaining Volume (%d) plus the Cancelled Volume (%d) "
				+ "exceeds the tradable's Original Volume (%d)", newRemainingVolume, cancelledVolume, originalVolume));
	}
}
