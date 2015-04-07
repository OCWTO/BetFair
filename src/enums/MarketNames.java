package enums;

/**
 * All markets that are currently used for predicting are in this class.
 * 
 * @author Craig Thomson
 *
 */
public enum MarketNames
{
	MATCH_ODDS("Match Odds"),
	FIRST_GOALSCORER("First Goalscorer"),
	HALF_TIME("Half Time"),
	SENDING_OFF("Sending Off"),
	CORRECT_SCORE("Correct Score"),
	PENALTY_TAKEN("Penalty Taken?");	
	/*^^^
	 * Note this wasn't in the design (penalty taken) It was just added later in the implementation because its easy to "predict"
	 * Although it is one of the 'cheat' predictions that you can extract by looking at when the market shuts. There's no log files
	 * that contain tracking data for it sadly.
	 */
	private String marketName;

	private MarketNames(String marketName)
	{
		this.marketName = marketName;
	}

	@Override
	public String toString()
	{
		return marketName;
	}
}
