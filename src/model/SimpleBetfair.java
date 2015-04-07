package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import betfairGSONClasses.EventTypeResult;
import betfairGSONClasses.LoginResponse;
import betfairGSONClasses.MarketBook;
import betfairGSONClasses.MarketCatalogue;
import betfairGSONClasses.MarketFilter;
import betfairUtils.EventTypeResultComparator;
import betfairUtils.MarketCatalogueEventNameComparator;
import betfairUtils.MarketCatalogueEventOpenDateComparator;
import exceptions.CryptoException;

/**
 * This class communicates with the BetFairCore class, takes the output and it sorts, parses
 * and then returns it's results in an easy to read String format. It was used for recording games
 * before a GUI was made.
 * @author Craig Thomson
 *
 */
public class SimpleBetfair implements ISimpleBetFair
{
	private BetfairCore betFair;

	/**
	 * 
	 * @param debug If true is passed in then the requests from the BetFairCore API are output
	 * in their raw JSON format.
	 */
	public SimpleBetfair(boolean debug)
	{
		betFair = new BetfairCore(debug);
	}

	/**
	 * @param username BetFair account username
	 * @param password BetFair account password
	 * @param certPassword The local p12 certificate files password
	 * @return The attempted log ins status, either SUCCESS or Login failure with a reason.
	 * @throws CryptoException if the p12 certificate file cannot be accessed with the given password.
	 */
	public String login(String username, String password, String certPassword, File certFile)
	{
		LoginResponse result = betFair.login(username, password, certPassword, certFile);

		if (result.getSessionToken() == null)
			return "Login failure. Reason: " + result.getLoginStatus();

		return result.getLoginStatus();
	}

	/**
	 * This maps to BetFairs listEventTypes method.
	 * @return List of Strings where each index represents a sport that BetFair supports betting on.
	 * The returned String is of the format {sportname,BetFair} sport id. 
	 */
	public List<BetfairSportObject> getSupportedSportList()
	{
		List<EventTypeResult> sportList = betFair
				.listEventTypes(new MarketFilter());
		sportList.sort(new EventTypeResultComparator());

		List<BetfairSportObject> formattedSportList = new ArrayList<BetfairSportObject>();

		for (int i = 0; i < sportList.size(); i++)
		{
			formattedSportList.add(new BetfairSportObject(sportList.get(i).getEventType().getName(),
					sportList.get(i).getEventType().getId()));
		}

		return formattedSportList;
	}

	/**
	 * This method returns a list of strings that represents the games which are in the sport passed.
	 * This method maps to the BetFair listEvents method.
	 * @param id The BetFair id representing the sport.
	 * @return A list of strings representing the games returned. The returned strings are in the
	 * format of {gamename, gameid}
	 */
	public List<BetfairGameObject> getGameListForSport(String id)
	{
		List<MarketCatalogue> gameList = betFair.getGames(id);
		gameList.sort(new MarketCatalogueEventOpenDateComparator());
		List<BetfairGameObject> formattedGameList = new ArrayList<BetfairGameObject>();
		

		for(int i=0; i < gameList.size();i++)
		{
			formattedGameList.add(new BetfairGameObject(gameList.get(i).getEvent()));
		}
		return formattedGameList;
	}
	
	/**
	 * The list of Strings returned is in the form of {marketName, marketId}. This method maps to the
	 * BetFair listMarketCatalogue method.
	 * @param gameId The BetFair id for the desired game.
	 * @return A List of Strings representing the markets available, which you can bet on in the game.
	 */
	public List<BetfairMarketObject> getMarketsForGame(String gameId)
	{
		List<MarketCatalogue> marketList = betFair.getMarketCatalogue(gameId);

		marketList.sort(new MarketCatalogueEventNameComparator());
		List<BetfairMarketObject> formattedMarketList = new ArrayList<BetfairMarketObject>();
		for(int i = 0; i < marketList.size(); i++)
		{
			formattedMarketList.add(new BetfairMarketObject(marketList.get(i)));
		}

		return formattedMarketList;
	}
	
	public void setDebug(boolean debugValue)
	{
		betFair.setDebug(debugValue);
	}

	/**
	 * @return This classes held instance of the BetFairCore class.
	 */
	public BetfairCore getBetFair()
	{
		return this.betFair;
	}


	@Override
	public List<BetfairGameObject> getGameListForSport(List<String> sportId)
	{
		List<BetfairGameObject> multipleSportGameList = new ArrayList<BetfairGameObject>();
		
		for(String id : sportId)
		{
			multipleSportGameList.addAll(getGameListForSport(id));
		}
		return multipleSportGameList;
	}

	@Override
	public IBetFairCore getBetFairObject()
	{
		return betFair;
	}

	@Override
	public List<BetfairMarketData> getMarketInformation(List<String> marketIds)
	{
		List<MarketBook> marketBook = betFair.getMarketBook(marketIds);
		List<BetfairMarketData> marketInformation = new ArrayList<BetfairMarketData>();
		
		for(MarketBook book: marketBook)
		{
			marketInformation.add(new BetfairMarketData(book));
		}
		return marketInformation;
	}
}