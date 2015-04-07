package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import enums.MarketNames;

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
	private String eventTypeId;
	
	public DataAnalysis(ProgramOptions options)
	{
		eventTypeId = options.getEventTypeId();
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
		eventTypeId = testFile.getOptions().getEventTypeId();
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
	private void initPredictionModel(List<BetfairMarketItem> marketProbabilities)
	{
		//For each market we have
		for(BetfairMarketItem marketProb : marketProbabilities)
		{
			//Create prediction model for the market
			PredictionModel marketModel = PredictionModelFactory.getModel(marketProb.getMarketName(), gameStartTime, eventTypeId);
			//add data for the market to the model
			marketModel.initialize(marketProb.getProbabilities());
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
		BetfairMarketItem matchOddsProbs = getMarketProbabilities(MarketNames.MATCH_ODDS.toString(), marketProbabilities);

		gameStartTime = events.getStartTime();


		//Add data to the prediction models
		addDataToPredictors(marketProbabilities);
		informClosedPredictors(events.getClosedMarkets());
		//Get predicted events
		List<String> predictedEvents = getPredictedEvents();
		
		ViewUpdate gameUpdate = null;
		
		//if the 1st iteration
		if(iterationCount == 1)
		{
			String homeTeam = matchOddsProbs.getProbabilities().get(0).getRunnerName();
			String awayTeam =  matchOddsProbs.getProbabilities().get(1).getRunnerName();
			String favouredTeam = "";
			String favouredGoalScorer = "";
			for(PredictionModel predictor : predictionModel)
			{
				if(predictor.getMarketName().equals(MarketNames.MATCH_ODDS.toString()))
				{
					favouredTeam = predictor.getFavouredRunner();
				}
				if(predictor.getMarketName().equals(MarketNames.FIRST_GOALSCORER.toString()))
				{
					favouredGoalScorer = predictor.getFavouredRunner();
				}
				
			}
			List<String> recentVals = getModelForMarket(MarketNames.MATCH_ODDS.toString()).getRecentValues();
			String lastUpdate = getMostRecentTimeStamp();
			if(lastUpdate == null)
			{
				lastUpdate = "Not started yet";
			}
			predictedEvents.add(lastUpdate + "," + "GAME STARTED ," + "");
			predictedEvents.add(lastUpdate + "," + "FAVOURED GOALSCORER IS ," + favouredGoalScorer);

			gameUpdate = new ViewUpdate(homeTeam, awayTeam, favouredTeam, gameStartTime.toString(), lastUpdate, LocalTime.now().toString(), recentVals, predictedEvents);
		}
		else
		{
			
			String lastUpdate = getMostRecentTimeStamp();
			
			List<String> probValues = null;
			
			if(getModelForMarket(MarketNames.MATCH_ODDS.toString()) != null)
			{
				probValues = getModelForMarket(MarketNames.MATCH_ODDS.toString()).getRecentValues();
			}
			gameUpdate = new ViewUpdate(lastUpdate, LocalTime.now().toString(), probValues, predictedEvents);
		}
		//TODO do a little inference
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
		for(PredictionModel predictor : justClosed)
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

	/**
	 * Notify predictors which markets have closed. Which indicate events
	 * @param closedMarkets The list of market names that have just closed
	 */
	private void informClosedPredictors(List<String> closedMarkets)
	{
		//Half Time,1428168018002 IN THE IND 0
		justClosed = new ArrayList<PredictionModel>();
		
		//For each closed name
		for(String closedMarket: closedMarkets)
		{
			for(int i = 0; i < predictionModel.size(); i++)
			{
				//if the prediction model represents the closed market
				if(predictionModel.get(i).getMarketName().equals(closedMarket.split(",")[0]))
				{
					predictionModel.get(i).closeMarket();
					justClosed.add(predictionModel.remove(i));
					i--;
				}
			}
		}
		//For each of the closed markets
		for(String closedMarketId: closedMarkets)
		{
			String marketName = closedMarketId.split(",")[0];
			long closedTime = Long.valueOf(closedMarketId.split(",")[1]);
			if(marketName.equals(MarketNames.HALF_TIME.toString()))
			{
				FootballPredictionModel.setFirstHalfTimeEnd(closedTime);
			}
		}
	}
	
	private List<String> getPredictedEvents()
	{
		List<String> predictedEvents = new ArrayList<String>();
		
		//For all recently closed events, get their predictions/notifications
		for(PredictionModel justClosedPredictors : justClosed)
		{
			predictedEvents.addAll(justClosedPredictors.getPredictions());
		}
		
		//Go through each markets model and poll for data
		for(PredictionModel marketPredictionModel : predictionModel)
		{
			predictedEvents.addAll(marketPredictionModel.getPredictions());
		}

		//Clear list of closed prediction models, so they are no longer queried
		//justClosed.clear();
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

	/**
	 * Add the given data to the existing prediction model for the market
	 * @param marketProbabilities
	 */
	private void addToExistingPredictionModel(List<BetfairMarketItem> marketProbabilities)
	{
		//For each market we're tracking (this can include closed markets)
		for(int i = 0; i < predictionModel.size(); i++)
		{
			//For each market we're getting data for (active)
			for(int j = 0; j < marketProbabilities.size(); j++)
			{
				//If the market names match, give it the relevant data and break (the market is still live)
				if(marketProbabilities.get(j).getMarketName().equals(predictionModel.get(i).getMarketName()))
				{
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