package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.ProgramOptions;
import model.SimpleBetFair;
import controllers.LoginController;

/**
 * This class represents the users log in view to the program
 * @author Craig Thomson
 *
 */
//TODO add option to put in cert files
public class LoginView extends BetFairView
{	
	private static final int xSize = 250;
	private static final int ySize = 450;
	private static final String frameTitle = "BetFair Login";
	private JTextField usernameEntry;
	private JPasswordField passwordEntry;
	private JPasswordField filePasswordEntry;
	private JCheckBox debugCheckBox;
	private JCheckBox testmodeCheckBox;
	private JLabel fileLocation;
	/**
	 * 
	 */
	public LoginView(ProgramOptions options)
	{	
		super(frameTitle, options, null);
		super.setSize(new Dimension(xSize, ySize));
		super.viewListener = new LoginController(this);
		setupAndDisplay();
	}
	
	/**
	 * Sets up fields and labels for entered data
	 */
	private void setupDetailsPanel()
	{
		JPanel loginPanel = new JPanel();
		
		//Username panel, label - field
		JPanel userNamePanel = new JPanel();
		JLabel userNameField = new JLabel("Username:");
		usernameEntry = new JTextField();
		usernameEntry.addActionListener(viewListener);
		usernameEntry.setText("0ocwto0");
		userNamePanel.add(userNameField, BorderLayout.EAST);
		userNamePanel.add(usernameEntry, BorderLayout.WEST);
		loginPanel.add(userNamePanel);
		
		//Password pane, label - field
		JPanel passwordPanel = new JPanel();
		JLabel PasswordField = new JLabel("Password:");
		passwordEntry = new JPasswordField();
		passwordEntry.setText("2014Project");
		passwordPanel.add(PasswordField, BorderLayout.EAST);
		passwordPanel.add(passwordEntry, BorderLayout.WEST);
		loginPanel.add(passwordPanel);
		
		//Certificate file password pane, label - field
		JPanel filePasswordPanel = new JPanel();
		JLabel filePasswordField = new JLabel("File Password:");
		filePasswordEntry = new JPasswordField();
		filePasswordEntry.setText("project");
		filePasswordPanel.add(filePasswordField, BorderLayout.EAST);
		filePasswordPanel.add(filePasswordEntry, BorderLayout.WEST);
		loginPanel.add(filePasswordPanel);
		
		//Add to main panel
		mainContainer.add(loginPanel);
	}
	
	/**
	 * Sets up panel with option checkboxes and a login button.
	 */
	private void setupOptionsPanel()
	{
		JPanel optionsPanel = new JPanel();
		
		JPanel certificateFilePanel = new JPanel();
		JButton locateCertificateFile = new JButton("Select Certificate File");
		locateCertificateFile.addActionListener(viewListener);
		certificateFilePanel.add(locateCertificateFile, BorderLayout.EAST);
		fileLocation = new JLabel("C:/Users/Craig/Desktop/Workspace/BetFair/certs/client-2048.p12");
		optionsPanel.add(fileLocation);
		optionsPanel.add(certificateFilePanel);

		
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		JPanel checkBoxOptionsPanel = new JPanel();
		
		debugCheckBox = new JCheckBox("Debug mode");
		debugCheckBox.addActionListener(viewListener);
		testmodeCheckBox = new JCheckBox("Test mode");
		testmodeCheckBox.addActionListener(viewListener);
		//Ignore the BorderLayout position
		checkBoxOptionsPanel.add(debugCheckBox, BorderLayout.EAST);
		checkBoxOptionsPanel.add(testmodeCheckBox, BorderLayout.NORTH);
		optionsPanel.add(checkBoxOptionsPanel);
		
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(viewListener);
		
		mainContainer.add(optionsPanel);
		mainContainer.add(loginButton);
	}
	
	public void setFileLocation(String location)
	{
		fileLocation.setText(location);
	}

	@Override
	void setupPanels()
	{
		setupDetailsPanel();
		setupOptionsPanel();
	}

	/**
	 * Returns a ProgramOptions object. This object contains all of the important information that the user has entered on the LoginView UI. This includes
	 * login name, password, file password.
	 */
	@Override
	public ProgramOptions getOptions()
	{
		ProgramOptions currentOptions = new ProgramOptions();
		currentOptions.setDebugMode(debugCheckBox.isSelected());
		currentOptions.setTestMode(testmodeCheckBox.isSelected());
		currentOptions.addBetFair(new SimpleBetFair(currentOptions.getDebugMode()));
		currentOptions.setUserDetails(usernameEntry.getText(), new String(passwordEntry.getPassword()), new String(filePasswordEntry.getPassword()));
		currentOptions.setCertificateFile(new File(fileLocation.getText()));
		return currentOptions;
	}
	
	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		LoginView gui = new LoginView(new ProgramOptions());
	}
}