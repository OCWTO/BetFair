package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ISimpleBetFair;
import model.ProgramOptions;
import model.SimpleBetFairCore;
import views.BetFairView;
import views.LoginView;
import views.SportSelectView;
import exceptions.BadLoginDetailsException;
import exceptions.CryptoException;

/**
 * Listener class for LoginView
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
	public LoginController(LoginView loginView)
	{
		betFair = new SimpleBetFairCore(false);
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
	
	private void openFileLocator()
	{
		options = view.getOptions();
		
		JFileChooser fileLocator = new JFileChooser();
		fileLocator.setFileFilter(new FileNameExtensionFilter("Personal Information Exchange Files (*.p12)","p12"));
		
		if(fileLocator.showOpenDialog(view.getFrame()) == JFileChooser.APPROVE_OPTION)
		{
			File certificateFile = fileLocator.getSelectedFile();
			options.setCertificateFile(certificateFile);
		}
	}

	private void loginPress()
	{
		options = view.getOptions();
		betFair = options.getBetFair();

		try
		{
			betFair.setDebug(options.getDebugMode());
			String response = betFair.login(options.getUsername(), options.getPassword(), options.getFilePassword(), options.getCertificateFile());

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