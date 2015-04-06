package betfairGSONClasses;

import java.util.List;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
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