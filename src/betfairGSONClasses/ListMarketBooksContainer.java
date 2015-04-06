package betfairGSONClasses;

import java.util.List;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class ListMarketBooksContainer extends Container
{
	private List<MarketBook> result;

	public List<MarketBook> getResult()
	{
		return result;
	}

	public void setResult(List<MarketBook> result)
	{
		this.result = result;
	}
}