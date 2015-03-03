package model;

import java.util.Observable;

/**
 * This class receives data from IBetFairCore and analyses it. If patterns in
 * the data are recognised then events are thrown to its observer(s).
 * 
 * @author Craig Thomson
 */
public class DataAnalysis extends Observable //implements BetFairDataUtiliser
{
	// TODO multi thread?
	// TODO add timertask
	//@Override
	public void passData(String data)
	{
		// TODO Auto-generated method stub

	}

	//@Override
	public boolean isRunning()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
//TODO find a cut off value to decide an event? then event is decided by looking at the name and finding the mapping to it