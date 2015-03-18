package model;

import java.util.ArrayList;
import java.util.List;

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
		observers = new ArrayList<Observer>();
		recorder = new GameRecorder(options);
		this.options = options;
		predictionModel = PredictionModelFactory.getModel(options.getEventTypeId());
		recorder = new GameRecorder(options);
	}
	
	/*TASKS
	 * On creation it gets programoptions 
	 * switch eventtype on factory class to get relevant model
	 * create gamerecorder with parameters
	 * make this observe that and this is observed by the view
	 * 
	 * 
	 */


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
		//THIS is when data is received
		//current idea is for each market passed up we update
		//if its not passed up then we assume its closed but problem
	}
}
//TODO find a cut off value to decide an event? then event is decided by looking at the name and finding the mapping to it