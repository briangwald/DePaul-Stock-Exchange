package trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import exceptions.InvalidParameterException;
import exceptions.OrderNotFoundException;
import messages.CancelMessage;
import messages.FillMessage;
import prices.Price;
import prices.PriceFactory;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MarketDataDTO;
import publishers.MessagePublisher;

public class ProductBook 
{
	private String symbol;
	
	private ProductBookSide buySide;
	
	private ProductBookSide sellSide;
	
	private String lastCurrentMarket = "";
	
	private HashSet<String> userQuotes = new HashSet<String>();
	
	private HashMap<Price, ArrayList<Tradable>> oldEntries = new HashMap<Price, ArrayList<Tradable>>();

	public ProductBook(String symbol) throws InvalidParameterException
	{
		setSymbol(symbol);
		
		buySide = new ProductBookSide(this, "BUY");
		sellSide = new ProductBookSide(this, "SELL");
	}

	private void setSymbol(String symbol) throws InvalidParameterException 
	{
		if (symbol == null || symbol == "")
		{
			throw new InvalidParameterException("Symbols cannot be empty or null");
		}

		this.symbol = symbol.toUpperCase().trim();		
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName)
	{
		ArrayList<TradableDTO> orders = new ArrayList<TradableDTO>();
		
		ArrayList<TradableDTO> buyOrders = buySide.getOrdersWithRemainingQty(userName);
		
		if (buyOrders != null)
		{
			orders.addAll(buySide.getOrdersWithRemainingQty(userName));
		}
		
		ArrayList<TradableDTO> sellOrders = sellSide.getOrdersWithRemainingQty(userName);
		
		if (sellOrders != null)
		{
			orders.addAll(sellSide.getOrdersWithRemainingQty(userName));
		}
		
		return orders;
	}
	
	public synchronized void checkTooLateToCancel(String orderId) throws InvalidParameterException, OrderNotFoundException
	{
		for (ArrayList<Tradable> entries : oldEntries.values())
		{
			for (Tradable t : entries)
			{
				if (t.getId() == orderId)
				{
					CancelMessage cm = new CancelMessage(t.getUser(), t.getProduct(), t.getPrice(), 
							t.getRemainingVolume(), "Too Late to Cancel", t.getSide(), t.getId());
					
					MessagePublisher.getInstance().publishCancel(cm);
					return;
				}
			}
		}
		throw new OrderNotFoundException("Requested order" + orderId + "could not be found");
	}
	
	public synchronized String[][] getBookDepth()
	{
		String[][] bookDepth = new String[2][];
		
		bookDepth[0] = buySide.getBookDepth();
		bookDepth[1] = sellSide.getBookDepth();
		
		return bookDepth;
	}
	
	public synchronized MarketDataDTO getMarketData()
	{
		Price bestBuyPrice = buySide.topOfBookPrice();
		
		if (bestBuyPrice == null)
		{
			bestBuyPrice = PriceFactory.makeLimitPrice(0);
		}
		
		Price bestSellPrice = sellSide.topOfBookPrice();
		
		if (bestSellPrice == null)
		{
			bestSellPrice = PriceFactory.makeLimitPrice(0);
		}
		
		int bestBuyVolume = buySide.topOfBookVolume();
		
		int bestSellVolume = sellSide.topOfBookVolume();
		
		return new MarketDataDTO(symbol, bestBuyPrice, bestBuyVolume, bestSellPrice, bestSellVolume);
	}
	
	public synchronized void addOldEntry(Tradable t) throws InvalidParameterException
	{
		if (!oldEntries.containsKey(t.getPrice()))
		{
			ArrayList<Tradable> entry = new ArrayList<Tradable>();
			
			oldEntries.put(t.getPrice(), entry);
		}
		
		t.setCancelledVolume(t.getRemainingVolume());
		t.setRemainingVolume(0);
		
		oldEntries.get(t.getPrice()).add(t);
	}
	
	public synchronized void openMarket() throws InvalidParameterException
	{
		Price bestBuyPrice = buySide.topOfBookPrice();
		Price bestSellPrice = sellSide.topOfBookPrice();
		
		if (bestBuyPrice == null || bestSellPrice == null)
		{
			return;
		}
		
		while (bestBuyPrice.greaterOrEqual(bestSellPrice) || bestBuyPrice.isMarket() 
				|| bestSellPrice.isMarket())
		{
			ArrayList<Tradable> topOfBuySide = buySide.getEntriesAtPrice(bestBuyPrice);
			
			HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
			
			ArrayList<Tradable> toRemove = new ArrayList<Tradable>();
			
			for (Tradable t : topOfBuySide)
			{
				allFills = sellSide.tryTrade(t);
				
				if (t.getRemainingVolume() == 0)
				{
					toRemove.add(t);
				}
			}
			
			for (Tradable t : toRemove)
			{
				buySide.removeTradable(t);
			}
			
			updateCurrentMarket();
			
			Price lastSalePrice = determineLastSalePrice(allFills);
			
			int lastSaleVolume = determineLastSaleQuantity(allFills);
			
			LastSalePublisher.getInstance().publishLastSale(symbol, lastSalePrice, lastSaleVolume);
			
			bestBuyPrice = buySide.topOfBookPrice();
			bestSellPrice = sellSide.topOfBookPrice();
			
			if (bestBuyPrice == null || bestSellPrice == null) break;
		}
	}
	
