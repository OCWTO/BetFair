package betFairGSONClasses;

import java.util.Date;

/**
 * This class is used for GSON serialisation
 * 
 * @author BetFair
 */
public class TimeRange
{
	private Date from;

	public final Date getFrom()
	{
		return from;
	}

	public final void setFrom(Date from)
	{
		this.from = from;
	}

	private Date to;

	public final Date getTo()
	{
		return to;
	}

	public final void setTo(Date to)
	{
		this.to = to;
	}
}
