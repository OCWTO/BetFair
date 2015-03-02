package model;

import java.util.Observable;

/**
 * This class receives data from IBetFairCore and records it. Once the market(s)
 * it's recording close it then saves the data to relevant files, found in the
 * logs/gamelogs/ directory.
 * 
 * @author Craig Thomson
 */
public class DataRecorder extends Observable implements IDataUtiliser
{
	@Override
	public void passData(String data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRunning()
	{
		// TODO Auto-generated method stub
		return false;
	}
}