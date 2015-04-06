package betfairGSONClasses;

/**
 * This class is used for GSON serialisation. It represents the object created from a login request.
 * Specially created (not taken from Betfair)
 * @author Craig Thomson
 */
public class LoginResponse
{
	private String sessionToken;
	private String loginStatus;

	/**
	 * Get the generated session token
	 * @return a token is login is successful, null otherwise.
	 */
	public String getSessionToken()
	{
		return this.sessionToken;
	}

	/**
	 * Get the status of the log in attempt
	 * @return Success or Fail (see BetFairLogin enums for some example responses)
	 */
	public String getLoginStatus()
	{
		return this.loginStatus;
	}

	public void setLoginStatus(String loginStatus)
	{
		this.loginStatus = loginStatus;
	}

	public void setSessionToken(String sessionToken)
	{
		this.sessionToken = sessionToken;
	}

	public String toString()
	{
		return "{" + "" + "session token=" + getSessionToken() + ","
				+ "login status=" + getLoginStatus() + "}";
	}
}