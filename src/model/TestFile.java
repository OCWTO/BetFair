package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import betfairUtils.JsonConverter;

import com.google.gson.reflect.TypeToken;

/**
 * This class is used for communication with the test file. It provides methods with appropriate signatures
 * to retrieve data from the cast, serialise it back to its original objects and return them. This class could be
 * performed by checking validity of the test file and a more robust method of getting data but it works.
 * @author Craig
 *
 */
public class TestFile
{
	private File jsonFile;
	private BufferedReader bReader;
	private TypeToken<ProgramOptions> programOptionsType;
	private TypeToken<List<BetfairMarketObject>> marketObjectType;
	private TypeToken<List<BetfairMarketData>> marketDataType;
	private ProgramOptions opts;
	/**
	 * 
	 * @param jsonHistoryFile The file that contains the test data
	 */
	public TestFile(File jsonHistoryFile)
	{
		jsonFile = jsonHistoryFile;
		marketDataType = new TypeToken<List<BetfairMarketData>>(){};
		marketObjectType = new TypeToken<List<BetfairMarketObject>>(){};
		programOptionsType = new TypeToken<ProgramOptions>(){};
		initReader();
	}
	
	/**
	 * Initialise the BufferedReader for the file the object with constructed with
	 */
	private void initReader()
	{
		try
		{
			bReader = new BufferedReader(new FileReader(jsonFile));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return The ProgramOptions object that was saved with the test file
	 */
	public ProgramOptions getOptions()
	{
		if(opts == null)
		{
			String json = nextLine();
			ProgramOptions options = JsonConverter.convertFromJson(json, programOptionsType.getType());
			opts = options;
			return opts;
		}
		else
			return opts;
	}
	
	/**
	 * These files can be ~20mb in size so for now it just gets the line. Ideally you'd want to request
	 * the line with a given number (ensuring that getOptions gets the result of line 1, marketlist is 2 and
	 * nextdata is anything else)  but for now this will do. There's also the assurance right now that since
	 * the json data has been generated from a recorder executing to finish, that the final request will be
	 * the final line in the file and it will never go past that. Error checking could be done here but 
	 * whats called and how many times is decided by its content and the assumption that other parts of the
	 * program are working correctly.
	 * @return The next line of the file
	 */
	private String nextLine()
	{
		String currentLine = null;
		try
		{
			currentLine = bReader.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return currentLine;
	}
	
	/**
	 *
	 * @return The saved BetfairMarketObject that is in the test file
	 */
	public List<BetfairMarketObject> getMarketList()
	{
		String json = nextLine();
		List<BetfairMarketObject> marketObj = JsonConverter.convertFromJson(json, marketObjectType.getType());
		return marketObj;
	}
	
	/**
	 * 
	 * @return Get the next BetfairMarketData object that's stored in the test file
	 */
	public List<BetfairMarketData> getNextData()
	{
		String json = nextLine();
		List<BetfairMarketData> marketData = JsonConverter.convertFromJson(json, marketDataType.getType());
		return marketData;
	}
}
