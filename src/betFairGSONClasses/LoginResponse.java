package betFairGSONClasses;

/**
 * This class is used for GSON serialisation
 * 
 * @author Craig Thomson
 */
public class LoginResponse
{
	private String sessionToken;
	private String loginStatus;

	public String getSessionToken()
	{
		return this.sessionToken;
	}

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