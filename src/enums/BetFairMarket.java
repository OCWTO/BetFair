package enums;

public enum BetFairMarket
{
	CLOSED_MARKET("CLOSED"),
	INACTIVE_MARKET("INACTIVE"),
	SUSPENDED_MARKET("SUSPENDED"),
	OPEN_MARKET("OPEN");
	
	private String marketStatus;
	
	private BetFairMarket(String marketStatus)
	{
		this.marketStatus = marketStatus;
	}

	@Override
	public String toString() {
		return marketStatus;
	}
}