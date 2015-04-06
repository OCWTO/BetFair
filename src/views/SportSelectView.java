package views;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import model.BetfairSportObject;
import model.ProgramOptions;
import controllers.SportSelectController;

/**
 * This class is used to provide users with the list of available sports that they can observe
 * @author Craig
 *
 */
public class SportSelectView extends BetFairView
{
	private static final String frameTitle = "BetFair Sport Select";
	private List<BetfairSportObject> availableSports;
	private JTable sportListTable;
	
	public SportSelectView(ProgramOptions options)
	{
		super(frameTitle, options, null);
		super.viewListener = new SportSelectController(options, this);
		availableSports = betFair.getSupportedSportList();
		setupAndDisplay();
	}

	@Override
	void setupPanels()
	{
		setupSportSelectPanel();
		setupOptionsPanel();
	}
	
	private void setupOptionsPanel()
	{
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel,BoxLayout.X_AXIS));
		
		JButton previousViewButton = new JButton();
		previousViewButton.setText("back");
		previousViewButton.addActionListener(viewListener);
		optionsPanel.add(previousViewButton);
		
		JButton nextViewButton = new JButton();
		nextViewButton.setText("next");
		nextViewButton.addActionListener(viewListener);
		optionsPanel.add(nextViewButton);
		
		mainContainer.add(optionsPanel);
	}

	private void setupSportSelectPanel()
	{	
		JPanel sportSelectPanel = new JPanel();
		JScrollPane tablePane;
		
		//Using stream to convert from List of BetFairSportObjects to List of Strings from the getName function
		List<String> sportNames = availableSports.stream().map(BetfairSportObject::getName).collect(Collectors.toList());
		String[] columnNames = {"Sport name"};
		Object[][] rowData = new Object[sportNames.size()][1];
		
		for(int i =0; i < sportNames.size(); i++)
		{
			rowData[i][0] = sportNames.get(i);
		}

		sportListTable = new JTable(rowData, columnNames)
		{
			   public boolean isCellEditable(int row, int column){
			        return false;
			   }
		};
		
		sportListTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablePane = new JScrollPane(sportListTable);
		sportSelectPanel.add(tablePane);
		mainContainer.add(sportSelectPanel);
	}

	@Override
	public ProgramOptions getOptions()
	{
		if(sportListTable.getSelectedRow() != -1)
			super.getOptions().setEventTypeId(availableSports.get(sportListTable.getSelectedRow()).getSportId());
		
		return super.getOptions();
	}
}