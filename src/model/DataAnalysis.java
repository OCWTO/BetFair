package model;

import java.util.Observable;

/**
 * This class receives data from IBetFairCore and analyses it. If patterns in the data are recognised
 * then events are thrown to its observer(s).
 * @author Craig Thomson
 */
public class DataAnalysis extends Observable implements IDataUtiliser
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
