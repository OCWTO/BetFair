package model;

import java.util.ArrayList;
import java.util.List;

import betFairGSONClasses.EventTypeResult;
import betFairGSONClasses.LoginResponse;
import betFairGSONClasses.MarketBook;
import betFairGSONClasses.MarketCatalogue;
import betFairGSONClasses.MarketFilter;
import betfairUtils.EventTypeResultComparator;
import betfairUtils.MarketCatalogueEventNameComparator;
import betfairUtils.MarketCatalogueEventOpenDateComparator;
import exceptions.CryptoException;

//TODO add support for sorting events into separate lists (UK, USA, etc) handy for gui
//TODO add support for sorting markets into their groupings e.g. match odds, goal line, etc.

/**
 * This class communicates with the BetFairCore API, takes the output and it sorts, parses
 * and then returns it's results in an easy to read String format.
 * @author Craig Thomson
 *
 */
public class SimpleBetFairCore implements ISimpleBetFair
{
	private BetFairCore betFair;

	/**
	 * 
	 * @param debug If true is passed in then the requests from the BetFairCore API are output
	 * in their raw JSON format.
	 */
	public SimpleBetFairCore(boolean debug)
	{
		betFair = new BetFairCore(debug);
	}

	/**
	 * @param username BetFair account username
	 * @param password BetFair account password
	 * @param certPassword The local p12 certificate files password
	 * @return The attempted log ins status, either SUCCESS or Login failure with a reason.
	 * @throws CryptoException if the p12 certificate file cannot be accessed with the given password.
	 */
	public String login(String username, String password, String certPassword)
	{
		LoginResponse result = betFair.login(username, password, certPassword);

		if (result.getSessionToken() == null)
			return "Login failure. Reason: " + result.getLoginStatus();

		return result.getLoginStatus();
	}

	/**
	 * This maps to BetFairs listEventTypes method.
	 * @return List of Strings where each index represents a sport that BetFair supports betting on.
	 * The returned String is of the format {sportname,BetFair} sport id. 
	 */
	public List<BetFairSportObject> getSupportedSportList()
	{
		List<EventTypeResult> sportList = betFair
				.listEventTypes(new MarketFilter());
		sportList.sort(new EventTypeResultComparator());

		List<BetFairSportObject> formattedSportList = new ArrayList<BetFairSportObject>();

		for (int i = 0; i < sportList.size(); i++)
		{
			formattedSportList.add(new BetFairSportObject(sportList.get(i).getEventType().getName(),
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
	public List<BetFairGameObject> getGameListForSport(String id)
	{
		List<MarketCatalogue> gameList = betFair.getGames(id);
		gameList.sort(new MarketCatalogueEventOpenDateComparator());
		List<BetFairGameObject> formattedGameList = new ArrayList<BetFairGameObject>();
		

		for(int i=0; i < gameList.size();i++)
		{
			formattedGameList.add(new BetFairGameObject(gameList.get(i).getEvent()));
		}
		return formattedGameList;
	}
	
	/**
	 * The list of Strings returned is in the form of {marketName, marketId}. This method maps to the
	 * BetFair listMarketCatalogue method.
	 * @param gameId The BetFair id for the desired game.
	 * @return A List of Strings representing the markets available, which you can bet on in the game.
	 */
	public List<BetFairMarketObject> getMarketsForGame(String gameId)
	{
		List<MarketCatalogue> marketList = betFair.getMarketCatalogue(gameId);

		marketList.sort(new MarketCatalogueEventNameComparator());
		List<BetFairMarketObject> formattedMarketList = new ArrayList<BetFairMarketObject>();
		for(int i = 0; i < marketList.size(); i++)
		{
			formattedMarketList.add(new BetFairMarketObject(marketList.get(i)));
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
	public BetFairCore getBetFair()
	{
		return this.betFair;
	}
	
	/**
	 * @param marketId The market you want information from
	 * @return List of Strings containing formatted market information for the given market.
	 */
	public List<String> getMarketRunnerProbability(List<String> marketId)
	{
		List<MarketBook> marketData = betFair.getMarketBook(marketId);
		return null;
	}

	@Override
	public List<BetFairGameObject> getGameListForSport(List<String> sportId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBetFairCore getBetFairObject()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BetFairMarketData> getMarketInformation(List<String> marketIds)
	{
		List<MarketBook> marketBook = betFair.getMarketBook(marketIds);
		List<BetFairMarketData> marketInformation = new ArrayList<BetFairMarketData>();
		
		for(MarketBook book: marketBook)
		{
			marketInformation.add(new BetFairMarketData(book));
		}
		return marketInformation;
	}
}