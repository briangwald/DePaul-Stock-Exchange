package prices;

import java.text.DecimalFormat;


public class Price implements Comparable<Price>
{
	private long value;
	
	private boolean market;	
	
	Price(long value)
	{
		this.market = false;
		this.value = value;
	}
	
	Price()
	{
		this.market = true;
	}
	
	//Price Math
	public Price add(Price p) throws InvalidPriceOperation
	{
		if (this.isMarket() || this == null || p.isMarket() || p == null)
		{
			throw new InvalidPriceOperation("Prices cannot be Market or Null.");
		}
		
		long newValue = this.value + p.value;
		
		return PriceFactory.makeLimitPrice(newValue);
	}
	
	public Price subtract(Price p) throws InvalidPriceOperation
	{
		if (this.isMarket() || this == null || p.isMarket() || p == null)
		{
			throw new InvalidPriceOperation("Prices cannot be Market or Null.");
		}
		
		long newValue = this.value - p.value;
		
		return PriceFactory.makeLimitPrice(newValue);
	}
	
	public Price multiply(int p) throws InvalidPriceOperation
	{
		if (this.isMarket() || this == null)
		{
			throw new InvalidPriceOperation("Price cannot be Market or Null.");
		}
		
		long newValue = this.value * p;
	
		return PriceFactory.makeLimitPrice(newValue);
	}
	
	//Price Comparisons
	public int compareTo(Price p)
	{
		if (this.value == p.value)
		{
			return 0;
		}
		
		if (this.value < p.value)
		{
			return -1;
		}
		
		return 1;
	}
	
	public boolean greaterOrEqual(Price p)
	{
		if (this.isMarket() || this.value < p.value)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean greaterThan(Price p)
	{
		if (this.isMarket() || this.value <= p.value)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean lessOrEqual(Price p)
	{
		if (this.isMarket() || this.value > p.value)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean lessThan(Price p)
	{
		if(this.isMarket() || this.value >= p.value)
		{
			return false;
		}
		
		return true;
	}
	
	public boolean equals(Price p)
	{
		if (this.isMarket())
		{
			return false;
		}
		
		return (this.value == p.value);
	}

	public boolean isMarket()
	{
		return market;
	}
	
	public boolean isNegative()
	{
		if (this.isMarket() || this.value >= 0)
		{
			return false;
		}
		
		return true;
	}
	
	public String toString()
	{
		if (this.isMarket())
		{
			return "MKT";
		}
		
		double doubleValue = (double)this.value;
		
		doubleValue /= 100.00;
		
		DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00;$-#,##0.00");
		
		return currencyFormat.format(doubleValue);
		
	}
}
