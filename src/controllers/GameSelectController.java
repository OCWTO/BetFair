package controllers;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import model.ProgramOptions;
import views.BetFairView;
import views.MarketSelectView;
import views.SportSelectView;

/**
 * Controller class for GameSelectView objects
 * @author Craig Thomson
 *
 */
public class GameSelectController extends ViewController
{
	/**
	 * Create a GameSelectController Object
	 * @param options A reference to the ProgramOptions object that stores the current selection state
	 * @param view A reference to the view that created the controller
	 */
	public GameSelectController(ProgramOptions options, BetFairView view)
	{
		super(options, view);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		//Grab the options object from the view that contains their selected settings
		options = view.getOptions();

		//If the next button is pressed
		if(e.getActionCommand().equals("next"))
		{ 
			//If a game id has been selected then transition
			if(options.getEventId() != null)
			{
				view.closeView();
				BetFairView marketSelectView = new MarketSelectView(options);
			}
			else
			{
				JOptionPane.showMessageDialog(view.getFrame(), "Please select a game");
			}
		}
		else if(e.getActionCommand().equals("back"))
		{
			view.closeView();
			options.setEventId(null);
			BetFairView nextView = new SportSelectView(options);
		}
	}
}