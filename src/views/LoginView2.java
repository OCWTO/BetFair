package views;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * 
 * @author Craig
 *
 */
public class LoginView2
{
	private JFrame guiFrame;
	private Container mainFrame;
	private final int xSize = 250;
	private final int ySize = 450;
	private final String frameTitle = "BetFair Login";

	/**
	 * 
	 */
	public LoginView2()
	{
		guiFrame = new JFrame(frameTitle);
		mainFrame = guiFrame.getContentPane();
		mainFrame.setLayout(new BoxLayout(guiFrame.getContentPane(), BoxLayout.Y_AXIS));
		setupPanels();
		addMenus();
		centreFrame();
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setVisible(true);	
	}
	
	/**
	 * 
	 */
	private void setupPanels()
	{
		//3 panels stacked vertically
		//1 for logo
		//2nd for login
		//3rd for checkboxes
	}
	
	/**
	 * 
	 */
	private void setupLogoFrame()
	{
		//Make one later?
	}
	
	/**
	 * 
	 */
	private void setupDetailsFrame()
	{
		//username + password fields + labels + file password stuff
	}
	
	/**
	 * 
	 */
	private void setupOptionsFrame()
	{
		//checkboxes and login
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
		LoginView2 gui = new LoginView2();
	}
}
