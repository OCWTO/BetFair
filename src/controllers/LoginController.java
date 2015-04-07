package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ISimpleBetFair;
import model.ProgramOptions;
import model.SimpleBetfair;
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
public class LoginController extends ViewController
{
	private ISimpleBetFair betFair;
	
	/**
	 * Create a LoginController Object
	 * @param loginView A reference to the view that created this project
	 * 
	 */
	public LoginController(BetFairView view)
	{
		super(null, view);
		betFair = new SimpleBetfair(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		//If log in button is pressed...
		if(e.getActionCommand().equalsIgnoreCase("login"))
		{
			loginPress();
		}
		//If user wants to locate a certificate
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
			if(options.getCertificateFile().exists())
			{
				try
				{
					betFair.setDebug(options.getDebugMode());
					String response = betFair.login(options.getUsername(), options.getPassword(), options.getFilePassword(), options.getCertificateFile());
		
					//If successful log in
					if(response.equalsIgnoreCase("success"))
					{	
						view.closeView();
						BetFairView nextView = new SportSelectView(options);
					}
				} 
				//Anything other than success should throw an run time exception which is caught below.
				catch (CryptoException badCertPasswordException)
				{
					JOptionPane.showMessageDialog(view.getFrame(), badCertPasswordException.getMessage());
				}
				catch(BadLoginDetailsException badDetailsException)
				{
					JOptionPane.showMessageDialog(view.getFrame(), badDetailsException.getMessage());
				}
			}
			else
			{
				JOptionPane.showMessageDialog(view.getFrame(), "Certificate file not found at location");
			}
		}
	}
}