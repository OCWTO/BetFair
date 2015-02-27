package enums;

/**
 * Enum for BetFair method call parameter names
 * 
 * @author Craig Thomson
 *
 */
public enum BetFairParams
{
	SORT("sort"), LOCALE("locale"), MAX_RESULT("maxResults"), MARKET_IDS(
			"marketIds"), MARKET_PROJECTION("marketProjection"), PRICE_PROJECTION(
			"priceProjection"), FILTER("filter");

	private String param;

	private BetFairParams(String param)
	{
		this.param = param;
	}

	@Override
	public String toString()
	{
		return this.param;
	}
}