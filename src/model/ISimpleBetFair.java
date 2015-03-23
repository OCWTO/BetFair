package model;

import java.io.File;
import java.util.List;

public interface ISimpleBetFair
{
	/**
	 * 
	 * @param username
	 * @param password
	 * @param filePassowrd
	 * @return
	 */
	public String login(String username, String password, String certPassword, File certFile);
	
	/**
	 * 
	 * @return
	 */
	public List<BetFairSportObject> getSupportedSportList();
	
	/**
	 * 
	 * @param sportId
	 * @return
	 */
	public List<BetFairGameObject> getGameListForSport(String sportId);
	
	/**
	 * Gets the list of games available
	 * @param sportId
	 * @return
	 */
	public List<BetFairGameObject> getGameListForSport(List<String> sportId);
	
	/**
	 * 
	 * @param gameId
	 * @return
	 */
	public List<BetFairMarketObject> getMarketsForGame(String gameId);
	
	
	public List<BetFairMarketData> getMarketInformation(List<String> marketIds);
	
	/**
	 * 
	 * @return
	 */
	public IBetFairCore getBetFairObject();
	
	public void setDebug(boolean debugValue);
	
}
