package views;

import model.ProgramOptions;

public class GameAnalysisView extends BetFairView
{
	private static final String frameTitle = "BetFair Game View";
	public GameAnalysisView(ProgramOptions options)
	{
		super(frameTitle, options, null);
		
		//new analysis view sooo
		//creates a recorder object, gives it betfair, observes it
		
		//Recorder will be observable
		//Class will sit above it and parse events
		//Class above it is observer and observable, it throws updates
		//after it receives events and nicely formats
		
	}

	@Override
	void setupPanels()
	{
		
	}

	@Override
	void addMenus()
	{
		
	}

}
	