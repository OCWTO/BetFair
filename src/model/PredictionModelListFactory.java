package model;

import enums.BetFairEventTypes;

public class PredictionModelListFactory
{
	public static PredictionModel getModel(String eventTypeId)
	{
		if(eventTypeId.equalsIgnoreCase(BetFairEventTypes.FOOTBALL_ID.toString()))
		{
			return new PredictionModel("", null);
		}
		else
		{
			return new PredictionModel("", null);	
		}
	}
	//TODO implement model, fix interfaces
	//factory to get appropriate model for given sport
}
