package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.ISimpleBetFair;
import model.SimpleBetFairCore;
import controllers.LoginController;

/**
 * This class represents the users log in view to the program
 * @author Craig Thomson
 *
 */
public class LoginView
{
	private JFrame guiFrame;
	private Container mainFrame;
	
	private final int xSize = 250;
	private final int ySize = 450;
	
	private final String frameTitle = "BetFair Login";
	
	private ISimpleBetFair betFair;
	private ActionListener guiListener;
	
	private JTextField usernameEntry;
	private JPasswordField passwordEntry;
	private JPasswordField filePasswordEntry;
	
	/**
	 * 
	 */
	public LoginView()
	{	
		betFair = new SimpleBetFairCore(false);
		guiListener = new LoginController(betFair, this);
		guiFrame = new JFrame(frameTitle);
		guiFrame.setResizable(false);
		mainFrame = guiFrame.getContentPane();
		mainFrame.setLayout(new BoxLayout(guiFrame.getContentPane(), BoxLayout.Y_AXIS));
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setupPanels();
		addMenus();
		centreFrame();
		
		guiFrame.setVisible(true);	
	}
	
	public void closeView()
	{
		guiFrame.setVisible(false);
		guiFrame.dispose();
	}
	
	public String[] getValues()
	{
		String[] usernameAndPass = new String[3];
		usernameAndPass[0] = usernameEntry.getText();
		usernameAndPass[1] = new String(passwordEntry.getPassword());
		usernameAndPass[2] = new String(filePasswordEntry.getPassword());
		return usernameAndPass;
	}
	
	public JFrame getFrame()
	{
		return guiFrame;
	}
	
	/**
	 * 
	 */
	private void setupPanels()
	{
		setupLogoPanel();
		setupDetailsPanel();
		setupOptionsPanel();
	}
	
	/**
	 * Sets up a panel containing a logo on the login view.
	 */
	private void setupLogoPanel()
	{
		JPanel logoPanel = new JPanel();
		logoPanel.setBackground(Color.RED);
		mainFrame.add(logoPanel);
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
		usernameEntry.addActionListener(guiListener);
		usernameEntry.setText("USERNAME");
		userNamePanel.add(userNameField, BorderLayout.EAST);
		userNamePanel.add(usernameEntry, BorderLayout.WEST);
		loginPanel.add(userNamePanel);
		
		//Password pane, label - field
		JPanel passwordPanel = new JPanel();
		JLabel PasswordField = new JLabel("Password:");
		passwordEntry = new JPasswordField();
		passwordEntry.setText("PASSWORD");
		passwordPanel.add(PasswordField, BorderLayout.EAST);
		passwordPanel.add(passwordEntry, BorderLayout.WEST);
		loginPanel.add(passwordPanel);
		
		//Certificate file password pane, label - field
		JPanel filePasswordPanel = new JPanel();
		JLabel filePasswordField = new JLabel("Password:");
		filePasswordEntry = new JPasswordField();
		filePasswordEntry.setText("File password");
		filePasswordPanel.add(filePasswordField, BorderLayout.EAST);
		filePasswordPanel.add(filePasswordEntry, BorderLayout.WEST);
		loginPanel.add(filePasswordPanel);
		
		//Add to main panel
		mainFrame.add(loginPanel);
	}
	
	/**
	 * Sets up panel with option checkboxes and a login button.
	 */
	private void setupOptionsPanel()
	{
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		JPanel debugPanel = new JPanel();
		JCheckBox debugCheckBox = new JCheckBox("Debug mode");
		debugCheckBox.addActionListener(guiListener);
		debugPanel.add(debugCheckBox, BorderLayout.EAST);
		optionsPanel.add(debugPanel);
		
		JPanel collectionPanel = new JPanel();
		JCheckBox collectionCheckBox = new JCheckBox("Collection mode");
		collectionCheckBox.addActionListener(guiListener);
		collectionPanel.add(collectionCheckBox, BorderLayout.EAST);
		optionsPanel.add(collectionPanel);
		
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(guiListener);
		
		mainFrame.add(optionsPanel);
		mainFrame.add(loginButton);
	}
	
	/**
	 * 
	 */
	private void addMenus()
	{
		//File->exit, File->About, Help->?? user guide?
	}
	
	
	/**
	 * 
	 */
	private void centreFrame()
	{
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		guiFrame.setBounds((int) screenDims.getWidth() / 2 - (xSize / 2),
						(int) screenDims.getHeight() / 2 - (ySize / 2), xSize, ySize);
	}
	
	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		LoginView gui = new LoginView();
	}
}