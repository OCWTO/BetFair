package model;

import java.util.ArrayList;
import java.util.List;

import betfairUtils.EventTypeResult;
import betfairUtils.EventTypeResultComparator;
import betfairUtils.LoginResponse;
import betfairUtils.MarketCatalogue;
import betfairUtils.MarketFilter;

public class SimpleBetFairCore
{
	private BetFairCore betFair;// = new Core();

	public SimpleBetFairCore()
	{
		betFair = new BetFairCore();
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
	public List<String> getSupportedSportList()
	{
		List<EventTypeResult> sportList = betFair.listEventTypes(new MarketFilter());
		sportList.sort(new EventTypeResultComparator());
		//sportList.sort(comparing(EventTypeResult :: getName));
		
		// sportList.get(0).getEventType().
		List<String> readableOutput = new ArrayList<String>();

		// maybe use stringbuilder here?
		for(int i = 0; i < sportList.size(); i++)
		{
			readableOutput.add(new String(i + " " + sportList.get(i).getEventType().getName() + ". ID: " + sportList.get(i).getEventType().getId()));
		}

		return readableOutput;
	}

	public List<String> getGameListForSport(String id)
	{
		List<MarketCatalogue> gameList = betFair.getGames(id);
		
		return null;
	}
	// sportlist

	// sportgamelist

	// ...
}
