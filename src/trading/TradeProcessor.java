package trading;

import java.util.HashMap;

import exceptions.InvalidParameterException;
import messages.FillMessage;

public interface TradeProcessor 
{
	public HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidParameterException
;
}
