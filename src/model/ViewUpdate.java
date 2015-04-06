package model;

import java.util.List;

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
	
	public ViewUpdate(String currentGameTime, String lastUpdated, List<String> runnersAndValues, List<String> predictedEvents)
	{
		initialUpdate = false;
		gameTime = currentGameTime;
		lastUpdateTime = lastUpdated;
		runnerValues = runnersAndValues;
		predictions = predictedEvents;
	}
	
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