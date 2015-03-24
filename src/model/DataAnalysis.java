package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * This class is an intermediary class for data analysis. It receives data from gameRecorder
 * using the Observer pattern and throws that data into PredictionModel objects. It then grabs
 * predicted events from PredictionModel objects and then throws them up to its observer (AnalysisView).
 * 
 * @author Craig Thomson
 */
public class DataAnalysis implements Observer, Observable
{
	private List<Observer> observers;
	private GameRecorder recorder;
	private List<PredictionModel> predictionModel;
	private Date gameStartTime;
	private boolean started;
	private Timer recorderTimer = new Timer();
	
	public DataAnalysis(ProgramOptions options)
	{
		started = false;
		observers = new ArrayList<Observer>();
		recorder = new GameRecorder(options);
		recorder.addObserver(this);
		predictionModel = new ArrayList<PredictionModel>();
	}
	
	/**
	 * Schedule the GameRecorder class to start getting data. It will
	 * start once the game has started.
	 */
	public void start(long queryTimeInMS)
	{
		//recorderTimer = new Timer();
		recorderTimer.schedule(recorder, recorder.getStartDelayInMS(), queryTimeInMS);
	}
	
	
	/**
	 * Initialise the list of prediction models, which exist for each tracked market.
	 * @param marketProbabilities An initial list of market probabilities
	 */
	private void initPredictionModel(List<BetFairMarketItem> marketProbabilities)
	{
		//For each market we have
		for(BetFairMarketItem marketProb : marketProbabilities)
		{
			//Create prediction model for the market
			PredictionModel marketModel = new PredictionModel(marketProb.getMarketName(), gameStartTime);
			
			//add data for the market to the model
			marketModel.init(marketProb.getProbabilities());
			
			//Store this predictionmodel
			predictionModel.add(marketModel);
		}
		started = true;
	}
	
	/**
	 * Update method for this Observer. It receives EventLists from GameRecorder, passes data from it
	 * into the models. It then polls each model for any events and adds them to it's list if any
	 * exist
	 */
	@Override
	public void update(Object obj)
	{
		//Cast the received object to an EventList
		EventList events = (EventList) obj;
		
		
		
		//Grab the raw probability data from it
		List<BetFairMarketItem> marketProbabilities = events.getProbabilites();
		System.out.println("RECEIVED DATA FOR " + marketProbabilities.size() + " MARKETS");
		//Store the game start time (used later to tell prediction models when the game starts.
		gameStartTime = events.getStartTime();
		
		//Add data to the prediction models
		addDataToPredictors(marketProbabilities);
		
		//Get predicted events
		List<String> predictedEvents = getPredictedEvents();
		
		//Analyse the events (remove unnecessary ones)
		System.out.println("UPDATE DONE");
		
		if(recorder.isRunning())
		{
			recorderTimer.cancel();
		}
	}
	
	private List<String> getPredictedEvents()
	{
		List<String> predictedEvents = new ArrayList<String>();
		
		//Go through each markets model and poll for data
		for(PredictionModel marketPredictionModel : predictionModel)
		{
			predictedEvents.addAll(marketPredictionModel.getPredictions());
		}
		System.out.println("Trying to predict events");
		return predictedEvents;
	}
	
	/**
	 * Adds data to existing predictor objects
	 * @param marketProbabilities List of market probabilities to be given to predictors
	 */
	private void addDataToPredictors(List<BetFairMarketItem> marketProbabilities)
	{
		//If this is the first iterator then we initilise them.
		if(!started)
		{
			initPredictionModel(marketProbabilities);
		}
		//Otherwise it just adds data to existing models.
		else
		{
			addToExistingPredictionModel(marketProbabilities);
		}
	}

	
	private void addToExistingPredictionModel(List<BetFairMarketItem> marketProbabilities)
	{
		System.out.println("Its trying to add to the predictor model.");

		//For each market we're tracking (this can include closed markets)
		for(int i = 0; i < predictionModel.size(); i++)
		{
			//For each market we're getting data for (active)
			for(int j = 0; j < marketProbabilities.size(); j++)
			{
				//If the market names match, give it the relevant data and break (the market is still live)
				if(marketProbabilities.get(j).getMarketName().equals(predictionModel.get(i).getMarketName()))
				{
					System.out.println("Adding data for " + marketProbabilities.get(j).getMarketName());
					predictionModel.get(i).addData(marketProbabilities.get(j).getProbabilities());
					break;
				}
				else
				{
					//No match so no data for this market was received, thus it's closed. Null tells it that its closed.
					System.out.println("Found dead market " + marketProbabilities.get(j).getMarketName());
					predictionModel.get(i).addData(null);
				}
			}
		}
	}

	@Override
	public void addObserver(Observer obs)
	{
		observers.add(obs);
	}

	@Override
	public void removeObserver(Observer obs)
	{
		observers.remove(obs);
	}

	@Override
	public void notifyObservers(Object event)
	{
		for(Observer obs : observers)
			obs.update(event);
	}
	
}