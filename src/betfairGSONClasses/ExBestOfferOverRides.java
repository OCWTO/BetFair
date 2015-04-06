package betfairGSONClasses;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class ExBestOfferOverRides
{
	private int bestPricesDepth;
	private int rollupLimit;
	private double rollupLiabilityThreshold;
	private int rollupLiabilityFactor;

	public int getBestPricesDepth()
	{
		return bestPricesDepth;
	}

	public void setBestPricesDepth(int bestPricesDepth)
	{
		this.bestPricesDepth = bestPricesDepth;
	}

	public int getRollupLimit()
	{
		return rollupLimit;
	}

	public void setRollupLimit(int rollupLimit)
	{
		this.rollupLimit = rollupLimit;
	}

	public double getRollupLiabilityThreshold()
	{
		return rollupLiabilityThreshold;
	}

	public void setRollupLiabilityThreshold(double rollupLiabilityThreshold)
	{
		this.rollupLiabilityThreshold = rollupLiabilityThreshold;
	}

	public int getRollupLiabilityFactor()
	{
		return rollupLiabilityFactor;
	}

	public void setRollupLiabilityFactor(int rollupLiabilityFactor)
	{
		this.rollupLiabilityFactor = rollupLiabilityFactor;
	}
}