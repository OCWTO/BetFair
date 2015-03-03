package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.BetFairCore;
import model.ProgramOptions;
import views.SportSelectView;

public class SportSelectController implements ActionListener
{
	private BetFairCore betFair;
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
		//get selected sport from the option list, add tha tto the options...
		//create new view and pass it in options
			
		//if next is pressed collapse old new, make new view and pass around betfair ref
		
		//if back is pressed then collapse all, remove betfair init and go back to login view
	}
}
