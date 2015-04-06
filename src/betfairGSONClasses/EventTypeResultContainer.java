package betfairGSONClasses;

import java.util.List;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
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