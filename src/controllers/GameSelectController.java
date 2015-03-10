package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.ProgramOptions;
import views.BetFairView;
import views.GameAnalysisView;
import views.GameSelectView;
import views.MarketSelectView;
//TODO make 
public class GameSelectController implements ActionListener
{
	private GameSelectView view;
	private ProgramOptions options;
	
	public GameSelectController(ProgramOptions options, GameSelectView view)
	{
		this.options = options;
		this.view = view;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		options = view.getOptions();

		if(e.getActionCommand().equals("next"))
		{
			if(options.getEventId() != null)
			{
				view.closeView();
				if(options.getCollectionMode())
				{
					BetFairView marketSelectView = new MarketSelectView(options);
				}
				else
				{
					BetFairView gameAnalysisView = new GameAnalysisView(options);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(view.getFrame(), "Please select a game");
			}
		}
		else if(e.getActionCommand().equals("back"))
		{
			System.out.println("b");
		}	
	}
}