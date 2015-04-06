package betfairGSONClasses;

/**
 * This class is used for GSON serialisation
 * CLASS TAKEN FROM BETFAIR source: https://github.com/betfair/API-NG-sample-code/tree/master/java/ng
 * @author Betfair
 */
public class Container
{
	private Error error;
	private String jsonrpc;

	public Error getError()
	{
		return error;
	}

	public void setError(Error error)
	{
		this.error = error;
	}

	public String getJsonrpc()
	{
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc)
	{
		this.jsonrpc = jsonrpc;
	}
}