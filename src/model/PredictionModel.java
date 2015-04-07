package model;

import java.util.List;

/**
 * Interface defining core methods required for any sport PredictionModel
 * @author Craig
 *
 */
public interface PredictionModel
{
	/**
	 * 
	 * @return The name of the market that the model object is for
	 */
	public String getMarketName();
	
	/**
	 * Initilise the prediction models internal collections with the given values
	 * @param newProbabilities The first set of probability values for the markets runners
	 */
	public void initialize(List<BetfairProbabilityItem> newProbabilities);
	
	/**
	 * @return The list of predictions that have just been made by this predictionmodel
	 */
	public List<String> getPredictions();
	
	/**
	 * Add the given probability data to the existing collections for the runners in this market
	 * @param newProbabilites probability data that is to be added
	 */
	public void addData(List<BetfairProbabilityItem> newProbabilites);
	
	/**
	 * 
	 * @return true if the market has closed. False otherwise
	 */
	public boolean isClosed();
	
	/**
	 * Mark the market that the PredictionModel observes as closed. One more possible prediction event will be produced by it, depending
	 * on if the market closing relates to an event (e.g. for football: penalty market shuts when a penalty occurs)
	 */
	public void closeMarket();
	
	/**
	 * @return The most recent calculated timestamp that data was received for (e.g. for football : 45 + 1:02)
	 */
	public String getMostRecentTime();

	/**
	 * @param market The name of the market that the favoured runner is being requested for
	 * @return null if the PredictionModel doesn't represent the given market. It gives the runner name with the highest
	 * initial probability if the market name matches the internal market name.
	 */
	public String getFavouredRunner();
			
	/**
	 * 
	 * @return The most recent data added. In the form of 'runner name, timestamp, value'
	 */
	public List<String> getRecentValues();
}
