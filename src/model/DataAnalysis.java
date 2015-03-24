package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * This class receives data from IBetFairCore and analyses it. If patterns in
 * the data are recognised then events are thrown to its observer(s). It implements
 * both Observer and Observable because it Observes a GameRecorder, puts its events
 * parses them into usable objects from the raw data and throws those objects up to
 * its Observer (GameAnalysisView).
 * 
 * @author Craig Thomson
 */
public class DataAnalysis implements Observer, Observable
{
	private List<Observer> observers;
	private GameRecorder recorder;
	private ProgramOptions options;
	private List<PredictionModel> predictionModel;
	private Date gameStartTime;
	
	public DataAnalysis(ProgramOptions options)
	{
		this.options = options;
		observers = new ArrayList<Observer>();
		recorder = new GameRecorder(options);
		recorder.addObserver(this);
		predictionModel = new ArrayList<PredictionModel>();
	}
	
	public void start()
	{
		Timer timer = new Timer();
		timer.schedule(recorder, recorder.getStartDelayInMS(), 5000);
	}
	
	
	//This class receives events, throws data into analysis and throws old events and new ones generated up

	private void initPredictionModel(List<BetFairMarketItem> marketProbabilities)
	{
		//For each market we have
		for(BetFairMarketItem marketProb : marketProbabilities)
		{
			//Create prediction model for the market
			PredictionModel marketModel = new PredictionModel(marketProb.getMarketName(), gameStartTime);
			
			//add data for the market to the model
			marketModel.init(marketProb.getProbabilities());
			predictionModel.add(marketModel);
		}
	}
	
	/**
	 * Update method for this Observer. It receives EventLists from GameRecorder, passes data from it
	 * into the models. It then polls each model for any events and adds them to it's list if any
	 * exist
	 */
	@Override
	public void update(Object obj)
	{
		EventList events = (EventList) obj;
		List<BetFairMarketItem> marketProbabilities = events.getProbabilites();
		System.out.println("RECEIVED DATA FOR " + marketProbabilities.size() + " MARKETS");
		gameStartTime = events.getStartTime();
		
		//if not init then init and add data
		if(predictionModel.size() == 0)
		{
			initPredictionModel(marketProbabilities);
		}
		else
		{
			addToExistingPredictionModel(marketProbabilities);
		}
		
		List<String> predictedEvents = new ArrayList<String>();
		
		//Go through each markets model and poll for data
		for(PredictionModel marketPredictionModel : predictionModel)
		{
			predictedEvents.addAll(marketPredictionModel.getPredictions());
		}
		System.out.println("getting updated");
		
	}

	//Need to figure out markets that are closed

	private void addToExistingPredictionModel(List<BetFairMarketItem> marketProbabilities)
	{
		//For each market we're tracking
		System.out.println(predictionModel.size() + " IS PRED SIZE");
		System.out.println(marketProbabilities.size() + " IS PROP SIZE");
		
		
		for(int i = 0; i < predictionModel.size(); i++)
		{
			//For each market we're getting data for (active)
			for(int j = 0; j < marketProbabilities.size(); j++)
			{
				//If the market names match, give it the relevant data and break
				System.out.println("TOP LEVEL ITERATION");
				if(marketProbabilities.get(j).getMarketName().equals(predictionModel.get(i).getMarketName()))
				{
					System.out.println("FOUND  MATCH");
					predictionModel.get(i).addData(marketProbabilities.get(j).getProbabilities());
					break;
				}
				else
				{
					//No match so no data for this market was received, thus it's closed. Null tells it that its closed.
					System.out.println("null pass");
					predictionModel.get(i).addData(null);
				}
				System.out.println("NO MATCH");
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
//TODO find a cut off value to decide an event? then event is decided by looking at the name and finding the mapping to it