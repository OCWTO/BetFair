package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.ProgramOptions;
import views.BetFairView;

/**
 * Abstract class that is the basis of all controllers for BetFairView objects
 * @author Craig Thomson
 *
 */
public abstract class ViewController implements ActionListener
{
	//The view that should contain components that this class is listening for
	protected BetFairView view;	
	/*
	 * options object that represents the GUI state. Say if on the market selected view
	 * then it is aware of the selected sport id, game id and contains the betfair object
	 * that will be used for the requests later on.
	 */
	protected ProgramOptions options;
	
	/**
	 * Create a ViewController Object
	 * @param options A reference to a ProgramOptions object that stores the UI state
	 * @param view A reference to the BetFairView object that this controller listens to
	 */
	public ViewController(ProgramOptions options, BetFairView view)
	{
		this.options = options;
		this.view = view;
	}

	@Override
	public abstract void actionPerformed(ActionEvent e);
}
