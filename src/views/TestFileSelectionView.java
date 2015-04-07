package views;

import java.awt.Dimension;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.ProgramOptions;
import controllers.TestFileLocatorController;

/**
 * Basic view for locating test files and then proceeding to simulate the program on them
 * @author Craig
 *
 */
public class TestFileSelectionView extends BetFairView
{
	private static final String frameTitle = "Test file locator view";
	private static final int xSize = 250;
	private static final int ySize = 450;
	private JLabel testFileLocation;
	
	public TestFileSelectionView(ProgramOptions options)
	{
		super(frameTitle, options, null);
		super.viewListener = new TestFileLocatorController(this);
		super.setSize(new Dimension(xSize, ySize));
		setupAndDisplay();
	}

	
	@Override
	void setupPanels()
	{
		setupInformationPanel();
		setupOptionsPanel();
	}

	private void setupOptionsPanel()
	{
		JPanel optionsPanel = new JPanel();
	
		JButton nextButton = new JButton("next");
		nextButton.addActionListener(viewListener);
		JButton backButton = new JButton("back");
		backButton.addActionListener(viewListener);
		optionsPanel.add(backButton);
		optionsPanel.add(nextButton);
		
		mainContainer.add(optionsPanel);
	}

	private void setupInformationPanel()
	{
		JPanel informationPanel = new JPanel();
		
		JLabel directionsLabel = new JLabel();
		directionsLabel.setText("Please select a test file by pressing on the locate button.");
		
		JButton locateButton = new JButton("Locate");
		locateButton.addActionListener(viewListener);
		
		testFileLocation = new JLabel();
		testFileLocation.setText("File location is : not set");
		
		informationPanel.setLayout(new BoxLayout(informationPanel,BoxLayout.Y_AXIS));
		informationPanel.add(directionsLabel);
		informationPanel.add(locateButton);
		informationPanel.add(testFileLocation);
		
		mainContainer.add(informationPanel);
	}

	public void setFileLocation(String path)
	{
		testFileLocation.setText(path);
	}
	
	@Override
	public ProgramOptions getOptions()
	{
		ProgramOptions options = super.getOptions();
		if(!testFileLocation.getText().equals("File location is : not set"))
		{
			options.setTestFile(new File(testFileLocation.getText()));
		}
		return options;
	}
}