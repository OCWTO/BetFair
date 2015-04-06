package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import enums.MarketNames;
//TODO add code so that the all market checker doesnt remove market data for what we're querying for
//ALSO but it needs to stop tracking markets it finds are closed
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
	private int iterationCount = 1;
	private List<PredictionModel> justClosed;
	
	public DataAnalysis(ProgramOptions options)
	{
		justClosed = new ArrayList<PredictionModel>();
		started = false;
		observers = new ArrayList<Observer>();
		recorder = new GameRecorder(options);
		recorder.addObserver(this);
		predictionModel = new ArrayList<PredictionModel>();
	}
	
	/**
	 * Constructor for DataAnalysis in test mode
	 * @param options
	 * @param testFile
	 */
	public DataAnalysis(TestFile testFile)
	{
		started = false;
		observers = new ArrayList<Observer>();
		recorder = new GameRecorder(testFile);
		recorder.addObserver(this);
		predictionModel = new ArrayList<PredictionModel>();
	}
	
	/**
	 * Schedule the GameRecorder class to start getting data. It will
	 * start once the game has started.
	 */
	public void start(long queryTimeInMS)
	{
		recorderTimer.schedule(recorder, recorder.getStartDelayInMS(), queryTimeInMS);
	}
	
	public void start()
	{
		recorderTimer.schedule(recorder, 0, 1);
	}
	
	
	/**
	 * Initialise the list of prediction models, which exist for each tracked market.
	 * @param marketProbabilities An initial list of market probabilities
	 */
	private void initPredictionModel(List<BetfairMarketItem> marketProbabilities)	//TODO works as intended
	{
		//For each market we have
		for(BetfairMarketItem marketProb : marketProbabilities)
		{
			//Create prediction model for the market
			PredictionModel marketModel = new PredictionModel(marketProb.getMarketName(), gameStartTime);
			//System.out.println("Making mode for " + marketProb.getMarketName());
			//add data for the market to the model
			marketModel.init(marketProb.getProbabilities());
			//System.out.println("adding for " + marketProb.getMarketName());
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
		List<BetfairMarketItem> marketProbabilities = events.getProbabilites();
		
		
		//get the match odds probability
		BetfairMarketItem matchOddsProbs = getMarketProbabilities("Match Odds", marketProbabilities);
		
		//System.out.println("RECEIVED DATA FOR " + marketProbabilities.size() + " MARKETS");
		//Store the game start time (used later to tell prediction models when the game starts.
		
		
		//Values pushed are 
		//Probabilities for the runners in terms of a data set
		//Last timestamp, which should also go in data set	(accessed from predictor?) 
//		

		
//			//we push special init object
		gameStartTime = events.getStartTime();

	
		
		//Add data to the prediction models
		addDataToPredictors(marketProbabilities);
		informClosedPredictors(events.getClosedMarkets());
		//Get predicted events
		List<String> predictedEvents = getPredictedEvents();
		
		ViewUpdate gameUpdate = null;
		
		if(iterationCount == 1)
		{
			String homeTeam = matchOddsProbs.getProbabilities().get(0).getRunnerName();
			String awayTeam =  matchOddsProbs.getProbabilities().get(1).getRunnerName();
			String favouredTeam = "";
			String favouredGoalScorer = "";
			for(PredictionModel predictor : predictionModel)
			{
				if(predictor.getFavouredRunner("Match Odds") != null)
				{
					favouredTeam = predictor.getFavouredRunner("Match Odds");
				}
				if(predictor.getFavouredRunner("First GoalScorer") != null)
				{
					favouredGoalScorer = predictor.getFavouredRunner("First GoalScorer");
					break;
				}
				
			}
			System.out.println("HOME IS " + homeTeam);
			System.out.println("AWAY IS " + awayTeam);
			System.out.println("FAVOURED IS " + favouredTeam);
			System.out.println("FAVOURED GOAL SCORER IS " + favouredGoalScorer);
			System.out.println("START TIME IS " + gameStartTime.toString());
			List<String> recentVals = getModelForMarket("Match Odds").getRecentValues();
			String lastUpdate = getMostRecentTimeStamp();
			System.out.println("LAST UPDATED " + lastUpdate);
			if(lastUpdate == null)
			{
				lastUpdate = "Not started yet";
			}
			for(int i = 0; i < recentVals.size(); i++)
			{
				System.out.println(recentVals.get(i));
			}
			
			gameUpdate = new ViewUpdate(homeTeam, awayTeam, favouredTeam, gameStartTime.toString(), lastUpdate, LocalTime.now().toString(), recentVals, predictedEvents);
			//make new gameUpdate
			//public ViewUpdate(String homeTeam, String awayTeam, String favouredTeam, String startTime, String currentGameTime, String lastUpdated, List<String> runnersAndValues)
			
				
		}
		else
		{
			String lastUpdate = getMostRecentTimeStamp();
//			//String timestamp;
//			for(int i = 0; i < events.getProbabilites().size(); i++)
//			{
//				if(events.getProbabilites().get(i).getMarketName().equals("Match Odds"))
//				{
//					BetFairMarketItem marketProbs = events.getProbabilites().get(i);
//					timestamp = marketProbs.getProbabilities().get(marketProbs.getProbabilities().size()-1).getTimeStamp();
//				}
//			}
			//List<String> predictedEvents = getPredictedEvents();
			
			gameUpdate = new ViewUpdate(lastUpdate, LocalTime.now().toString(), getModelForMarket("Match Odds").getRecentValues(), predictedEvents);
			//	public ViewUpdate(String currentGameTime, String lastUpdated, List<String> runnersAndValues, List<String> predictedEvents)
			//gameUpdate = new ViewUpdate()
			//List<String> predictions = events.g
			//Normally push up values, predictions, game time
		}
		
		
		
		
		
		
		
		
		
		//List<String> predictedEvents = getPredictedEvents();
		//Feed into inference and get out actual
		
		
		
		
		//Analyse the events (remove unnecessary ones)
		
		if(recorder.isRunning())
		{
			recorderTimer.cancel();
		}

		notifyObservers(gameUpdate);
		iterationCount++;
	}
	
	
	private String getMostRecentTimeStamp()
	{
		for(int i = 0; i < predictionModel.size(); i++)
		{
			if(!predictionModel.get(i).isClosed())
			{
				if(predictionModel.get(i).getMostRecentTime() != null)
				{
					return predictionModel.get(i).getMostRecentTime();
				}
			}
		}
		return null;
	}
	
	private PredictionModel getModelForMarket(String name)
	{
		for(PredictionModel predictor : predictionModel)
		{
			if(predictor.getMarketName().equals(name))
			{
				return predictor;
			}
		}
		return null;
	}
	
	private BetfairMarketItem getMarketProbabilities(String marketName,
			List<BetfairMarketItem> marketProbabilities)
	{
		for(BetfairMarketItem marketProbability : marketProbabilities)
		{
			if(marketProbability.getMarketName().equals(marketName))
			{
				return marketProbability;
			}
		}
		return null;
	}

	private void informClosedPredictors(List<String> closedMarkets)
	{
		justClosed = new ArrayList<PredictionModel>();
		//For each prediction model
		for(String closedMarket: closedMarkets)
		{
			for(int i = 0; i < predictionModel.size(); i++)
			{
				if(predictionModel.get(i).getMarketName().equals(closedMarket))
				{
					predictionModel.get(i).close();
					justClosed.add(predictionModel.get(i));
					predictionModel.remove(i);
					i--;
				}
			}
		}
		
		//System.out.println(closedMarkets == null);
		for(String closedMarketId: closedMarkets)
		{
			String marketName = closedMarketId.split(",")[0];
			long closedTime = Long.valueOf(closedMarketId.split(",")[1]);
			//System.out.println("NAME IS " + marketName);
			//System.out.println(marketName.e);
			if(marketName.equals(MarketNames.HALF_TIME.toString()))
			{
				PredictionModel.setFirstHalfTimeEnd(closedTime);
			}
			System.out.println("CLOSED MARKET " + closedMarketId);
//			for(PredictionModel model : predictionModel)
//			{
//				if(model.getMarketName().equals(closedMarketId))
//				{
//					model.addData(null);
//				}
//			}
		}
	}
	
	private List<String> getPredictedEvents()
	{
		List<String> predictedEvents = new ArrayList<String>();
		
		for(PredictionModel justClosedPredictors : justClosed)
		{
			predictedEvents.addAll(justClosedPredictors.getPreds());
		}
		
		//Go through each markets model and poll for data
		for(PredictionModel marketPredictionModel : predictionModel)
		{
			predictedEvents.addAll(marketPredictionModel.getPreds());
		}
		if(predictedEvents.size() > 0)
		{
			for(String event : predictedEvents)
			{
				System.out.println("EVENT PREDICTED " + event);
			}
		}
		justClosed.clear();
		
		
		//HERE WE NEED TO FEED INTO ANOTHER METHOD TO CONVERT CLOSED MARKET EVENTS TO ACTUAL EVENTS
		
		return predictedEvents;
	}
	
	/**
	 * Adds data to existing predictor objects
	 * @param marketProbabilities List of market probabilities to be given to predictors
	 */
	private void addDataToPredictors(List<BetfairMarketItem> marketProbabilities)
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

	
	private void addToExistingPredictionModel(List<BetfairMarketItem> marketProbabilities)
	{
		//System.out.println("Its trying to add to the predictor model.");

		//For each market we're tracking (this can include closed markets)
		for(int i = 0; i < predictionModel.size(); i++)
		{
			//System.out.println("iter through market " + i);
			//For each market we're getting data for (active)
			for(int j = 0; j < marketProbabilities.size(); j++)
			{
				//If the market names match, give it the relevant data and break (the market is still live)
				if(marketProbabilities.get(j).getMarketName().equals(predictionModel.get(i).getMarketName()))
				{
					//System.out.println("Adding data for " + marketProbabilities.get(j).getMarketName());
					//System.out.println("Adding data for market " + marketProbabilities.get(j).getMarketName());
					predictionModel.get(i).addData(marketProbabilities.get(j).getProbabilities());
					break;
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