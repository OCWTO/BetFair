package model;
/**
 * A interface version of the Observable abstract class. This is used because
 * Java disallows multiple inheritance and one of the objects in this project
 * that's observed is GameRecorder which is a subclass of Javas TimerTask class.
 * @author Craig Thomson
 *
 */
public interface Observable
{
	public void addObserver(Observer obs);
	
	public void removeObserver(Observer obs);
	
    public void notifyObservers(Object event);
}
