package client;

import java.sql.Timestamp;
import java.util.ArrayList;

import messages.CancelMessage;
import messages.FillMessage;
import prices.Price;
import trading.ProductService;
import trading.TradableDTO;
import exceptions.AlreadySubscribedException;
import exceptions.InvalidParameterException;
import exceptions.InvalidPriceOperation;
import exceptions.OrderNotFoundException;
import exceptions.UserNotConnectedException;
import gui.UserDisplayManager;

public class UserImpl implements User
{
	String userName;
	
	long connectionId;
	
	ArrayList<String> stocks = new ArrayList<>();
	
	ArrayList<TradableUserData> submittedOrders = new ArrayList<>();
	
	Position position;
	
	UserDisplayManager displayManager;
	
	public UserImpl(String user) throws InvalidParameterException
	{
		setUserName(user);
		
		position = new Position();
	}
	
	private void setUserName(String user) throws InvalidParameterException
	{
		if (user == null || user == "")
		{
			throw new InvalidParameterException("Username cannot be null or empty.");
		}
		
		userName = user;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void acceptLastSale(String product, Price price, int volume)
	{
		displayManager.updateLastSale(product, price, volume);
		
		position.updateLastSale(product, price);
	}
	
	public void acceptMessage(FillMessage fm)
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		String summary = String.format("{%s} Fill Message: %s %d %s at %s %s "
				+ "[Tadable Id: %s]\n", timestamp, fm.getSide(), fm.getVolume(), fm.getProduct(),
				fm.getPrice().toString(), fm.getDetails(), fm.getId());
		
		displayManager.updateMarketActivity(summary);
		
		try
		{
			position.updatePosition(fm.getProduct(), fm.getPrice(), fm.getSide(), fm.getVolume());	
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	public void acceptMessage(CancelMessage cm)
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String summary = String.format("{%s} Cancel Message: %s %d %s at %s %s "
				+ "[Tadable Id: %s]\n", timestamp, cm.getSide(), cm.getVolume(), cm.getProduct(),
				cm.getPrice().toString(), cm.getDetails(), cm.getId());
		
		displayManager.updateMarketActivity(summary);
	}
	
	public void acceptMarketMessage(String message)
	{
		displayManager.updateMarketState(message);
	}
	
	public void acceptTicker(String product, Price price, char direction)
	{
		displayManager.updateTicker(product, price, direction);
	}
	
	public void acceptCurrentMarket(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume)
	{
		displayManager.updateMarketData(product, buyPrice, buyVolume, sellPrice, sellVolume);
	}
	
	public void connect() throws InvalidParameterException, UserNotConnectedException
	{
		connectionId = UserCommandService.getInstance().connect(this);
		
		stocks = UserCommandService.getInstance().getProducts(userName, connectionId);
		
		displayManager = new UserDisplayManager(this);
	}
	
	public void disConnect() throws InvalidParameterException, UserNotConnectedException
	{
		UserCommandService.getInstance().disConnect(userName, connectionId);
	}
	
	public void showMarketDisplay() throws UserNotConnectedException
	{
		if (stocks == null)
		{
			throw new UserNotConnectedException("User is not connected.");
		}
		
		displayManager.showMarketDisplay();
	}
	
	public String submitOrder(String product, Price price, int volume, String side) throws InvalidParameterException, UserNotConnectedException
	{
		String orderId = UserCommandService.getInstance().submitOrder(userName, connectionId, product, price, volume, side);
		
		TradableUserData t = new TradableUserData(userName, product, side, orderId);
		
		submittedOrders.add(t);
		
		return orderId;
	}
	
	public void submitOrderCancel(String product, String side, String orderId) throws InvalidParameterException, OrderNotFoundException, UserNotConnectedException
	{
		UserCommandService.getInstance().submitOrderCancel(userName, connectionId, product, side, orderId);
	}
	
	public void submitQuote(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidParameterException, UserNotConnectedException
	{
		UserCommandService.getInstance().submitQuote(userName, connectionId, product, buyPrice, buyVolume, sellPrice, sellVolume);
	}
	
	public void submitQuoteCancel(String product) throws InvalidParameterException, UserNotConnectedException
	{
		UserCommandService.getInstance().submitQuoteCancel(userName, connectionId, product);
	}
	
	public void subscribeCurrentMarket(String product) throws AlreadySubscribedException, InvalidParameterException, UserNotConnectedException
	{
		UserCommandService.getInstance().subscribeCurrentMarket(userName, connectionId, product);
	}
	
	public void subscribeLastSale(String product) throws InvalidParameterException, AlreadySubscribedException, UserNotConnectedException
	{
		UserCommandService.getInstance().subscribeLastSale(userName, connectionId, product);
	}
	
	public void subscribeMessages(String product) throws InvalidParameterException, AlreadySubscribedException, UserNotConnectedException
	{
		UserCommandService.getInstance().subscribeMessages(userName, connectionId, product);
	}
	
	public void subscribeTicker(String product) throws InvalidParameterException, AlreadySubscribedException, UserNotConnectedException
	{
		UserCommandService.getInstance().subscribeTicker(userName, connectionId, product);
	}
	
	public Price getAllStockValue() throws InvalidPriceOperation
	{
		return position.getAllStockValue();
	}
	
	public Price getAccountCosts()
	{
		return position.getAccountCosts();
	}
	
	public Price getNetAccountValue() throws InvalidPriceOperation
	{
		return position.getNetAccountValue();
	}
	
	public String[][] getBookDepth(String product) throws InvalidParameterException, UserNotConnectedException
	{
		return UserCommandService.getInstance().getBookDepth(userName, connectionId, product);
	}
	
	public String getMarketState() throws InvalidParameterException, UserNotConnectedException
	{
		return UserCommandService.getInstance().getMarketState(userName, connectionId);
	}
	
	public ArrayList<TradableUserData> getOrderIds()
	{
		return new ArrayList<>(submittedOrders);
	}
	
	public ArrayList<String> getProductList()
	{
		return new ArrayList<>(stocks);
	}
	
	public Price getStockPositionValue(String product) throws InvalidPriceOperation
	{
		return position.getStockPositionValue(product);
	}
	
	public int getStockPositionVolume(String product)
	{
		return position.getStockPositionVolume(product);
	}
	
	public ArrayList<String> getHoldings()
	{
		return position.getHoldings();
	}
	
	public ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) throws InvalidParameterException, UserNotConnectedException
	{
		return UserCommandService.getInstance().getOrdersWithRemainingQty(userName, connectionId, product);
	}

}
