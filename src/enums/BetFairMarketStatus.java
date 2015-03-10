package enums;

/**
 * Enum for BetFair Market statuses
 * 
 * @author Craig Thomson
 *
 */
public enum BetFairMarketStatus
{
	CLOSED_MARKET("CLOSED"), INACTIVE_MARKET("INACTIVE"), SUSPENDED_MARKET(
			"SUSPENDED"), OPEN_MARKET("OPEN");

	private String marketStatus;

	private BetFairMarketStatus(String marketStatus)
	{
		this.marketStatus = marketStatus;
	}

	@Override
	public String toString()
	{
		return marketStatus;
	}
}