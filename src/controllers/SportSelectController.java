package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.ProgramOptions;
import views.BetFairView;
import views.GameSelectView;
import views.LoginView;

/**
 * Controller class for SportSelectView objects
 * @author Craig Thomson
 *
 */
public class SportSelectController implements ActionListener
{
	private BetFairView view;
	private ProgramOptions options;
	
	public SportSelectController(ProgramOptions options, BetFairView view)
	{
		
		this.options = options;
		this.view = view;
	}


	public void actionPerformed(ActionEvent e)
	{
		//Get the selected options
		options = view.getOptions();
		
		//If next button was pressed
		if(e.getActionCommand().equals("next"))
		{
			//If a sport has been selected then transition
			if(options.getEventTypeId() != null)
			{
				view.closeView();
				BetFairView sportSelectView = new GameSelectView(options);
			}	
			else
				JOptionPane.showMessageDialog(view.getFrame(), "Please select a sport");	
		}
		else if(e.getActionCommand().equals("back"))
		{
			view.closeView();
			BetFairView nextView = new LoginView(new ProgramOptions());
		}
	}
}