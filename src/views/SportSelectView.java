package views;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JList;

import model.BetFairSportObject;
import model.ProgramOptions;
import controllers.SportSelectController;

public class SportSelectView extends BetFairView
{


	private static final String frameTitle = "BetFair Sport Select";
	
	//private ISimpleBetFair betFair;
	private ActionListener guiListener;
	private ProgramOptions options;
	private List<BetFairSportObject> availableSports;
	private JList sportList;
	//TODO purpose of this view is to call get sports, display and let user pick
	//It probably 
	
	public SportSelectView(ProgramOptions options)
	{
		super(frameTitle, options, null);
		this.betFair = options.getBetFair();
		guiListener = new SportSelectController(options, this);
		mainFrame.setLayout(new BoxLayout(mainFrame, BoxLayout.Y_AXIS));
		
		setupAndDisplay();
		//showSports();
	}
	
	//requires boundary checking in listener
	public BetFairSportObject getSelectedSport()
	{
		return availableSports.get(sportList.getSelectedIndex());
	}

	private void showSports()
	{
		availableSports = betFair.getSupportedSportList();
		sportList = new JList(availableSports.toArray());
		System.out.println("Remade list");
		System.out.println(sportList.getModel().getElementAt(0));
		//guiFrame.repaint();
		//need to pass object at the instance 
		
		//sportList.getSele
		//replace current list with this list
		
		//show only sport names, get selected index later and get the id from ti
		
		//List<BetFairSportObject> betFairSportList = options.getBetFair().getSupportedSportList();
	}

	//TODO add superclass for all views with common functionality
	//centreframe, closeview, setup panels, setup menus, returnuserinput?
	
	//what happens if you close a subclass, do you need to close super too?
	
	
	//on creation it needs to get the list of all sports from BetFair




	@Override
	void setupPanels()
	{
		// TODO Auto-generated method stub
		//header panel with list?
		//1 big panel in the middle for jlist
		//1 panel at bottom 
		
		
	}


	@Override
	void addMenus()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProgramOptions getOptions()
	{
		// TODO Auto-generated method stub
		return null;
	}
}