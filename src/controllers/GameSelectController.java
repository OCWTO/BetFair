package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.ProgramOptions;
import views.BetFairView;
import views.MarketSelectView;

/**
 * Controller class for GameSelectView objects
 * @author Craig
 *
 */
public class GameSelectController implements ActionListener
{
	private BetFairView gameSelectView;
	private ProgramOptions options;
	
	public GameSelectController(ProgramOptions options, BetFairView view)
	{
		this.options = options;
		this.gameSelectView = view;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//Grab the options object from the view that contains their selected settings
		options = gameSelectView.getOptions();

		if(e.getActionCommand().equals("next"))
		{ 
			//If market ids have been selected then transition
			if(options.getMarketIds() != null)
			{
				gameSelectView.closeView();
				BetFairView marketSelectView = new MarketSelectView(options);
			}
			else
			{
				JOptionPane.showMessageDialog(gameSelectView.getFrame(), "Please select a game");
			}
		}
		else if(e.getActionCommand().equals("back"))
		{
			//TODO implement back option
		}
	}
}