package views;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import model.ISimpleBetFair;
import model.ProgramOptions;

/**
 * Abstract class that is the superclass of all graphical views
 * @author Craig Thomson
 *
 */
public abstract class BetFairView
{
	private JFrame guiFrame;
	protected Container mainContainer;
	protected ActionListener viewListener;
	private double xSize = 900;
	private double ySize = 600;
	protected ISimpleBetFair betFair;
	private ProgramOptions viewOptions;
	
	public BetFairView(String title, ProgramOptions programOptions, ActionListener listener)
	{
		viewOptions = programOptions;
		viewListener = listener;
		betFair = programOptions.getBetFair();
		guiFrame = new JFrame(title);
		guiFrame.setResizable(false);
		mainContainer = guiFrame.getContentPane();
		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Template pattern here deciding the flow of calls that occur before a view becomes visible.
	 */
	protected void setupAndDisplay()
	{
		setupPanels();
		addMenus();
		centreView();
		showView();
	}

	abstract void setupPanels();
	
	/**
	 * This method is used by controllers to access the selected options once the next button is pressed
	 * @return The options selected by this view
	 */
	public ProgramOptions getOptions()
	{
		return viewOptions;
	}
	
	protected void showView()
	{
		guiFrame.setVisible(true);
	}
	
	protected void addMenus()
	{
		//TODO implement menus
	}
	
	public JFrame getFrame()
	{
		return guiFrame;
	}
	
	public void closeView()
	{
		guiFrame.setVisible(false);
		guiFrame.dispose();
	}
	
	protected void setSize(Dimension dimensions)
	{
		xSize = dimensions.getWidth();
		ySize = dimensions.getHeight();
	}
	
	protected void centreView()
	{
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		guiFrame.setBounds((int) (screenDims.getWidth() / 2 - (xSize / 2)),
						(int) (screenDims.getHeight() / 2 - (ySize / 2)), (int) xSize, (int) ySize);
	}	
}