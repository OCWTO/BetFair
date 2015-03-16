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
			if(options.getMarketIds() != null)
			{
				view.closeView();
				
				//if(options.getCollectionMode())
				//{
				//	System.out.println("collect mode so pick markets");
					BetFairView marketSelectView = new MarketSelectView(options);
				//}
//				else
//				{
//					System.out.println("not in collect so go to game analysis");
//					BetFairView gameAnalysisView = new GameAnalysisView(options);
//				}
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