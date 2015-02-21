package views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * 
 * @author Craig
 *
 */
public class LoginView
{
	private JFrame guiFrame;
	private Container mainFrame;
	
	private final int xSize = 250;
	private final int ySize = 450;
	
	private final String frameTitle = "BetFair Login";
	//TODO keep betfair here?
	//TODO method to get values of checkboxes
	//TODO controller gets values and decides what to do, 
	/**
	 * 
	 */
	public LoginView()
	{	
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
		JTextField usernameEntry = new JTextField();
		usernameEntry.setText("USERNAME");
		userNamePanel.add(userNameField, BorderLayout.EAST);
		userNamePanel.add(usernameEntry, BorderLayout.WEST);
		loginPanel.add(userNamePanel);
		
		//Password pane, label - field
		JPanel passwordPanel = new JPanel();
		JLabel PasswordField = new JLabel("Password:");
		JPasswordField passwordEntry = new JPasswordField();
		passwordEntry.setText("PASSWORD");
		passwordPanel.add(PasswordField, BorderLayout.EAST);
		passwordPanel.add(passwordEntry, BorderLayout.WEST);
		loginPanel.add(passwordPanel);
		
		//Add to main panel
		mainFrame.add(loginPanel);
	}
	
	/**
	 * Sets up panel with option checkboxes and a login button.
	 */
	private void setupOptionsPanel()
	{
		JPanel optionsPanel = new JPanel();
		

		
		JPanel debugPanel = new JPanel();
		JLabel debugLabel = new JLabel("Debug mode");
		JCheckBox debugCheckBox = new JCheckBox();
		debugPanel.add(debugLabel, BorderLayout.WEST);
		debugPanel.add(debugCheckBox, BorderLayout.EAST);
		optionsPanel.add(debugPanel);
		
		JPanel collectionPanel = new JPanel();
		JLabel collectionLabel = new JLabel("Collection mode");
		JCheckBox collectionCheckBox = new JCheckBox();
		collectionPanel.add(collectionLabel, BorderLayout.WEST);
		collectionPanel.add(collectionCheckBox, BorderLayout.EAST);
		optionsPanel.add(collectionPanel);
		
		
		JButton loginButton = new JButton("Login");
		
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
	
	public void closeView()
	{
		guiFrame.setVisible(false);
		guiFrame.dispose();
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
		LoginView gui = new LoginView();
	}
}