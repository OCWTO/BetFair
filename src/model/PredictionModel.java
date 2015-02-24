package model;

public class PredictionModel
{
	//knows nothing of BetFair, only gets a market name and values
	//examines the last 4 values and looks for changes in gradient between all
	
	//receives data, makes predictions, if prediction made it causes some kind of event
	//it will work in terms of game time so 
	
	/**
	 * Gui exists, observes predictionmodel. Predictionmodel is referred to by the gui and gamerecorder
	 * gamerecorder feeds it data, if events pop gui gets told
	 * 
	 * maybe make a new class to feed data
	 * feed it events every timer tick? so it knows game time and when stuff occurs
	 */
	public PredictionModel()
	{
		
	}
	
	public void addData(String market, double time, double probability)
	{
		
	}
}


