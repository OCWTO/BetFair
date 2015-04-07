package enums;

/**
 * Enum for login responses
 * @author Craig
 *
 */
public enum BetfairLogin
{
	BADLOGINDETAILS("INVALID_USERNAME_OR_PASSWORD"),
	PENDINGAUTH("PENDING_AUTH"),
	NOWLOCKED("ACCOUNT_NOW_LOCKED"),
	LOCKED("ACCOUNT_ALREADY_LOCKED"),
	SECURITY("SECURITY_RESTRICTED_LOCATION"),
	PASSCHANGE("CHANGE_PASSWORD_REQUIRED");
	
	private String loginReason;
	
	private BetfairLogin(String loginReason)
	{
		this.loginReason = loginReason;
	}
	
	public String toString()
	{
		return this.loginReason;
	}	
}