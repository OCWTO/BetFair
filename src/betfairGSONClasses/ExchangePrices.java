package betfairGSONClasses;

import java.util.List;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class ExchangePrices
{
	private List<PriceSize> availableToBack;
	private List<PriceSize> availableToLay;
	private List<PriceSize> tradedVolume;

	public List<PriceSize> getAvailableToBack()
	{
		return availableToBack;
	}

	public void setAvailableToBack(List<PriceSize> availableToBack)
	{
		this.availableToBack = availableToBack;
	}

	public List<PriceSize> getAvailableToLay()
	{
		return availableToLay;
	}

	public void setAvailableToLay(List<PriceSize> availableToLay)
	{
		this.availableToLay = availableToLay;
	}

	public List<PriceSize> getTradedVolume()
	{
		return tradedVolume;
	}

	public void setTradedVolume(List<PriceSize> tradedVolume)
	{
		this.tradedVolume = tradedVolume;
	}

	public String toString()
	{
		return "{" + "" + "availableToBack=" + getAvailableToBack() + ","
				+ "availableToLay=" + getAvailableToLay() + ","
				+ "tradedVolume=" + getTradedVolume() + "," + "}";
	}
}