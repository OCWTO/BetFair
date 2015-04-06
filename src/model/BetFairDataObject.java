package model;

/**
 * Interface designed for any object that contains betfair data.
 * In use, not all betfair data objects use it
 * @author Craig
 *
 */
public interface BetFairDataObject
{
	public String getName();
	
	public String getMarketId();

	@Override
	public String toString();
}
