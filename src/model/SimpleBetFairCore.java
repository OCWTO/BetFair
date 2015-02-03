package model;

import java.util.ArrayList;
import java.util.List;

import Exceptions.CryptoException;
import betfairUtils.Event;
import betfairUtils.EventTypeResult;
import betfairUtils.EventTypeResultComparator;
import betfairUtils.LoginResponse;
import betfairUtils.MarketCatalogue;
import betfairUtils.MarketFilter;

public class SimpleBetFairCore
{
	private BetFairCore betFair;// = new Core();

	public SimpleBetFairCore(boolean debug)
	{
		betFair = new BetFairCore(debug);
	}

	// maps to core.login
	public String login(String username, String password, String certPassword)
			throws CryptoException
	{
		LoginResponse result = betFair.login(username, password, certPassword);

		if (result.getSessionToken() == null)
			return "Login failure. Reason: " + result.getLoginStatus();

		return result.getLoginStatus();
	}

	// maps to core.geteventlist
	public List<String> getSupportedSportList()
	{
		List<EventTypeResult> sportList = betFair
				.listEventTypes(new MarketFilter());
		sportList.sort(new EventTypeResultComparator());
		// sportList.sort(comparing(EventTypeResult :: getName));

		// sportList.get(0).getEventType().
		List<String> readableOutput = new ArrayList<String>();

		// maybe use stringbuilder here?
		for (int i = 0; i < sportList.size(); i++)
		{
			readableOutput.add(new String(i + " "
					+ sportList.get(i).getEventType().getName() + ". ID: "
					+ sportList.get(i).getEventType().getId()));
		}

		return readableOutput;
	}

	public List<String> getGameListForSport(String id)
	{
		List<MarketCatalogue> gameList = betFair.getGames(id);
		List<String> formattedGameList = new ArrayList<String>();
		
		//TODO look at stringbuilder
		Event actualEvent;
		
		for(int i=0;i<gameList.size();i++)
		{
			actualEvent = gameList.get(i).getEvent();
			formattedGameList.add(i + "," + actualEvent.getName() + "," + actualEvent.getOpenDate()  + "," +  actualEvent.getId());
		}
		//return null;
		return formattedGameList;
	}
	// sportlist

	// sportgamelist

	// ...
}
