package views;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * Abstract class that is the superclass of all graphical views
 * @author Craig Thomson
 *
 */
public abstract class BetFairView
{
	private JFrame guiFrame;
	private int xSize = 900;
	private int ySize = 600;
	
	public BetFairView()
	{
		
	}
	
	private void setup()
	{
		//THIS WILL BE THE TEMPLATE METHOD
	}
	
	//menus
	
	private void centreFrame()
	{
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		guiFrame.setBounds((int) screenDims.getWidth() / 2 - (xSize / 2),
						(int) screenDims.getHeight() / 2 - (ySize / 2), xSize, ySize);
	}	
}