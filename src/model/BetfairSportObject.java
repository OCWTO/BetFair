package model;

/**
 * This class is used to represent sports
 * @author Craig
 *
 */
public class BetfairSportObject
{
	private String sportName;
	private String sportId;
	
	/**
	 * 
	 * @param name the sports name
	 * @param id the sports id
	 */
	public BetfairSportObject(String name, String id)
	{
		this.sportName = name;
		this.sportId = id;
	}
	
	/**
	 * 
	 * @return the sports name
	 */
	public String getName()
	{
		return this.sportName;
	}
	
	/**
	 * @return the sports id
	 */
	public String getSportId()
	{
		return this.sportId;
	}
	
	@Override
	public String toString()
	{
		return "Sport name: " + sportName +". Sport Id: " + sportId;
	}
}