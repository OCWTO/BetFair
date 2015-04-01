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
import views.TestFileSelectionView;

public class TestFileLocatorController implements ActionListener
{
	private BetFairView view;
	
	public TestFileLocatorController(BetFairView view)
	{
		this.view = view;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("next"))
		{
			ProgramOptions options = view.getOptions();
			System.out.println(options.getTestFile() == null);
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
	}
	
	/**
	 * Brings up a file locator for .p12 certificate files.
	 */
	private void openFileLocator()
	{
		JFileChooser fileLocator = new JFileChooser();
		fileLocator.setFileFilter(new FileNameExtensionFilter("Betfair JSON game logs (*.txt)","txt"));
		
		if(fileLocator.showOpenDialog(view.getFrame()) == JFileChooser.APPROVE_OPTION)
		{
			File certificateFile = fileLocator.getSelectedFile();
			((TestFileSelectionView) view).setFileLocation(certificateFile.getPath());
		}
	}

}
