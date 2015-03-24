package model;

/**
 * Interface for any object that contains betfair data.
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
