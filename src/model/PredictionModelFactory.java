package model;

import java.util.Date;

import enums.BetfairEventTypes;

/**
 * Factory for making prediction models. 
 * @author Craig
 *
 */
public class PredictionModelFactory
{
	public static PredictionModel getModel(String marketName, Date startDate, String eventTypeId)
	{
		if(eventTypeId.equalsIgnoreCase(BetfairEventTypes.FOOTBALL_ID.toString()))
		{
			return new FootballPredictionModel(marketName, startDate);
		}
		return null;
	}
}
