package model;

public class GameTime
{
	private int mins;
	private int seconds;
	
	public GameTime(int gameMins, int gameSec)
	{
		mins = gameMins;
		seconds = gameSec;
	}
	
	@Override
	public String toString()
	{
		return mins + ":" + seconds;
	}
}