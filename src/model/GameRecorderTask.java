package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

//pass in markets to query, 
public class GameRecorderTask extends TimerTask
{
	private BetFairCore betFair;
	private String marketId, marketName;
	private List<String> results;
	private Date time;
	
	public GameRecorderTask(BetFairCore betFair, String marketId, String marketName)
	{
		this.betFair = betFair;
		this.marketId = marketId;
		this.marketName = marketName;
		results = new ArrayList<String>();
		time = new Date();
		results.add("MARKET " + marketName + " " + marketId + "STARTED " + time.toString());
	}

	@Override
	public void run()
	{
		//call thing
		//if market is closed then cancel and store
		//else get data and add to array
		
		
		// TODO Auto-generated method stub
		//this.
	}

}
