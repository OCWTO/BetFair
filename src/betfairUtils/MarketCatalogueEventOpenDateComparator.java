package betfairUtils;

import java.util.Comparator;

//Sorts them by the dates that the markets open
public class MarketCatalogueEventOpenDateComparator implements Comparator<MarketCatalogue>
{
	@Override
	public int compare(MarketCatalogue a, MarketCatalogue b)
	{
		return a.getEvent().getOpenDate().compareTo(b.getEvent().getOpenDate());
	}
}