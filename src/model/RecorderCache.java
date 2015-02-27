package model;

/**
 * This class represents a small cache used by the RecorderAnalysis class
 * 
 * @author Craig Thomson
 *
 */
public class RecorderCache
{
	private static final int cacheSize = 4;
	private String[] cache;

	// Receives data in the format of timestamp (game time), probability,
	// runner?
	/**
	 * 
	 */
	public RecorderCache()
	{
		cache = new String[cacheSize];
	}

	/**
	 * 
	 * @param newData
	 */
	public void addToCache(String newData)
	{
		if (cache.length == cacheSize - 1)
		{
			cacheShuffle(newData);
		}
		else
			cache[cache.length] = newData; // double check this
	}

	/**
	 * Shifts everything up 1, chops off the end index and adds the new data
	 * 
	 * @param newData
	 */
	private void cacheShuffle(String newData)
	{

	}

	/**
	 * 
	 * @return
	 */
	public double getGradientChange()
	{
		// what about runner?
		String[] index0 = cache[0].split(",");
		String[] indexLast = cache[cacheSize - 1].split(",");

		double x1 = Double.valueOf(index0[0]);
		double y1 = Double.valueOf(index0[1]);

		double x2 = Double.valueOf(indexLast[0]);
		double y2 = Double.valueOf(indexLast[1]);

		return (x2 - x1 / y2 - y1);
	}
}