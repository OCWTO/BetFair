package model;

import enums.BetFairEventTypes;

public class PredictionModelFactory
{
	public static PredictionModel getModel(String eventTypeId)
	{
		if(eventTypeId.equalsIgnoreCase(BetFairEventTypes.FOOTBALL_ID.toString()))
		{
			return new PredictionModel("");
		}
		else
		{
			return new PredictionModel("");	
		}
	}
	//TODO implement model, fix interfaces
	//factory to get appropriate model for given sport
}
