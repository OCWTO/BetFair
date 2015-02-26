package betfairUtils;

import java.util.Comparator;

import betFairGSONClasses.MarketCatalogue;

//Sorts them by the names of the markets, typically runner x vs runner y
public class MarketCatalogueEventNameComparator implements Comparator<MarketCatalogue>
{
	@Override
	public int compare(MarketCatalogue a, MarketCatalogue b)
	{
		return a.getMarketName().compareTo(b.getMarketName());
	}
}