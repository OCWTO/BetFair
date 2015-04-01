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
import views.MarketSelectView;

/**
 * Controller class for MarketSelectView objects
 * @author Craig
 *
 */
public class MarketSelectController implements ActionListener
{
	private BetFairView view;
	private ProgramOptions options;
	private List<String> defaultMarketList;
	
	public MarketSelectController(ProgramOptions options, BetFairView view)
	{
		this.view = view;
		this.options = options;
		defaultMarketList = new ArrayList<String>();
		populateDefaultMarketList();
	}

	//This really shouldn't be here but I can't decide what class I should
	//put a static method to get the supported market list in.
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
		if(e.getActionCommand().equals("next"))
		{
			options = view.getOptions();
			if(options.getMarketIds() != null)
			{
				System.out.println(options.getMarketIds());
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
			//TODO implement back option
		}
	}

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