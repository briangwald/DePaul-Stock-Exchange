package trading;

import java.util.ArrayList;
import java.util.HashMap;

import exceptions.InvalidParameterException;
import prices.Price;
import messages.FillMessage;

public class TradeProcessorPriceTimeImpl implements TradeProcessor 
{
	private HashMap<String, FillMessage> fillMessages = new HashMap<String, FillMessage>();
	
	ProductBookSide bookSide;
	
	public TradeProcessorPriceTimeImpl(ProductBookSide side)
	{
		setProductBookSide(side);
	}
	
	private void setProductBookSide(ProductBookSide side) 
	{
		bookSide = side;
	}
	
	private String makeFillKey(FillMessage fm)
	{
		String key = fm.getUser() + fm.getId() + fm.getPrice();
		
		return key;
	}
	
	private boolean isNewFill(FillMessage fm)
	{
		String key = makeFillKey(fm);
		
		if (!fillMessages.containsKey(key)) return true;
		
		FillMessage oldFill = fillMessages.get(key);
		
		if (fm.getSide() != oldFill.getSide()) return true;
		
		if (fm.getId() != oldFill.getId()) return true;
		
		return false;
	}
	
	private void addFillMessage(FillMessage fm) throws InvalidParameterException
	{
		if (isNewFill(fm))
		{
			String key = makeFillKey(fm);
			fillMessages.put(key, fm);
		}
		else
		{
			String key = makeFillKey(fm);
			
			FillMessage fillMessage = fillMessages.get(key);
			
			fillMessage.setVolume(fillMessage.getVolume() + fm.getVolume());
			fillMessage.setDetails(fm.getDetails());
		}
	}

	@Override
	public HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidParameterException
	{
		fillMessages = new HashMap<String, FillMessage>();
		
		ArrayList<Tradable> tradedOut = new ArrayList<Tradable>();
		
		ArrayList<Tradable> entriesAtPrice = bookSide.getEntriesAtTopOfBook();
		
		for (Tradable t : entriesAtPrice)
		{
			if (trd.getRemainingVolume() == 0)
			{
				break;
			}
			
			Price tradePrice;
			
			if (trd.getRemainingVolume() >= t.getRemainingVolume())
			{
				tradedOut.add(t);
								
				if (t.getPrice().isMarket())
				{
					tradePrice = trd.getPrice();
				}
				else
				{
					tradePrice = t.getPrice();
				}
				
				FillMessage tFillMessage = new FillMessage(t.getUser(), t.getProduct(), tradePrice,
						t.getRemainingVolume(), "leaving 0", t.getSide(), t.getId());
				
				addFillMessage(tFillMessage);
				
				FillMessage trdFillMessage = new FillMessage (trd.getUser(), t.getProduct(), tradePrice, 
						t.getRemainingVolume(), "leaving " + (trd.getRemainingVolume() - t.getRemainingVolume()),
						trd.getSide(), trd.getId());
				
				addFillMessage(trdFillMessage);
				
				trd.setRemainingVolume(trd.getRemainingVolume() - t.getRemainingVolume());
				t.setRemainingVolume(0);
				
				bookSide.addOldEntry(t);
			}
			else
			{
				int remainder = t.getRemainingVolume() - trd.getRemainingVolume();
				
				if (t.getPrice().isMarket())
				{
					tradePrice = trd.getPrice();
				}
				else
				{
					tradePrice = t.getPrice();
				}
				
				FillMessage tFillMessage = new FillMessage(t.getUser(), t.getProduct(), tradePrice,
						trd.getRemainingVolume(), "leaving " + remainder, t.getSide(), t.getId());
				
				addFillMessage(tFillMessage);
				
				FillMessage trdFillMessage = new FillMessage (trd.getUser(), trd.getProduct(), tradePrice, 
						trd.getRemainingVolume(), "leaving 0", trd.getSide(), trd.getId());
				
				addFillMessage(trdFillMessage);
				
				trd.setRemainingVolume(0);
				t.setRemainingVolume(remainder);
				
				bookSide.addOldEntry(trd);
			}
		}
		
		for (Tradable t : tradedOut)
		{
			entriesAtPrice.remove(t);
		}
		
		if (entriesAtPrice.isEmpty())
		{
			bookSide.clearIfEmpty(bookSide.topOfBookPrice());
		}
		return fillMessages;
	}

}
