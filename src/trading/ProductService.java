package trading;

import java.util.ArrayList;
import java.util.HashMap;

import exceptions.InvalidMarketStateTransitionException;
import exceptions.InvalidParameterException;
import exceptions.OrderNotFoundException;
import messages.MarketMessage;
import publishers.MarketDataDTO;
import publishers.MessagePublisher;

public class ProductService 
{
	private static final ProductService instance = new ProductService();
	
	private HashMap<String, ProductBook> allBooks = new HashMap<String, ProductBook>();
	
	private String marketState = "CLOSED";
	
	private ProductService() { }
	
	public static ProductService getInstance()
	{
		return instance;
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, String product)
	{
		ProductBook pb = allBooks.get(product);
		
		return pb.getOrdersWithRemainingQty(userName);
	}
	
	public synchronized MarketDataDTO getMarketData(String product)
	{
		ProductBook pb = allBooks.get(product);
		
		return pb.getMarketData();
	}
	
	public synchronized String getMarketState()
	{
		return marketState;
	}
	
	public synchronized String[][] getBookDepth(String product) throws InvalidParameterException
	{
		if (!allBooks.containsKey(product))
		{
			throw new InvalidParameterException("Product " + product + " does not exist.");
		}
		
		ProductBook pb = allBooks.get(product);
		
		return pb.getBookDepth();
	}
	
	public synchronized ArrayList<String> getProductList()
	{
		return new ArrayList<String>(allBooks.keySet());
	}
	
	public synchronized void setMarketState(String ms) 
		throws InvalidParameterException, InvalidMarketStateTransitionException, OrderNotFoundException 
	{
		if ((marketState == "CLOSED" && ms == "OPEN") ||
			(marketState == "PREOPEN" && ms == "CLOSED") ||
			(marketState == "OPEN" && ms == "PREOPEN"))
		{
			throw new InvalidMarketStateTransitionException("Cannot transition from " 
					+ marketState + " to " + ms + ".");
		}
		
		marketState = ms;
		
		MarketMessage mm = new MarketMessage(marketState);
		
		MessagePublisher.getInstance().publishMarketMessage(mm);
		
		if (marketState == "OPEN")
		{
			for (ProductBook pb: allBooks.values())
			{
				pb.openMarket();
			}
		}
		else if (marketState == "CLOSED")
		{
			for (ProductBook pb : allBooks.values())
			{
				pb.closeMarket();
			}
		}
	}
	
	public synchronized void createProduct(String product) 
			throws InvalidParameterException
	{
		if (product == null || product.isEmpty())
		{
			throw new InvalidParameterException("Product symbol cannot be empty or null.");
		}
		
		if (allBooks.containsKey(product))
		{
			throw new InvalidParameterException("Product " + product + " already exists.");
		}
		
		ProductBook pb = new ProductBook(product);
		
		allBooks.put(product, pb);
	}
	
	public synchronized void submitQuote(Quote q) throws InvalidParameterException
	{
		if (marketState == "CLOSED")
		{
			throw new InvalidParameterException("Market is currently closed.");
		}
		
		if (!allBooks.containsKey(q.getProduct()))
		{
			throw new InvalidParameterException("Product " + q.getProduct() + " does not exist.");
		}
		
		ProductBook pb = allBooks.get(q.getProduct());
		
		pb.addToBook(q);
	}
	
	public synchronized String submitOrder(Order o) throws InvalidParameterException
	{
		if (marketState == "CLOSED")
		{
			throw new InvalidParameterException("Market is currently closed.");
		}
		
		if (marketState == "PREOPEN" && o.getPrice().isMarket())
		{
			throw new InvalidParameterException("Market orders cannot be submitted during PREOPEN.");
		}
		
		if (!allBooks.containsKey(o.getProduct()))
		{
			throw new InvalidParameterException("Product " + o.getProduct() + " does not exist.");
		}
		
		ProductBook pb = allBooks.get(o.getProduct());
		
		pb.addToBook(o);
		
		return o.getId();
	}
	
	public synchronized void submitOrderCancel(String product, String side, String orderId) 
			throws InvalidParameterException, OrderNotFoundException
	{
		if (marketState == "CLOSED")
		{
			throw new InvalidParameterException("Market is currently closed.");
		}
		
		if (!allBooks.containsKey(product))
		{
			throw new InvalidParameterException("Product " + product + " does not exist.");
		}
		
		ProductBook pb = allBooks.get(product);
		
		pb.cancelOrder(side, orderId);
	}
	
	public synchronized void submitQuoteCancel(String userName, String product) 
			throws InvalidParameterException
	{
		if (marketState == "CLOSED")
		{
			throw new InvalidParameterException("Market is currently closed.");
		}
		
		if (!allBooks.containsKey(product))
		{
			throw new InvalidParameterException("Product " + product + " does not exist.");
		}
		
		ProductBook pb = allBooks.get(product);
		
		pb.cancelQuote(userName);
	}
}
