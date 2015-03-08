package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.ProgramOptions;
import views.GameSelectView;
import views.SportSelectView;

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
		if(e.getActionCommand().equals("next"))
		{
			
		}
		else if(e.getActionCommand().equals("back"))
		{
			
		}	
	}
}