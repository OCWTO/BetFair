package betFairGSONClasses;

import java.util.List;

/**
 * This class is used for GSON serialisation
 * 
 * @author BetFair
 */
public class ListMarketCatalogueContainer extends Container
{

	private List<MarketCatalogue> result;

	public List<MarketCatalogue> getResult()
	{
		return result;
	}

	public void setResult(List<MarketCatalogue> result)
	{
		this.result = result;
	}
}