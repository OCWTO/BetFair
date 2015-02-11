package model;

import java.util.ArrayList;
import java.util.List;

import Exceptions.CryptoException;
import betfairUtils.Event;
import betfairUtils.EventTypeResult;
import betfairUtils.EventTypeResultComparator;
import betfairUtils.LoginResponse;
import betfairUtils.MarketCatalogue;
import betfairUtils.MarketCatalogueEventOpenDateComparator;
import betfairUtils.MarketCatalogueEventNameComparator;
import betfairUtils.MarketFilter;

//In this class I take the betfair outputs and format them into readable strings
//TODO add support for sorting events into separate lists (UK, USA, etc) handy for gui
//TODO add support for sorting markets into their groupings e.g. match odds, goal line, etc.
public class SimpleBetFairCore
{
	private BetFairCore betFair;

	public SimpleBetFairCore(boolean debug)
	{
		betFair = new BetFairCore(debug);
	}

	// maps to core.login
	/**
	 * @param username BetFair account username
	 * @param password BetFair account password
	 * @param certPassword The local p12 certificate files password
	 * @return The attempted log ins status, either SUCCESS or Login failure with a reason.
	 * @throws CryptoException if the p12 certificate file cannot be accessed with the given password.
	 */
	public String login(String username, String password, String certPassword)
			throws CryptoException
	{
		LoginResponse result = betFair.login(username, password, certPassword);

		if (result.getSessionToken() == null)
			return "Login failure. Reason: " + result.getLoginStatus();

		return result.getLoginStatus();
	}

	//Maps to core.listEvents
	public List<String> getSupportedSportList()
	{
		List<EventTypeResult> sportList = betFair
				.listEventTypes(new MarketFilter());
		sportList.sort(new EventTypeResultComparator());

		List<String> readableOutput = new ArrayList<String>();

		for (int i = 0; i < sportList.size(); i++)
		{
			readableOutput.add(new String(i + " "
					+ sportList.get(i).getEventType().getName() + ". ID: "
					+ sportList.get(i).getEventType().getId()));
		}

		return readableOutput;
	}

	//Maps to 
	public List<String> getGameListForSport(String id)
	{
		List<MarketCatalogue> gameList = betFair.getGames(id);
		//game);
		gameList.sort(new MarketCatalogueEventOpenDateComparator());
		List<String> formattedGameList = new ArrayList<String>();
		
		Event actualEvent;
		
		for(int i=0;i<gameList.size();i++)
		{
			actualEvent = gameList.get(i).getEvent();
			formattedGameList.add(i + "," + actualEvent.getName() + "," + actualEvent.getOpenDate()  + "," +  actualEvent.getId());
		}
		//return null;
		return formattedGameList;
	}
	
	public List<String> getMarketsForGame(String gameId)
	{
		List<MarketCatalogue> marketList = betFair.getMarketCatalogue(gameId);
		System.out.println(marketList.get(0));
		System.out.println(marketList.get(0).getMarketName());
		marketList.sort(new MarketCatalogueEventNameComparator());
		List<String> formattedMarketList = new ArrayList<String>();
		for(int i = 0; i < marketList.size(); i++)
		{
			formattedMarketList.add(i + "," + marketList.get(i).getMarketName() + "," + marketList.get(i).getMarketId());
		}

		return formattedMarketList;
	}

	public BetFairCore getBetFair()
	{
		return this.betFair;
	}
}