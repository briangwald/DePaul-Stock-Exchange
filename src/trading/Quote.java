package trading;
import exceptions.InvalidParameterException;
import prices.Price;


public class Quote 
{	
	private String userName;
	
	private String productSymbol;
	
	private QuoteSide buySide;
	
	private QuoteSide sellSide;
	
	public Quote(String userName, String productSymbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) 
			throws InvalidParameterException
	{
		this.userName = userName;
		this.productSymbol = productSymbol;
		
		this.buySide = new QuoteSide(userName, productSymbol, buyPrice, buyVolume, "BUY");
		this.sellSide = new QuoteSide(userName, productSymbol, sellPrice, sellVolume, "SELL");

	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public String getProduct()
	{
		return productSymbol;
	}
	
	public QuoteSide getQuoteSide(String sideIn) throws InvalidParameterException
	{
		sideIn = sideIn.toUpperCase().trim();
		
		if (sideIn.equals("BUY"))
		{
			return new QuoteSide(buySide);
		}
		
		return new QuoteSide(sellSide);
	}
	
	public String toString()
	{
		String headerOutput = String.format("%s quote: %s ", this.userName, this.productSymbol);
		
		String buySideOutput = String.format("%s x %d (Original Vol: %d, CXL'd Vol: %d) [%s]", 
				buySide.getPrice().toString(), buySide.getRemainingVolume(), buySide.getOriginalVolume(),
				buySide.getCancelledVolume(), buySide.getId());
		
		String sellSideOutput = String.format("%s x %d (Original Vol: %d, CXL'd Vol: %d) [%s]",
				sellSide.getPrice().toString(), sellSide.getRemainingVolume(), sellSide.getOriginalVolume(),
				sellSide.getCancelledVolume(), sellSide.getId());
		
		return headerOutput + buySideOutput + " - " + sellSideOutput;	
	}

}
