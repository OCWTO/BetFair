package model;

import java.util.ArrayList;
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
	private PredictionModel predictionModel;
	
	public DataAnalysis(ProgramOptions options)
	{
		this.options = options;
		observers = new ArrayList<Observer>();
		recorder = new GameRecorder(options);
		recorder.addObserver(this);
		predictionModel = PredictionModelFactory.getModel(options.getEventTypeId());
	}
	
	public void start()
	{
		Timer timer = new Timer();
		timer.schedule(recorder, recorder.getStartDelayInMS(), 5000);
	}
	
	/*TASKS
	 * On creation it gets programoptions 
	 * switch eventtype on factory class to get relevant model
	 * create gamerecorder with parameters
	 * make this observe that and this is observed by the view
	 * 
	 * 
	 */
	
	
	//This class receives events, throws data into analysis and throws old events and new ones generated up


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

	@Override
	public void update(Object obj)
	{
		System.out.println("getting updated");
		//THIS is when data is received
		//current idea is for each market passed up we update
		//if its not passed up then we assume its closed but problem
	}
}
//TODO find a cut off value to decide an event? then event is decided by looking at the name and finding the mapping to it