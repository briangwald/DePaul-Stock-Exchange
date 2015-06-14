package trading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import exceptions.InvalidParameterException;
import exceptions.OrderNotFoundException;
import messages.CancelMessage;
import messages.FillMessage;
import prices.Price;
import publishers.MessagePublisher;

public class ProductBookSide 
{
	private String bookSide;
	
	private HashMap<Price, ArrayList<Tradable>> bookEntries = new HashMap<Price, ArrayList<Tradable>>();

	private TradeProcessor processor;
	
	private ProductBook parentBook;
	
	public ProductBookSide(ProductBook parent, String side) throws InvalidParameterException
	{
		setParent(parent);
		setSide(side);
		
		processor = TradeProcessorFactory.build(this);
	}
	
	private void setParent(ProductBook parent) throws InvalidParameterException
	{
		if (parent == null)
		{
			throw new InvalidParameterException("Parent Product Book cannot be nulll.");
		}
		
		parentBook = parent;
	}
	
	private void setSide(String side) throws InvalidParameterException
	{
		side = side.toUpperCase().trim();
		
		if (side == null || side == "" || (side != "BUY" && side != "SELL"))
		{
			throw new InvalidParameterException("Side must be \"BUY\" or \"SELL\"");
		}
		bookSide = side;
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName)
	{
		if (bookEntries.isEmpty()) return null;
		
		ArrayList<TradableDTO> orders = new ArrayList<TradableDTO>();
		
		for (ArrayList<Tradable> entries : bookEntries.values())
		{
			for (Tradable t : entries)
			{
				if (t.getUser() == userName && t.getRemainingVolume() > 0 && !t.isQuote())
				{
					orders.add(new TradableDTO(t.getProduct(), t.getPrice(), t.getOriginalVolume(), 
							t.getRemainingVolume(), t.getCancelledVolume(), t.getUser(), t.getSide(),
							t.isQuote(), t.getId()));
				}
			}
		}
		
		return orders;
	}
	
	synchronized ArrayList<Tradable> getEntriesAtTopOfBook()
	{
		if (bookEntries.isEmpty()) return null;
		
		ArrayList<Price> prices = new ArrayList<Price>(bookEntries.keySet());
		
		Collections.sort(prices);
		
		if (bookSide == "BUY")
		{
			Collections.reverse(prices);
		}
		
		return bookEntries.get(prices.get(0));
	}
	
	public synchronized String[] getBookDepth()
	{
		if (bookEntries.isEmpty())
		{
			return new String[] { "<Empty>" };
		}
		
		String[] strArray = new String[bookEntries.size()];
		
		ArrayList<Price> prices = new ArrayList<Price>(bookEntries.keySet());
		
		Collections.sort(prices);
		
		if (bookSide == "BUY")
		{
			Collections.reverse(prices);
		}
		
		int index = 0;
		
		for (Price p : prices)
		{
			int volumeSum = 0;
			
			for (Tradable t : bookEntries.get(p))
			{
				volumeSum += t.getRemainingVolume();
			}
			
			strArray[index] = p + " x " + volumeSum;
			index++;
		}
		
		return strArray;
	}
	
	synchronized ArrayList<Tradable> getEntriesAtPrice(Price price)
	{
		if (!bookEntries.containsKey(price)) return null;
		
		return bookEntries.get(price);
	}
	
	synchronized boolean hasMarketPrice()
	{
		if (bookEntries.containsKey(null)) return true;
		
		return false;
	}
	
	synchronized boolean hasOnlyMarketPrice()
	{
		if (bookEntries.size() == 1 && bookEntries.containsKey(null)) return true;
		
		return false;
	}
	
	public synchronized Price topOfBookPrice()
	{
		if (bookEntries.isEmpty()) return null;
		
		ArrayList<Price> prices = new ArrayList<Price>(bookEntries.keySet());
		
		Collections.sort(prices);
		
		if (bookSide == "BUY")
		{
			Collections.reverse(prices);
		}
		
		return prices.get(0);
	}
	
	public synchronized int topOfBookVolume()
	{
		if (bookEntries.isEmpty()) return 0;
		
		ArrayList<Price> prices = new ArrayList<Price>(bookEntries.keySet());
		
		Collections.sort(prices);
		
		if (bookSide == "BUY")
		{
			Collections.reverse(prices);
		}
		
		ArrayList<Tradable> topOfBook = bookEntries.get(prices.get(0));
		
		int volumeSum = 0;
		
		for (Tradable t : topOfBook)
		{
			volumeSum += t.getRemainingVolume();
		}
		
		return volumeSum;		
	}
	
	public synchronized boolean isEmpty()
	{
		if (bookEntries.isEmpty()) return true;
		
		return false;
	}
	
	public synchronized void cancelAll() throws InvalidParameterException, OrderNotFoundException
	{
		ArrayList<ArrayList<Tradable>> bookValues =  new ArrayList<ArrayList<Tradable>>();
		
		for (ArrayList<Tradable> entries : bookEntries.values())
		{
			bookValues.add(new ArrayList<Tradable>(entries));
		}
		
		for (ArrayList<Tradable> entries : bookValues)
		{

			for (Tradable t : entries)
			{
				if (t.isQuote())
				{
					submitQuoteCancel(t.getUser());
				}
				else
				{
					submitOrderCancel(t.getId());
					
				}
			}
		}
	}
	
