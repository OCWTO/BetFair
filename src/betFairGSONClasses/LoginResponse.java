package betFairGSONClasses;

public class LoginResponse
{
	// Note the variable names must be the same as the values in the json
	// response
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
