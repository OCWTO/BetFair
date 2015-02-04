package model;

import java.util.List;

public class GameRecorder implements Runnable
{
	private List<String> marketIds;
	private BetFairCore betFair;
		
	public GameRecorder(List<String> marketIds, BetFairCore betFair)
	{	
		this.marketIds = marketIds;
		this.betFair = betFair;
	}

	@Override
	public void run()
	{
		
		
	}

}
