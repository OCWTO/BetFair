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

public class LoginView2
{
	private JFrame guiFrame;
	private Container mainFrame;
	private final int xSize = 250;
	private final int ySize = 450;
	private final String frameTitle = "BetFair Login";
	private JTextField textField;
	
	public LoginView2()
	{
		guiFrame = new JFrame(frameTitle);
		
		
		mainFrame = guiFrame.getContentPane();
		guiFrame.getContentPane().setLayout(new BoxLayout(guiFrame.getContentPane(), BoxLayout.X_AXIS));
		
		JLabel lblNewLabel = new JLabel("New label");
		guiFrame.getContentPane().add(lblNewLabel);
		
		textField = new JTextField();
		guiFrame.getContentPane().add(textField);
		textField.setColumns(10);
		setupPanels();
		addMenus();
		centreFrame();
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setVisible(true);	
	}
	
	private void setupPanels()
	{
		//3 panels stacked vertically
		//1 for logo
		//2nd for login
		//3rd for checkboxes
	}
	
	private void setupLogoFrame()
	{
		
	}
	
	private void setupDetailsFrame()
	{
		//username + password fields + labels + file password stuff
	}
	
	private void setupOptionsFrame()
	{
		//checkboxes and login
	}
	
	private void addMenus()
	{
		
	}
	
	private void centreFrame()
	{
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		guiFrame.setBounds((int) screenDims.getWidth() / 2 - (xSize / 2),
						(int) screenDims.getHeight() / 2 - (ySize / 2), xSize, ySize);
	}
}
