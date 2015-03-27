package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import betfairUtils.JsonConverter;

import com.google.gson.reflect.TypeToken;

public class TestFile
{
	private File jsonFile;
	private BufferedReader bReader;
	private TypeToken<ProgramOptions> programOptionsType;
	private TypeToken<List<BetFairMarketObject>> marketObjectType;
	private TypeToken<List<BetFairMarketData>> marketDataType;
	
	public TestFile(File jsonHistoryFile)
	{
		jsonFile = jsonHistoryFile;
		marketDataType = new TypeToken<List<BetFairMarketData>>(){};
		marketObjectType = new TypeToken<List<BetFairMarketObject>>(){};
		programOptionsType = new TypeToken<ProgramOptions>(){};
		initReader();
	}
	
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

	public ProgramOptions getOptions()
	{
		String json = nextLine();
		ProgramOptions options = JsonConverter.convertFromJson(json, programOptionsType.getType());
		return options;
	}
	
	/**
	 * These files can be ~20mb in size so for now it just gets the line. Ideally you'd want to request
	 * the line with a given number (ensuring that getOptions gets the result of line 1, marketlist is 2 and
	 * nextdata is anything else)  but for now this will do. There's also the assurance right now that since
	 * the json data has been generated from a recorder executing to finish, that the final request will be
	 * the final line in the file and it will never go past that. Error checking could be done here but 
	 * whats called and how many times is decided by its content and the assumption that other parts of the
	 * program are working correctly.
	 * @return
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
	
	public List<BetFairMarketObject> getMarketList()
	{
		String json = nextLine();
		List<BetFairMarketObject> marketObj = JsonConverter.convertFromJson(json, marketObjectType.getType());
		return marketObj;
	}
	
	public List<BetFairMarketData> getNextData()
	{
		String json = nextLine();
		List<BetFairMarketData> marketData = JsonConverter.convertFromJson(json, marketDataType.getType());
		return marketData;
	}
}
