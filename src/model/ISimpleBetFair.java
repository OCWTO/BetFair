package model;

import java.io.File;
import java.util.List;

/**
 * Interface for classes that are to provide simple interaction with objects implementing IBetFairCore
 * @author Craig
 *
 */
public interface ISimpleBetFair
{
	/**
	 * Attempt a log in to Betfair
	 * @param username Betfair account username
	 * @param password Betfair account password
	 * @param certPassword Certificate file password
	 * @param certFile Users certificate file
	 * @return Success or failure with reasons
	 */
	public String login(String username, String password, String certPassword, File certFile);
	
	/**
	 * Get the list of sports that Betfair allows betting on
	 * @return BetfairSportObjects, one for each sports which contains name and id
	 */
	public List<BetfairSportObject> getSupportedSportList();
	
	/**
	 * Get a list of games for the sport. The games returned are all either in play or not started (provides 2 day look ahead)
	 * @param sportId The id of the sport you want a game list for
	 * @return List of BetfairGameObjects, one for each game. Inside is the game id and names
	 */
	public List<BetfairGameObject> getGameListForSport(String sportId);
	
	/**
	 * Get a list of games for the sport(s). The games returned are all either in play or not started (provides 2 day look ahead)
	 * @param sportId The id of the sport you want a game list for
	 * @return List of BetfairGameObjects, one for each game. Inside is the game id and names
	 */
	public List<BetfairGameObject> getGameListForSport(List<String> sportId);
	
	/**
	 * Get the list of markets for the given gameId
	 * @param gameId The id of the game that the markets are for
	 * @return List of BetfairMarketObjects, each representing a market available for the game.
	 */
	public List<BetfairMarketObject> getMarketsForGame(String gameId);
	
	/**
	 * Get live market data for the requested markets 
	 * @param marketIds List of market ids that the data is for
	 * @return List of BetfairMarketData objects, one for each of the requested marketIds
	 */
	public List<BetfairMarketData> getMarketInformation(List<String> marketIds);
	
	/**
	 * 
	 * @return instance of the underlying IBetFairCore object that is performing requests for this object.
	 */
	public IBetFairCore getBetFairObject();
	
	public void setDebug(boolean debugValue);
	
}
