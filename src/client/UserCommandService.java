package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import prices.Price;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MessagePublisher;
import publishers.TickerPublisher;
import trading.Order;
import trading.ProductService;
import trading.Quote;
import trading.TradableDTO;
import exceptions.AlreadySubscribedException;
import exceptions.InvalidParameterException;
import exceptions.NotSubscribedException;
import exceptions.OrderNotFoundException;
import exceptions.UserNotConnectedException;

public class UserCommandService 
{
	private HashMap<String, Long> connectedUserIds = new HashMap<>();
	
	private HashMap<String, User> connectedUsers = new HashMap<>();
	
	private HashMap<String, Long> connectedTime = new HashMap<>();
	
	private static final UserCommandService instance = new UserCommandService();
	
	private UserCommandService() { }
	
	public static UserCommandService getInstance()
	{
		return instance;
	}
	
	private void verifyUser(String userName, long connId) throws InvalidParameterException, UserNotConnectedException
	{
		if (!connectedUserIds.containsKey(userName))
		{
			throw new UserNotConnectedException("User is not connected.");
		}
		
		if (connectedUserIds.get(userName) != connId)
		{
			throw new InvalidParameterException("Connection ID is invalid.");
		}
	}
	
	public synchronized long connect(User user) throws InvalidParameterException
	{
		if (connectedUserIds.containsKey(user.getUserName()))
		{
			throw new InvalidParameterException("User is already connected");
		}
		
		long currentTime = System.nanoTime();
		
		connectedUserIds.put(user.getUserName(), currentTime);
		connectedUsers.put(user.getUserName(), user);
		connectedTime.put(user.getUserName(), System.currentTimeMillis());
		
		return currentTime;
	}
	
	public synchronized void disConnect(String userName, long connId) throws InvalidParameterException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		connectedUserIds.remove(userName);
		connectedUsers.remove(userName);
		connectedTime.remove(userName);
	}
	
	public String[][] getBookDepth(String userName, long connId, String product) throws InvalidParameterException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		return ProductService.getInstance().getBookDepth(product);
	}
	
	public String getMarketState(String userName, long connId) throws InvalidParameterException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		return ProductService.getInstance().getMarketState();
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, long connId, String product) throws InvalidParameterException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		return ProductService.getInstance().getOrdersWithRemainingQty(userName, product);
	}
	
	public ArrayList<String> getProducts(String userName, long connId) throws InvalidParameterException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		ArrayList<String> productList = ProductService.getInstance().getProductList();
		
		Collections.sort(productList);
		
		return productList;
	}
	
	public String submitOrder(String userName, long connId, String product, Price price, int volume, String side) throws InvalidParameterException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		Order o = new Order(userName, product, price, volume, side);
		
		String orderId = ProductService.getInstance().submitOrder(o);
		
		return orderId;
	}
	
	public void submitOrderCancel(String userName, long connId, String product, String side, String orderId) throws InvalidParameterException, OrderNotFoundException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		ProductService.getInstance().submitOrderCancel(product, side, orderId);
	}
	
	public void submitQuote(String userName, long connId, String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidParameterException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		Quote q = new Quote(userName, product, buyPrice, buyVolume, sellPrice, sellVolume);
		
		ProductService.getInstance().submitQuote(q);
	}
	
	public void submitQuoteCancel(String userName, long connId, String product) throws InvalidParameterException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		ProductService.getInstance().submitQuoteCancel(userName, product);
	}
	
	public void subscribeCurrentMarket(String userName, long connId, String product) throws AlreadySubscribedException, InvalidParameterException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		User u = connectedUsers.get(userName);
		
		CurrentMarketPublisher.getInstance().subscribe(u, product);
	}
	
	public void subscribeLastSale(String userName, long connId, String product) throws InvalidParameterException, AlreadySubscribedException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		User u = connectedUsers.get(userName);
		
		LastSalePublisher.getInstance().subscribe(u, product);
	}
	
	public void subscribeMessages(String userName, long connId, String product) throws InvalidParameterException, AlreadySubscribedException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		User u = connectedUsers.get(userName);
		
		MessagePublisher.getInstance().subscribe(u, product);
	}
	
	public void subscribeTicker(String userName, long connId, String product) throws InvalidParameterException, AlreadySubscribedException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		User u = connectedUsers.get(userName);
		
		TickerPublisher.getInstance().subscribe(u, product);
	}
	
	public void unSubscribeCurrentMarket(String userName, long connId, String product) throws InvalidParameterException, NotSubscribedException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		User u = connectedUsers.get(userName);
		
		CurrentMarketPublisher.getInstance().unSubscribe(u, product);
	}
	
	public void unSubscribeLastSale(String userName, long connId, String product) throws InvalidParameterException, NotSubscribedException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		User u = connectedUsers.get(userName);
		
		LastSalePublisher.getInstance().unSubscribe(u, product);
	}
	
	public void unSubscribeMessages(String userName, long connId, String product) throws InvalidParameterException, NotSubscribedException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		User u = connectedUsers.get(userName);
		
		MessagePublisher.getInstance().unSubscribe(u, product);
	}
	
	public void unSubscribeTicker(String userName, long connId, String product) throws InvalidParameterException, NotSubscribedException, UserNotConnectedException
	{
		verifyUser(userName, connId);
		
		User u = connectedUsers.get(userName);
		
		TickerPublisher.getInstance().unSubscribe(u, product);
	}
	
	
	
}
