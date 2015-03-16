package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.ProgramOptions;
import views.BetFairView;
import views.GameAnalysisView;

public class MarketSelectController implements ActionListener
{
	private BetFairView view;
	private ProgramOptions options;
	
	public MarketSelectController(ProgramOptions options, BetFairView view)
	{
		this.view = view;
		this.options = options;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		options = view.getOptions();
		
		if(e.getActionCommand().equals("next"))
		{
			if(options.getMarketIds() != null)
			{
				view.closeView();
				BetFairView analysisView = new GameAnalysisView(options);
			}	
			else
				JOptionPane.showMessageDialog(view.getFrame(), "Please select one or more market(s)");	
		}
		else if(e.getActionCommand().equals("back"))
		{
			System.out.println("b");
		}
	}
}