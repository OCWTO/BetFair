package enums;

public enum BetFairParams
{
	SORT("sort"),
	LOCALE("locale"),
	MAX_RESULT("maxResults"),
	MARKET_IDS("marketIds"),
	MARKET_PROJECTION("marketProjection"),
	PRICE_PROJECTION("priceProjection"),
	FILTER("filer");
	
	private String param;
	
	private BetFairParams(String param)
	{
		this.param = param;
	}
	
	public String getParam()
	{
		return this.param;
	}
}