	public synchronized TradableDTO removeQuote(String user)
	{
		for (ArrayList<Tradable> entries : bookEntries.values())
		{
			for (Tradable t : entries)
			{
				if (t.isQuote() && t.getUser() == user)
				{
					TradableDTO quoteDTO = new TradableDTO(t.getProduct(), t.getPrice(), t.getOriginalVolume(), 
							t.getRemainingVolume(), t.getCancelledVolume(), t.getUser(), t.getSide(),
							t.isQuote(), t.getId());
					
					entries.remove(t);
					
					if (entries.isEmpty())
					{
						bookEntries.remove(t.getPrice());
					}
					
					return quoteDTO;
				}
			}
		}
		
		return null;
	}
	
	public synchronized void submitOrderCancel(String orderId) throws InvalidParameterException, OrderNotFoundException
	{				
		for (ArrayList<Tradable> entries : bookEntries.values())
		{
			for (Tradable t : entries)
			{
				if (t.getId() == orderId)
				{
					String details = t.getSide() + " Order Cancelled";
					
					CancelMessage cm = new CancelMessage(t.getUser(), t.getProduct(), t.getPrice(), 
							t.getRemainingVolume(), details, t.getSide(), t.getId());
					
					MessagePublisher.getInstance().publishCancel(cm);
					
					addOldEntry(t);
					
					entries.remove(t);
					
					if(entries.isEmpty())
					{
						bookEntries.remove(t.getPrice());
					}
					
					return;
				}
			}
		}
		
		parentBook.checkTooLateToCancel(orderId);
	}
	
	public synchronized void submitQuoteCancel(String userName) throws InvalidParameterException
	{
		TradableDTO quoteDTO = removeQuote(userName);
		
		if (quoteDTO == null) return;
		
		String details = "Quote " + quoteDTO.side + "-Side Cancelled";
		
		CancelMessage cm = new CancelMessage(quoteDTO.userName, quoteDTO.product, quoteDTO.price,
				quoteDTO.remainingVolume, details, quoteDTO.side, quoteDTO.id);
		
		MessagePublisher.getInstance().publishCancel(cm);
	}
	
	public void addOldEntry(Tradable t) throws InvalidParameterException
	{
		parentBook.addOldEntry(t);
	}
	
	public synchronized void addToBook(Tradable t)
	{
		Price p = t.getPrice();
		
		if (!bookEntries.containsKey(p))
		{
			ArrayList<Tradable> entry = new ArrayList<Tradable>();
			bookEntries.put(p, entry);			
		}
		bookEntries.get(p).add(t);
	}
	
	public HashMap<String, FillMessage> tryTrade(Tradable t) throws InvalidParameterException
	{
		HashMap<String, FillMessage> allFills;
		
		if (bookSide == "BUY")
		{
			allFills = trySellAgainstBuySideTrade(t);
		}
		else
		{
			allFills = tryBuyAgainstSellSideTrade(t);
		}
		
		for (FillMessage fillMessage : allFills.values())
		{
			MessagePublisher.getInstance().publishFill(fillMessage);
		}
		
		return allFills;
	}
	
	public synchronized HashMap<String, FillMessage> trySellAgainstBuySideTrade(Tradable t) 
			throws  InvalidParameterException
	{
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMessages = new HashMap<String, FillMessage>();
		
		while (t.getRemainingVolume() > 0 && !bookEntries.isEmpty() && 
				(t.getPrice().lessOrEqual(topOfBookPrice())	|| t.getPrice().isMarket()))
		{
			HashMap<String, FillMessage> tempFills = processor.doTrade(t);
			
			fillMessages = mergeFills(fillMessages, tempFills);
		}
		
		allFills.putAll(fillMessages);
		return allFills;
	}
	
	private HashMap<String, FillMessage> mergeFills(HashMap<String, FillMessage> existingFills,
			HashMap<String, FillMessage> newFills) throws InvalidParameterException
	{
		if (existingFills.isEmpty())
		{
			return new HashMap<String, FillMessage>(newFills);
		}
		
		HashMap<String, FillMessage> results = new HashMap<String, FillMessage>(existingFills);
		
		for (String key : newFills.keySet())
		{
			if (!existingFills.containsKey(key))
			{
				results.put(key, newFills.get(key));
			}
			else
			{
				FillMessage fm = results.get(key);
				
				fm.setVolume(newFills.get(key).getVolume());
				fm.setDetails(newFills.get(key).getDetails());
			}
		}
		
		return results;
	}
	
	public synchronized HashMap<String, FillMessage> tryBuyAgainstSellSideTrade(Tradable t) 
			throws InvalidParameterException 
	{
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMessages = new HashMap<String, FillMessage>();
		
		while (t.getRemainingVolume() > 0 && !bookEntries.isEmpty() &&
				(t.getPrice().greaterOrEqual(topOfBookPrice()) || t.getPrice().isMarket()))
		{
			HashMap<String, FillMessage> tempFills = processor.doTrade(t);
			
			fillMessages = mergeFills(fillMessages, tempFills);
		}
		
		allFills.putAll(fillMessages);
		
		return allFills;
	}
	
	public synchronized void clearIfEmpty(Price p)
	{
		ArrayList<Tradable> entries = bookEntries.get(p);
		
		if (entries.isEmpty())
		{
			bookEntries.remove(p);
		}
	}
	
	public synchronized void removeTradable(Tradable t)
	{
		ArrayList<Tradable> entries = bookEntries.get(t.getPrice());
		
		if (entries == null) return;
		
		boolean wasRemoved = entries.remove(t);
		
		if (!wasRemoved) return;
		
		if (entries.isEmpty())
		{
			clearIfEmpty(t.getPrice());
		}
	}
	
}
