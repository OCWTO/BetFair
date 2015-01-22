package model;

import java.util.ArrayList;
import java.util.List;

import betfairUtils.EventTypeResult;
import betfairUtils.EventTypeResultComparator;
import betfairUtils.LoginResponse;
import betfairUtils.MarketFilter;

public class SimpleBetfair
{
	private Core betFair;// = new Core();

	public SimpleBetfair()
	{
		betFair = new Core();
	}

	// maps to core.login
	public String login(String username, String password, String certPassword)
	{
		LoginResponse result = betFair.login(username, password, certPassword);

		if (result.getSessionToken() == null)
			return "Login failure. Reason: " + result.getLoginStatus();

		return result.getLoginStatus();
	}

	// maps to core.geteventlist
	public List<String> getSportSupportedSportList()
	{
		List<EventTypeResult> sportList = betFair.listEventTypes(new MarketFilter());
		sportList.sort(new EventTypeResultComparator());

		// sportList.get(0).getEventType().
		List<String> readableOutput = new ArrayList<String>();

		// maybe use stringbuilder here?
		for (EventTypeResult result : sportList)
		{
			readableOutput.add(new String("Sport: " + result.getEventType().getName() + ". ID: " + result.getEventType().getId()));
		}

		return readableOutput;
	}

	// sportlist

	// sportgamelist

	// ...
}
