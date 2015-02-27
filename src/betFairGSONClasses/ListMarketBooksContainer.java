package betFairGSONClasses;

import java.util.List;

/**
 * This class is used for GSON serialisation
 * 
 * @author BetFair
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