package betfairGSONClasses;

import java.util.Set;

import enums.PriceData;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class PriceProjection
{
	private Set<PriceData> priceData;
	private ExBestOfferOverRides exBestOfferOverRides;
	private boolean virtualise;
	private boolean rolloverStakes;

	public Set<PriceData> getPriceData()
	{
		return priceData;
	}

	public void setPriceData(Set<PriceData> priceData)
	{
		this.priceData = priceData;
	}

	public ExBestOfferOverRides getExBestOfferOverRides()
	{
		return exBestOfferOverRides;
	}

	public void setExBestOfferOverRides(
			ExBestOfferOverRides exBestOfferOverRides)
	{
		this.exBestOfferOverRides = exBestOfferOverRides;
	}

	public boolean isVirtualise()
	{
		return virtualise;
	}

	public void setVirtualise(boolean virtualise)
	{
		this.virtualise = virtualise;
	}

	public boolean isRolloverStakes()
	{
		return rolloverStakes;
	}

	public void setRolloverStakes(boolean rolloverStakes)
	{
		this.rolloverStakes = rolloverStakes;
	}
}