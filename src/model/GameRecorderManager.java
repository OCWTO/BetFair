package model;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRecorderManager implements Runnable
{
	private ExecutorService threadPool;
	private BetFairCore betFair;
	//have thread pool
	
	public GameRecorderManager(BetFairCore betFair)
	{
		threadPool = Executors.newFixedThreadPool(10);
		this.betFair = betFair;
		//init thread pool
	}
	
	//assigntask
	public void assignTask(List<String> marketIds)
	{
		Runnable task = new GameRecorder(marketIds, betFair);
		//pass shit to runnable and then executor run
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
}
