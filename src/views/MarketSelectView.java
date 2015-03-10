package views;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.BetFairMarketObject;
import model.ProgramOptions;
import controllers.MarketSelectController;

public class MarketSelectView extends BetFairView
{
	private static final String frameTitle = "BetFair Market select";
	private List<BetFairMarketObject> availableMarkets;
	private JTable marketListTable;
	
	public MarketSelectView(ProgramOptions programOptions)
	{
		super(frameTitle, programOptions, null);
		super.viewListener = new MarketSelectController(programOptions, this);
		availableMarkets = betFair.getMarketsForGame(programOptions.getEventId());
		setupAndDisplay();
	}

	@Override
	void setupPanels()
	{
		setupMarketSelectPanel();
		setupOptionsPanel();
	}

	private void setupMarketSelectPanel()
	{
		JPanel gameSelectPanel = new JPanel();
		JScrollPane tablePane;

		String[] columnNames = {"Game name", "Start time", "Country"};
		//Object[][]rowData = new Object[availableGames.size()][3];
		
//		for(int i = 0; i < availableGames.size(); i++)
//		{
//			rowData[i][0] = availableGames.get(i).getName();
//			rowData[i][1] = availableGames.get(i).getStartTime();
//			rowData[i][2] = availableGames.get(i).getCountryCode();
//		}
//		
//		gameListTable = new JTable(rowData, columnNames);
//		tablePane = new JScrollPane(gameListTable);
//		gameSelectPanel.add(tablePane);
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
	void addMenus()
	{
		// TODO Auto-generated method stub
		
	}

}
