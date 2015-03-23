package views;

import java.util.ArrayList;
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

		String[] columnNames = {"Market name", "Market Id"};
		Object[][]rowData = new Object[availableMarkets.size()][columnNames.length];
		
		for(int i = 0; i < availableMarkets.size(); i++)
		{
			rowData[i][0] = availableMarkets.get(i).getName();
			rowData[i][1] = availableMarkets.get(i).getId();
		}
		
		marketListTable = new JTable(rowData, columnNames);
		tablePane = new JScrollPane(marketListTable);
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
		if(marketListTable.getSelectedRows().length > 0)
		{
			List<String> selectedMarketIds = new ArrayList<String>();
			for(int selectedRow: marketListTable.getSelectedRows())
			{
				selectedMarketIds.add(availableMarkets.get(selectedRow).getId());
			}
		}
			
		return super.getOptions();
	}
}