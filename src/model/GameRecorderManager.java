package model;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRecorderManager implements Runnable
{
	private ExecutorService threadPool;
	private BetFairCore betFair;
	private List<String> taskList;
	//have thread pool
	
	//input is betfair, 
	public GameRecorderManager(BetFairCore betFair, List<String> taskList)
	{
		threadPool = Executors.newFixedThreadPool(10);
		this.betFair = betFair;
		this.taskList = taskList;
	}
	
	//assigntask
	//Always have to track half time market
	//once we find half time market as closed, we sleep all other threads 15min
	public void assignTask(List<String> marketIds)
	{
		//we give our task ids to poll, the betfair instance and a collection for communication. so we need a map from thread to task?
		//first market to shut is half time so check all for a status of closed, then sleep all others
		//threadPool.
		Runnable task = new GameRecorder(marketIds, betFair);
		threadPool.execute(task);
		//threadPool. get the future object?
		//task.
		//pass shit to runnable and then executor run
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
}
