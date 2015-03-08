package model;

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
	public String login(String username, String password, String filePassowrd);
	
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
	 * Gets the list of games availble
	 * @param sportId
	 * @return
	 */
	public List<String> getGameListForSport(List<String> sportId);
	
	/**
	 * 
	 * @param gameId
	 * @return
	 */
	public List<String> getMarketsForGame(String gameId);
	
	/**
	 * 
	 * @return
	 */
	public IBetFairCore getBetFairObject();
	
	public void setDebug(boolean debugValue);
	
}
