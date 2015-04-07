package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import model.ProgramOptions;
import views.AnalysisView;
import views.BetFairView;
import views.GameSelectView;
import views.MarketSelectView;

/**
 * Controller class for MarketSelectView objects
 * @author Craig Thomson
 *
 */
public class MarketSelectController extends ViewController
{
	private List<String> defaultMarketList;
	
	/**
	 * Create a MarketSelectController object
	 * @param options The GUIs ProgramOptions object, which stores UI choices
	 * @param view The view that the controller is to listen to
	 */
	public MarketSelectController(ProgramOptions options, BetFairView view)
	{
		super(options, view);
		defaultMarketList = new ArrayList<String>();
		populateDefaultMarketList();
	}

	//This ideally shouldn't be here but I can't decide what class I should
	//put a static method to get the tracked market list in.
	private void populateDefaultMarketList()
	{
		defaultMarketList.add("Match Odds");
		defaultMarketList.add("Sending Off");
		defaultMarketList.add("Correct Score");
		defaultMarketList.add("First Goalscorer");
		defaultMarketList.add("Half Time");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//If next button is pressed (transition to next view)
		if(e.getActionCommand().equals("next"))
		{
			//Get the selected options from the view
			options = view.getOptions();
			
			//If markets are selected
			if(options.getMarketIds() != null)
			{
				view.closeView();
				BetFairView analysisView = new AnalysisView(options);
			}	
			else
				JOptionPane.showMessageDialog(view.getFrame(), "Please select one or more market(s)");	
		}
		else if(e.getActionCommand().equals("Use defaults"))
		{
			selectDefaultOptions();
		}
		else if(e.getActionCommand().equals("back"))
		{
			view.closeView();
			options.setEventId(null);
			BetFairView analysisView = new GameSelectView(options);
		}
	}

	/**
	 * Method used to select the supported markets that exist on the list of markets.
	 */
	private void selectDefaultOptions()
	{
		JTable marketTable = ((MarketSelectView) view).getMarketTable();
		marketTable.clearSelection();
		for(int i = 0; i < marketTable.getModel().getRowCount(); i++)
		{
			if(defaultMarketList.contains(marketTable.getModel().getValueAt(i, 0)))
			{
				marketTable.changeSelection(i, 0, true, false);
			}
		}
	}
}