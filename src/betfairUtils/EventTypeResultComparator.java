package betfairUtils;

import java.util.Comparator;

import betfairGSONClasses.EventTypeResult;

/**
 * This class is used for sorting EventTypeResult objects by EventType name.
 * 
 * @author Craig Thomson
 *
 */
public class EventTypeResultComparator implements Comparator<EventTypeResult>
{
	@Override
	public int compare(EventTypeResult a, EventTypeResult b)
	{
		return a.getEventType().getName().compareTo(b.getEventType().getName());
	}
}