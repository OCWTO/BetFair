package betFairGSONClasses;

import java.util.List;

/**
 * This class is used for GSON serialisation
 * 
 * @author BetFair
 */
public class EventTypeResultContainer extends Container
{
	private List<EventTypeResult> result;

	public List<EventTypeResult> getResult()
	{
		return result;
	}

	public void setResult(List<EventTypeResult> result)
	{
		this.result = result;
	}
}