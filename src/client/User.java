package client;

import java.util.ArrayList;

import exceptions.AlreadySubscribedException;
import exceptions.InvalidParameterException;
import exceptions.InvalidPriceOperation;
import exceptions.OrderNotFoundException;
import exceptions.UserNotConnectedException;
import messages.CancelMessage;
import messages.FillMessage;
import prices.Price;
import trading.TradableDTO;

public interface User 
{
	String getUserName();
	
	void acceptLastSale(String product, Price p, int v);
	
	void acceptMessage(FillMessage fm);
	
	void acceptMessage(CancelMessage cm);
	
	void acceptMarketMessage(String message);
	
	void acceptTicker(String product, Price p, char direction);
	
	void acceptCurrentMarket(String product, Price bp, int bv, Price sp, int sv);
	
	void connect() throws InvalidParameterException, UserNotConnectedException;
	
	void disConnect() throws InvalidParameterException, UserNotConnectedException;
	
	void showMarketDisplay() throws UserNotConnectedException;
	
	String submitOrder(String product, Price price, int volume, String side) throws InvalidParameterException, UserNotConnectedException;
	
	void submitOrderCancel(String product, String side, String orderId) throws InvalidParameterException, OrderNotFoundException, UserNotConnectedException;
	
	void submitQuote(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidParameterException, UserNotConnectedException;
	
	void submitQuoteCancel(String product) throws InvalidParameterException, UserNotConnectedException;
	
	void subscribeCurrentMarket(String product) throws AlreadySubscribedException, InvalidParameterException, UserNotConnectedException;
	
	void subscribeLastSale(String product) throws InvalidParameterException, AlreadySubscribedException, UserNotConnectedException;
	
	void subscribeMessages(String product) throws InvalidParameterException, AlreadySubscribedException, UserNotConnectedException;
	
	void subscribeTicker(String product) throws InvalidParameterException, AlreadySubscribedException, UserNotConnectedException;
	
	Price getAllStockValue() throws InvalidPriceOperation;
	
	Price getAccountCosts();
	
	Price getNetAccountValue() throws InvalidPriceOperation;
	
	String[][] getBookDepth(String product) throws InvalidParameterException, UserNotConnectedException;
	
	String getMarketState() throws InvalidParameterException, UserNotConnectedException;
	
	ArrayList<TradableUserData> getOrderIds();
	
	ArrayList<String> getProductList();
	
	Price getStockPositionValue(String sym) throws InvalidPriceOperation;
	
	int getStockPositionVolume(String sym);
	
	ArrayList<String> getHoldings();
	
	ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) throws InvalidParameterException, UserNotConnectedException;
	
	
}
