package views;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.BetfairMarketObject;
import model.ProgramOptions;
import controllers.MarketSelectController;

/**
 * This class is used to display the available markets for selection with the given options
 * @author Craig
 *
 */
public class MarketSelectView extends BetFairView
{
	private static final String frameTitle = "BetFair Market select";
	private List<BetfairMarketObject> availableMarkets;
	private JTable marketListTable;
	private JButton defaultMarketSelect;
	
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
	
	public JTable getMarketTable()
	{
		return marketListTable;
	}

	private void setupMarketSelectPanel()
	{
		JPanel gameSelectPanel = new JPanel();
		JScrollPane tablePane;

		String[] columnNames = {"Market name"};
		Object[][]rowData = new Object[availableMarkets.size()][columnNames.length];
		
		for(int i = 0; i < availableMarkets.size(); i++)
		{
			rowData[i][0] = availableMarkets.get(i).getName();
		}
		
		marketListTable = new JTable(rowData, columnNames)
		{
			   public boolean isCellEditable(int row, int column){
			        return false;
			   }
		};
		tablePane = new JScrollPane(marketListTable);
		gameSelectPanel.add(tablePane);
		mainContainer.add(gameSelectPanel);
		
	}
	
	private void setupOptionsPanel()
	{
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel,BoxLayout.X_AXIS));
		
		defaultMarketSelect = new JButton();
		defaultMarketSelect.setText("Use defaults");
		defaultMarketSelect.addActionListener(viewListener);
		optionsPanel.add(defaultMarketSelect);
		
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
				selectedMarketIds.add(availableMarkets.get(selectedRow).getMarketId());
			}
			super.getOptions().addMarketIds(selectedMarketIds);	
		}
		return super.getOptions();
	}
}