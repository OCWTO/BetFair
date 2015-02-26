package betfairUtils;

import java.util.Comparator;

import betFairGSONClasses.EventTypeResult;

public class EventTypeResultComparator implements Comparator<EventTypeResult>
{
	@Override
	public int compare(EventTypeResult a, EventTypeResult b)
	{
		return a.getEventType().getName().compareTo(b.getEventType().getName());
	}
}
