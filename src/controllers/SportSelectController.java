package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.ProgramOptions;
import views.BetFairView;
import views.MarketSelectView;
import views.SportSelectView;

public class SportSelectController implements ActionListener
{
	private SportSelectView view;
	private ProgramOptions options;
	
	public SportSelectController(ProgramOptions options, SportSelectView view)
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
				BetFairView marketSelectView = new MarketSelectView(options);
			}	
			else
				JOptionPane.showMessageDialog(view.getFrame(), "Please select a sport");
				
		}
		else if(e.getActionCommand().equals("back"))
		{
			System.out.println("b");
		}
		else
		{
			System.out.println(e.getActionCommand());
		}
		//get selected sport from the option list, add tha tto the options...
		//create new view and pass it in options
			
		//if next is pressed collapse old new, make new view and pass around betfair ref
		
		//if back is pressed then collapse all, remove betfair init and go back to login view
	}
}
