package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ISimpleBetFair;
import model.ProgramOptions;
import model.SimpleBetFair;
import views.BetFairView;
import views.LoginView;
import views.SportSelectView;
import views.TestFileSelectionView;
import exceptions.BadLoginDetailsException;
import exceptions.CryptoException;

/**
 * Controller class for LoginView objects
 * @author Craig Thomson
 *
 */
public class LoginController implements ActionListener
{
	private ISimpleBetFair betFair;
	private BetFairView view;
	private ProgramOptions options;
	
	/**
	 * 
	 * @param loginView A reference to the view that created this project
	 */
	public LoginController(BetFairView loginView)
	{
		betFair = new SimpleBetFair(false);
		view = loginView;
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
		else if(e.getActionCommand().equalsIgnoreCase("select certificate file"))
		{
			openFileLocator();
		}
	}
	
	/**
	 * Brings up a file locator for .p12 certificate files.
	 */
	private void openFileLocator()
	{
		JFileChooser fileLocator = new JFileChooser();
		fileLocator.setFileFilter(new FileNameExtensionFilter("Personal Information Exchange Files (*.p12)","p12"));
		
		if(fileLocator.showOpenDialog(view.getFrame()) == JFileChooser.APPROVE_OPTION)
		{
			File certificateFile = fileLocator.getSelectedFile();
			((LoginView) view).setFileLocation(certificateFile.getPath());
		}
	}

	/**
	 * Deals with the event of the log in button being pressed
	 */
	private void loginPress()
	{
		options = view.getOptions();
		betFair = options.getBetFair();
		
		//If its in test mode then we don't need to log in
		if(options.getTestMode())
		{
			view.closeView();
			BetFairView testView = new TestFileSelectionView(options);
		}
		else
		{
			try
			{
				betFair.setDebug(options.getDebugMode());
				System.out.println(options.getCertificateFile());
				String response = betFair.login(options.getUsername(), options.getPassword(), options.getFilePassword(), options.getCertificateFile());
	
				//If successful log in
				if(response.equalsIgnoreCase("success"))
				{	
					view.closeView();
					BetFairView nextView = new SportSelectView(options);
				}
				//Anything other than success should throw an run time exception which is caught below.
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
}