package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ProgramOptions;
import model.TestFile;
import views.AnalysisView;
import views.BetFairView;
import views.LoginView;
import views.TestFileSelectionView;

/**
 * Controller class for TestFileSelectionView objects
 * @author Craig Thomson
 *
 */
public class TestFileLocatorController extends ViewController
{
	
	/**
	 * Create a TestFileLocatorController object
	 * @param view BetFairView that the controller listens for
	 */
	public TestFileLocatorController(BetFairView view)
	{
		super(null, view);
	}
	

	public void actionPerformed(ActionEvent e)
	{
		//If next button is pressed
		if(e.getActionCommand().equals("next"))
		{
			//Grab selected options from view
			ProgramOptions options = view.getOptions();

			//if a file has been selected
			if(options.getTestFile() != null)
			{
				view.closeView();
				BetFairView nextView = new AnalysisView(new TestFile(options.getTestFile()));
			}
			else
			{
				JOptionPane.showMessageDialog(view.getFrame(), "No testfile has been selected");	
			}
		}
		else if(e.getActionCommand().equals("Locate"))
		{
			openFileLocator();
		}
		else if(e.getActionCommand().equals(("back")))
		{
			view.closeView();
			BetFairView nextView = new LoginView(new ProgramOptions());
		}
	}
	
	/**
	 * Brings up a file locator for .p12 certificate files.
	 */
	private void openFileLocator()
	{
		JFileChooser fileLocator = new JFileChooser();
		fileLocator.setFileFilter(new FileNameExtensionFilter("Betfair JSON game logs (*.txt)","txt"));
		
		//If a file has been selected
		if(fileLocator.showOpenDialog(view.getFrame()) == JFileChooser.APPROVE_OPTION)
		{
			File certificateFile = fileLocator.getSelectedFile();
			((TestFileSelectionView) view).setFileLocation(certificateFile.getPath());
		}
	}

}
