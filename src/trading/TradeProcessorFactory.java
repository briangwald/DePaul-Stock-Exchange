package trading;

public class TradeProcessorFactory 
{
	public static TradeProcessor build(ProductBookSide side)
	{
		return new TradeProcessorPriceTimeImpl(side);
	}
}
