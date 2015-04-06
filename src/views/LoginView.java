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
	 * Create a LoginView object
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
		JPanel detailsPanel = new JPanel();
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		JLabel userNameLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		JLabel certPasswordLabel = new JLabel("File Password:");
		labelPanel.add(userNameLabel);
		labelPanel.add(passwordLabel);
		labelPanel.add(certPasswordLabel);
		
		JPanel textFieldPanel = new JPanel();
		textFieldPanel.setLayout(new BoxLayout(textFieldPanel, BoxLayout.Y_AXIS));
		usernameEntry = new JTextField();
		usernameEntry.addActionListener(viewListener);
		usernameEntry.setText("0ocwto0");
		passwordEntry = new JPasswordField();
		passwordEntry.setText("2014Project");
		filePasswordEntry = new JPasswordField();
		filePasswordEntry.setText("project");
		textFieldPanel.add(usernameEntry);
		textFieldPanel.add(passwordEntry);
		textFieldPanel.add(filePasswordEntry);
		
		detailsPanel.add(labelPanel, BorderLayout.WEST);
		detailsPanel.add(textFieldPanel, BorderLayout.EAST);

		mainContainer.add(detailsPanel);
	}
	
	/**
	 * Sets up panel with option checkboxes and a login button.
	 */
	private void setupOptionsPanel()
	{		
		JPanel certificatePanel = new JPanel();
		certificatePanel.setLayout(new BoxLayout(certificatePanel, BoxLayout.Y_AXIS));
		
		JPanel certificateFilePanel = new JPanel();
		JButton locateCertificateFile = new JButton("Select Certificate File");
		locateCertificateFile.addActionListener(viewListener);
		certificateFilePanel.add(locateCertificateFile, BorderLayout.NORTH);
		fileLocation = new JLabel("./certs/client-2048.p12");
		certificateFilePanel.add(fileLocation, BorderLayout.CENTER);
		certificatePanel.add(certificateFilePanel);
		
		mainContainer.add(certificatePanel);
/////////////////////
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		
		JPanel checkBoxPanel = new JPanel();		
		debugCheckBox = new JCheckBox("Debug mode");
		debugCheckBox.addActionListener(viewListener);
		testmodeCheckBox = new JCheckBox("Test mode");
		testmodeCheckBox.addActionListener(viewListener);
		checkBoxPanel.add(debugCheckBox, BorderLayout.NORTH);
		checkBoxPanel.add(testmodeCheckBox, BorderLayout.SOUTH);
		optionsPanel.add(checkBoxPanel);
		
		mainContainer.add(optionsPanel);
		
		JPanel loginPanel = new JPanel();
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(viewListener);
		loginPanel.add(loginButton, BorderLayout.CENTER);
		
		mainContainer.add(loginPanel);
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