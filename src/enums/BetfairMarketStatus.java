package enums;

/**
 * Enum for BetFair Market Statuses
 * 
 * @author Craig Thomson
 *
 */
public enum BetfairMarketStatus
{
	CLOSED_MARKET("CLOSED"), INACTIVE_MARKET("INACTIVE"), SUSPENDED_MARKET(
			"SUSPENDED"), OPEN_MARKET("OPEN");

	private String marketStatus;

	private BetfairMarketStatus(String marketStatus)
	{
		this.marketStatus = marketStatus;
	}

	@Override
	public String toString()
	{
		return marketStatus;
	}
}