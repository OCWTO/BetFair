package enums;

/**
 * All markets that are currently used for predicting are in this class.
 * @author Craig
 *
 */
public enum MarketNames
{
	MATCH_ODDS("Match Odds"),
	NEXT_GOALSCORER("Next Goalscorer"),
	HALF_TIME("Half Time"),
	SENDING_OFF("Sending Off"),
	CORRECT_SCORE("Correct Score");

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