	public synchronized void closeMarket() throws InvalidParameterException, OrderNotFoundException
	{
		buySide.cancelAll();
		sellSide.cancelAll();
		
		updateCurrentMarket();
	}
	
	public synchronized void cancelOrder(String side, String orderId) throws InvalidParameterException, OrderNotFoundException
	{
		if (side == "BUY")
		{
			buySide.submitOrderCancel(orderId);
		}
		else
		{
			sellSide.submitOrderCancel(orderId);
		}
		
		updateCurrentMarket();
	}
	
	public synchronized void cancelQuote(String userName) throws InvalidParameterException
	{
		buySide.submitQuoteCancel(userName);
		sellSide.submitQuoteCancel(userName);
		
		updateCurrentMarket();
	}
	
	public synchronized void addToBook(Quote q) throws InvalidParameterException
	{
		QuoteSide quoteBuySide = q.getQuoteSide("BUY");
		QuoteSide quoteSellSide = q.getQuoteSide("SELL");
		
		if (quoteSellSide.getPrice().lessOrEqual(quoteBuySide.getPrice()))
		{
			throw new InvalidParameterException("Sell price cannot be less than or equal to the Buy price");
		}
		
		if (quoteSellSide.getPrice().lessOrEqual(PriceFactory.makeLimitPrice(0)) 
				|| quoteBuySide.getPrice().lessOrEqual(PriceFactory.makeLimitPrice(0)))
		{
			throw new InvalidParameterException("Sell and Buy price cannot be less than or equal to 0");
		}
		
		if(quoteSellSide.getOriginalVolume() <= 0 || quoteBuySide.getOriginalVolume() <= 0)
		{
			throw new InvalidParameterException("Sell and Buy side volume cannot be less than or equal to 0");
		}
		
		if (userQuotes.contains(q.getUserName()))
		{
			buySide.removeQuote(q.getUserName());
			sellSide.removeQuote(q.getUserName());
			
			updateCurrentMarket();
		}
		
		addToBook("BUY", quoteBuySide);
		addToBook("SELL", quoteSellSide);
		
		userQuotes.add(q.getUserName());
		
		updateCurrentMarket();		
	}
	
	public synchronized void addToBook(Order o) throws InvalidParameterException
	{
		addToBook(o.getSide(), o);
		
		updateCurrentMarket();
	}
	
	public synchronized void updateCurrentMarket()
	{
		String topBuySidePrice;
		String topSellSidePrice;
		
		if (buySide.topOfBookPrice() == null) 
		{
			topBuySidePrice = "0";
		}
		else
		{
			topBuySidePrice = buySide.topOfBookPrice().toString();
		}
		
		if (sellSide.topOfBookPrice() == null) 
		{
			topSellSidePrice = "0";
		}
		else
		{
			topSellSidePrice = sellSide.topOfBookPrice().toString();
		}
		
		String currentMarket = topBuySidePrice + buySide.topOfBookVolume() +
				topSellSidePrice + sellSide.topOfBookVolume();
		
		if (!lastCurrentMarket.equals(currentMarket))
		{
			MarketDataDTO marketData = new MarketDataDTO(symbol, buySide.topOfBookPrice(), 
					buySide.topOfBookVolume(), sellSide.topOfBookPrice(), sellSide.topOfBookVolume());
			CurrentMarketPublisher.getInstance().publishCurrentMarket(marketData);
			
			lastCurrentMarket = currentMarket;
		}
	}
	
	private synchronized Price determineLastSalePrice(HashMap<String, FillMessage> fills)
	{
		ArrayList<FillMessage> fillMessages = new ArrayList<FillMessage>(fills.values());
		
		Collections.sort(fillMessages);
		
		return fillMessages.get(0).getPrice();
	}
	
	private synchronized int determineLastSaleQuantity(HashMap<String, FillMessage> fills)
	{
		ArrayList<FillMessage> fillMessages = new ArrayList<FillMessage>(fills.values());
		
		Collections.sort(fillMessages);
		
		return fillMessages.get(0).getVolume();
	}
	
	private synchronized void addToBook(String side, Tradable t) throws InvalidParameterException
	{
		String marketState = ProductService.getInstance().getMarketState();
		
		if (marketState == "PREOPEN")
		{
			if (side == "BUY")
			{
				buySide.addToBook(t);
			}
			else
			{
				sellSide.addToBook(t);
			}
			return;
		}
		
		HashMap<String, FillMessage> allFills = null;
		
		if (side == "BUY")
		{
			allFills = sellSide.tryTrade(t);
		}
		else
		{
			allFills = buySide.tryTrade(t);
		}
		
		if (!allFills.isEmpty() && allFills != null)
		{
			updateCurrentMarket();
			
			int volumeDifference = t.getOriginalVolume() - t.getRemainingVolume();
			
			Price lastSalePrice = determineLastSalePrice(allFills);
			
			LastSalePublisher.getInstance().publishLastSale(symbol, lastSalePrice, volumeDifference);
		}
		
		if (t.getRemainingVolume() > 0)
		{
			if (t.getPrice().isMarket())
			{
				CancelMessage cm = new CancelMessage(t.getUser(), t.getProduct(), t.getPrice(), 
						t.getRemainingVolume(), "Cancelled", t.getSide(), t.getId());
			
				MessagePublisher.getInstance().publishCancel(cm);
			}
			else
			{
				if (side == "BUY")
				{
					buySide.addToBook(t);
				}
				else
				{
					sellSide.addToBook(t);
				}
			}
		}
	
	}
}
