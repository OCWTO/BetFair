package betfairUtils;

import java.util.Comparator;

import betfairGSONClasses.MarketCatalogue;

/**
 * This class is used to sort MarketCatalogue objects by Event name
 * 
 * @author Craig Thomson
 *
 */
public class MarketCatalogueEventNameComparator implements
		Comparator<MarketCatalogue>
{
	@Override
	public int compare(MarketCatalogue a, MarketCatalogue b)
	{
		return a.getMarketName().compareTo(b.getMarketName());
	}
}