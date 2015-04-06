package betfairGSONClasses;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class RunnerCatalog
{
	private Long selectionId;
	private String runnerName;
	private Double handicap;

	public Long getSelectionId()
	{
		return selectionId;
	}

	public void setSelectionId(Long selectionId)
	{
		this.selectionId = selectionId;
	}

	public String getRunnerName()
	{
		return runnerName;
	}

	public void setRunnerName(String runnerName)
	{
		this.runnerName = runnerName;
	}

	public Double getHandicap()
	{
		return handicap;
	}

	public void setHandicap(Double handicap)
	{
		this.handicap = handicap;
	}

	public String toString()
	{
		return "{" + "" + "selectionId=" + getSelectionId() + ","
				+ "runnerName=" + getRunnerName() + "," + "handicap="
				+ getHandicap() + "," + "}";
	}
}