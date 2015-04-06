package betfairUtils;

import java.util.Comparator;

import betfairGSONClasses.MarketCatalogue;

/**
 * This class is used to sort MarketCatalogue objects by the time their event opened.
 * @author Craig Thomson
 *
 */
public class MarketCatalogueEventOpenDateComparator implements Comparator<MarketCatalogue>
{
	@Override
	public int compare(MarketCatalogue a, MarketCatalogue b)
	{
		return a.getEvent().getOpenDate().compareTo(b.getEvent().getOpenDate());
	}
}