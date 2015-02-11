package betfairUtils;

import java.util.Comparator;

//Sorts them by the names of the markets, typically runner x vs runner y
public class MarketCatalogueEventNameComparator implements Comparator<MarketCatalogue>
{
	@Override
	public int compare(MarketCatalogue a, MarketCatalogue b)
	{
		return a.getMarketName().compareTo(b.getMarketName());
	}
}