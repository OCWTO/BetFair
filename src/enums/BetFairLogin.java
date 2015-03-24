package enums;

/**
 * Enum for login responses
 * @author Craig
 *
 */
public enum BetFairLogin
{
	BADLOGINDETAILS("INVALID_USERNAME_OR_PASSWORD"),
	PENDINGAUTH("PENDING_AUTH"),
	NOWLOCKED("ACCOUNT_NOW_LOCKED"),
	LOCKED("ACCOUNT_ALREADY_LOCKED"),
	SECURITY("SECURITY_RESTRICTED_LOCATION"),
	PASSCHANGE("CHANGE_PASSWORD_REQUIRED");
	
	private String loginReason;
	
	private BetFairLogin(String loginReason)
	{
		this.loginReason = loginReason;
	}
	
	public String toString()
	{
		return this.loginReason;
	}	
}