package model;

/**
 * My version of Javas Observer interface. This is necessary because the standard
 * update(Observable,Object) cannot be used in this case because a custom implementation
 * of Observable is used.
 * @author Craig Thomson
 *
 */
public interface Observer
{
	public void update(Object obj);
}
