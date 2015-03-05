package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.ISimpleBetFair;
import model.ProgramOptions;
import model.SimpleBetFairCore;
import views.BetFairView;
import views.LoginView;
import views.SportSelectView;
import exceptions.BadLoginDetailsException;
import exceptions.CryptoException;

public class LoginController implements ActionListener
{
	private ISimpleBetFair betFair;
	private BetFairView view;
	private ProgramOptions options;
	
	/**
	 * 
	 * @param betFair2
	 * @param loginView
	 */
	public LoginController(LoginView loginView)
	{
		betFair = new SimpleBetFairCore(false);
		view = loginView;
		//options = view.getOptions();
	}

	/**
	 * Deals with button presses and checkbox events
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equalsIgnoreCase("login"))
		{
			loginPress();
		}
	}
	
	private void loginPress()
	{
		options = view.getOptions();
		betFair = options.getBetFair();

		try
		{
			betFair.setDebug(options.getDebugMode());
			String response = betFair.login(options.getUsername(), options.getPassword(), options.getFilePassword());

			if(response.equalsIgnoreCase("success"))
			{	
					view.closeView();
					BetFairView nextView = new SportSelectView(options);
			}
			//Anything other than success should throw an exception which is caught below.
		} 
		catch (CryptoException badCertPasswordException)
		{
			JOptionPane.showMessageDialog(view.getFrame(), badCertPasswordException.getMessage());
		}
		catch(BadLoginDetailsException badDetailsException)
		{
			JOptionPane.showMessageDialog(view.getFrame(), badDetailsException.getMessage());
		}
	}
}