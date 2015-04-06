package model;

import java.util.List;

/**
 * This class is used to hold data that is pushed up from the DataAnalysis class to the AnalysisView
 * @author Craig
 *
 */
public class ViewUpdate
{
	private boolean initialUpdate;
	private String homeTeamName;
	private String awayTeamName;
	private String favouredTeamName;
	private String gameStartTime;
	private String gameTime;
	private String lastUpdateTime;
	private List<String> runnerValues;
	private List<String> predictions;
	
	/**
	 * Create a new ViewUpdate object
	 * @param currentGameTime The most recent game time for data received (for football 51:02 etc.)
	 * @param lastUpdated The last time that data was received (actual time)
	 * @param runnersAndValues List of runner names, timestamps and probability values (all comma separated)
	 * @param predictedEvents List of the predicted events to be displayed
	 */
	public ViewUpdate(String currentGameTime, String lastUpdated, List<String> runnersAndValues, List<String> predictedEvents)
	{
		initialUpdate = false;
		gameTime = currentGameTime;
		lastUpdateTime = lastUpdated;
		runnerValues = runnersAndValues;
		predictions = predictedEvents;
	}
	
	/**
	 * 
	 * @param homeTeam The home teams name
	 * @param awayTeam The away teams name
	 * @param favouredTeam The name of the favoured runner
	 * @param startTime The time that the game starts up
	 * @param currentGameTime The most recent game time for data received (for football 51:02 etc.)
	 * @param lastUpdated The last time that data was received (actual time)
	 * @param runnersAndValues List of runner names, timestamps and probability values (all comma separated)
	 * @param predictedEvents List of the predicted events to be displayed
	 */
	public ViewUpdate(String homeTeam, String awayTeam, String favouredTeam, String startTime, String currentGameTime, String lastUpdated, List<String> runnersAndValues, List<String> predictedEvents)
	{
		initialUpdate = true;
		homeTeamName = homeTeam;
		awayTeamName = awayTeam;
		favouredTeamName = favouredTeam;
		gameStartTime = startTime;
		gameTime = currentGameTime;
		lastUpdateTime = lastUpdated;
		runnerValues = runnersAndValues;
		predictions = predictedEvents;
	}
	
	public List<String> getPredictions()
	{
		return predictions;
	}
	
	/**
	 * 
	 * @return true if this is the initial update object that contains more information that a normal update (team names etc.)
	 */
	public boolean getInitialUpdate()
	{
		return initialUpdate;
	}
	
	public String getHomeTeam()
	{
		return homeTeamName;
	}
	
	public String getAwayTeam()
	{
		return awayTeamName;
	}
	
	public String getFavouredTeam()
	{
		return favouredTeamName;
	}
	
	public String getGameTime()
	{
		return gameTime;
	}
	
	public String getLastUpdateTime()
	{
		return lastUpdateTime;
	}
	
	public String getGameStartTime()
	{
		return gameStartTime;
	}
	
	public List<String> getRunnerVals()
	{
		return runnerValues;
	}
}