/**
 * @author Craig Thomson
 * This interface is for any class which receives BetFair data.
 */
package model;

/**
 * @author Craig Thomson
 * This interface is for any class which receives BetFair data.
 */
public interface IDataUtiliser
{
	/**
	 * @param data
	 */
	public void passData(String data);
	
	/**
	 * 
	 * @return
	 */
	public boolean isRunning();
	
}
