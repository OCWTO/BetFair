package views;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import model.BetfairGameObject;
import model.ProgramOptions;
import controllers.GameSelectController;

/**
 * GameSelectView is used to display the list of available games for the given sport thats in the received ProgramOptions object
 * @author Craig
 *
 */
public class GameSelectView extends BetFairView
{
	private static final String frameTitle = "BetFair Game Select";
	private List<BetfairGameObject> availableGames;
	private JTable gameListTable;
	
	public GameSelectView(ProgramOptions options)
	{
		super(frameTitle, options, null);
		this.betFair = options.getBetFair();
		super.viewListener = new GameSelectController(options, this);
		availableGames = betFair.getGameListForSport(options.getEventTypeId());
		setupAndDisplay();
	}

	@Override
	void setupPanels()
	{
		setupGameSelectPanel();
		setupOptionsPanel();
	}
	
	private void setupGameSelectPanel()
	{
		JPanel gameSelectPanel = new JPanel();
		JScrollPane tablePane;

		String[] columnNames = {"Game name", "Start time", "Country"};
		Object[][]rowData = new Object[availableGames.size()][3];
		
		for(int i = 0; i < availableGames.size(); i++)
		{
			rowData[i][0] = availableGames.get(i).getName();
			rowData[i][1] = availableGames.get(i).getStartTime();
			rowData[i][2] = availableGames.get(i).getCountryCode();
		}
		
		//I didn't feel that it merited a whole class just for that overridden method
		gameListTable = new JTable(rowData, columnNames)
		{
			   public boolean isCellEditable(int row, int column){
			        return false;
			   }
		};
		gameListTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablePane = new JScrollPane(gameListTable);
		gameSelectPanel.add(tablePane);
		mainContainer.add(gameSelectPanel);
		
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
	

	
	@Override
	public ProgramOptions getOptions()
	{
		if(gameListTable.getSelectedRow() != -1)
			super.getOptions().setEventId(availableGames.get(gameListTable.getSelectedRow()).getMarketId());
		
		return super.getOptions();
	}
}