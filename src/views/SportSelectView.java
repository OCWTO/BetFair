package views;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JList;

import model.BetFairSportObject;
import model.ISimpleBetFair;
import model.ProgramOptions;
import controllers.SportSelectController;

public class SportSelectView
{
	private JFrame guiFrame;
	private Container mainFrame;
	
	private final int xSize = 900;
	private final int ySize = 600;
	
	private final String frameTitle = "BetFair Sport Select";
	
	private ISimpleBetFair betFair;
	private ActionListener guiListener;
	private ProgramOptions options;
	private List<BetFairSportObject> availableSports;
	private JList sportList;
	//TODO purpose of this view is to call get sports, display and let user pick
	//It probably 
	public SportSelectView(ProgramOptions options)
	{
		this.betFair = options.getBetFair();
		guiListener = new SportSelectController(options, this);
		guiFrame = new JFrame(frameTitle);
		guiFrame.setResizable(false);
		mainFrame = guiFrame.getContentPane();
		mainFrame.setLayout(new BoxLayout(guiFrame.getContentPane(), BoxLayout.Y_AXIS));
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setupPanels();
		
		//show list thats blank then populate it, set initial string value as getting sport list?
		addMenus();
		centreFrame();
		showSports();
		guiFrame.setVisible(true);
		availableSports = betFair.getSupportedSportList();
	}
	
	//requires boundary checking in listener
	public BetFairSportObject getSelectedSport()
	{
		return availableSports.get(sportList.getSelectedIndex());
	}

	private void showSports()
	{
		sportList = new JList(availableSports.toArray());
		//need to pass object at the instance 
		
		//sportList.getSele
		//replace current list with this list
		
		//show only sport names, get selected index later and get the id from ti
		
		//List<BetFairSportObject> betFairSportList = options.getBetFair().getSupportedSportList();
	}

	private void centreFrame()
	{
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		guiFrame.setBounds((int) screenDims.getWidth() / 2 - (xSize / 2),
						(int) screenDims.getHeight() / 2 - (ySize / 2), xSize, ySize);
	}
	
	//TODO add superclass for all views with common functionality
	//centreframe, closeview, setup panels, setup menus, returnuserinput?
	
	//what happens if you close a subclass, do you need to close super too?
	
	
	//on creation it needs to get the list of all sports from BetFair

	private void addMenus()
	{
		// TODO Auto-generated method stub
	}
	
	public void closeView()
	{
		guiFrame.setVisible(false);
		guiFrame.dispose();
	}

	private void setupPanels()
	{
		
	}
}