package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.DataAnalysis;
import model.Observer;
import model.ProgramOptions;
import model.TestFile;
import model.ViewUpdate;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.Series;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class is used to provide a view of the predicted events and state of the game. It's designed to observe
 * a DataAnalysis object and display the data it receives from updates
 * @author Craig
 *
 */
public class AnalysisView extends BetFairView implements Observer
{
	private static final String frameTitle = "BetFair Game View";
	private DataAnalysis analysis;
	
	private String lastUpdated = "Last Updated: \n";
	
	private JTable eventListTable;

	private JLabel homeTeamName;
	private JLabel awayTeamName;
	private JLabel homeTeamScore;
	private JLabel awayTeamScore;
	private JLabel startTimeLabel;
	private JLabel gameTimeLabel;
	private JLabel lastUpdatedTimeLabel;
	
	private XYSeriesCollection probabilityDataset;
	
	private int graphCounter = 0;
	
	private static final String fontName = "Arial Rounded MT Bold";
	
	/**
	 * Create a new AnalysisView object
	 * @param options The selected options for the game to observe
	 */
	public AnalysisView(ProgramOptions options)
	{
		super(frameTitle, options, null);
		setupAndDisplay();
		analysis = new DataAnalysis(options);
		analysis.addObserver(this);
		analysis.start(5000);
	}
	
	public AnalysisView()
	{
		super(frameTitle, new ProgramOptions(), null);
		setupAndDisplay();
	}
	
	/**
	 * Create an AnalysisView object for the given test file and start the DataAnalysis class below to start
	 * receiving its data at 1ms intervals
	 * @param testFile TestFile object that contains the .txt test file that the program will be simulated on
	 */
	public AnalysisView(TestFile testFile)
	{
		super(frameTitle, new ProgramOptions(), null);
		analysis = new DataAnalysis(testFile);
		analysis.addObserver(this);
		setupAndDisplay();
		analysis.start();
	}
	
	private void setAwayTeamName(String awayTeam)
	{
		awayTeamName.setText(awayTeam);
	}
	
	private void setHomeTeamName(String homeTeam)
	{
		homeTeamName.setText(homeTeam);
	}
	
	private void setGameStartTime(String date)
	{
		startTimeLabel.setText(date);
	}
	

	private void setGameTime(String timeInMins)
	{
		if(timeInMins != null)
			gameTimeLabel.setText(timeInMins + " mins");
	}
	
	@Override
	void setupPanels()
	{
		setupInformationPanels();
	}
	
	
	private JPanel getProbabilityGraph()
	{
		probabilityDataset = new XYSeriesCollection();

		JFreeChart chart = ChartFactory.createXYLineChart("Match Odds Chart", //graph title
				"Time", //x axis label
				"Implied Probability", //y axis label
				probabilityDataset, //the dataset
				PlotOrientation.VERTICAL,
				true, //legend is displayed
				true,
				false 
		);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		JPanel graphPanel = new JPanel();
		graphPanel.add(chartPanel, BorderLayout.CENTER);
		
		return graphPanel;
	}

	private void setupInformationPanels() 
	{
		//Top panel with runner info
		JPanel homeTeamPanel = getHomeTeamPanel();
		JPanel gameDetailsPanel = getGameDetailsPanel();
		JPanel awayTeamPanel = getAwayTeamPanel();
			
		JPanel detailsPanel = new JPanel();
		detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.X_AXIS));
		detailsPanel.add(homeTeamPanel);
		detailsPanel.add(gameDetailsPanel);
		detailsPanel.add(awayTeamPanel);
		mainContainer.add(detailsPanel);
		
		//Bottom panel with the tabs
		JTabbedPane dataPanel = setupDataPanel();
		mainContainer.add(dataPanel);	
	}

	private JTabbedPane setupDataPanel()
	{
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel dataPanel = new JPanel();
		JScrollPane tablePane;
		
		dataPanel.setBackground(Color.BLUE);
		
		String[] columnNames = {"Time", "Event Predicted", "Team"};
		Object[][] data = new Object[0][columnNames.length];
		eventListTable = new JTable()
		{
			   public boolean isCellEditable(int row, int column){
			        return false;
			   }
		};
		eventListTable.setModel(new DefaultTableModel(data, columnNames));
		tablePane = new JScrollPane(eventListTable);
		dataPanel.add(tablePane);
		JPanel graphPanel = new JPanel();
		graphPanel.add(getProbabilityGraph());
		
		tabbedPane.add("Predicted Events", dataPanel);
		tabbedPane.add("Probability Graph", graphPanel);
		return tabbedPane;
	}
	
	private JPanel getHomeTeamPanel()
	{
		JPanel homeTeamPanel = new JPanel();
		homeTeamPanel.setLayout(new BoxLayout(homeTeamPanel, BoxLayout.Y_AXIS));
		
		homeTeamName = new JLabel("HOME TEAM NAME");
		homeTeamName.setFont(new Font(fontName, Font.PLAIN, 28));
		JLabel homeTeam = new JLabel("HOME");
		homeTeamScore = new JLabel("0");
		homeTeamScore.setFont(new Font("Serif", Font.PLAIN, 48));
		
		homeTeamPanel.add(homeTeamName);
		homeTeamPanel.add(homeTeam);
		homeTeamPanel.add(homeTeamScore);
		
		return homeTeamPanel;
	}
	
	private JPanel getAwayTeamPanel()
	{
		JPanel awayTeamPanel = new JPanel();
		awayTeamPanel.setLayout(new BoxLayout(awayTeamPanel, BoxLayout.Y_AXIS));
		
		awayTeamName = new JLabel("AWAY TEAM NAME");
		awayTeamName.setFont(new Font(fontName, Font.PLAIN, 28));
		JLabel awayTeam = new JLabel("AWAY");
		awayTeamScore = new JLabel("0");
		awayTeamScore.setFont(new Font(fontName, Font.PLAIN, 48));
		
		
		awayTeamPanel.add(awayTeamName);
		awayTeamPanel.add(awayTeam);
		awayTeamPanel.add(awayTeamScore);
		
		return awayTeamPanel;
	}
	
	private JPanel getGameDetailsPanel()
	{
		JPanel gameDetailsPanel = new JPanel();
		gameDetailsPanel.setLayout(new BoxLayout(gameDetailsPanel, BoxLayout.Y_AXIS));
		
		startTimeLabel = new JLabel("UNKNOWN START TIME");
		startTimeLabel.setFont(new Font(fontName, Font.PLAIN, 20));
		gameTimeLabel = new JLabel("00:00");
		gameTimeLabel.setFont(new Font(fontName, Font.PLAIN, 24));
		lastUpdatedTimeLabel = new JLabel(lastUpdated + "0");
		
		gameDetailsPanel.add(startTimeLabel);
		gameDetailsPanel.add(gameTimeLabel);
		gameDetailsPanel.add(lastUpdatedTimeLabel);
		return gameDetailsPanel;
	}
	
	@Override
	protected void addMenus()
	{
	}

	@Override
	public void update(Object obj)
	{
		if(obj != null)
		{
			ViewUpdate update = (ViewUpdate) obj;
			
			//If this is the first update then get the start up data
			if(update.getInitialUpdate())
			{
				setAwayTeamName(update.getAwayTeam());
				setHomeTeamName(update.getHomeTeam());
				setFavouredTeamName(update.getFavouredTeam());
				setGameTime(update.getGameTime());
				setGameStartTime(update.getGameStartTime());
				setLastUpdateTime(update.getLastUpdateTime());
				setupGraph(update.getRunnerVals());
				addPredictions(update.getPredictions());
			}
			//Otherwise just add the normal stuff
			else
			{
				setGameTime(update.getGameTime());
				setLastUpdateTime(update.getLastUpdateTime());
				addToGraph(update.getRunnerVals());
				addPredictions(update.getPredictions());
			}	
		}
	}
		
	private void setLastUpdateTime(String lastUpdateTime)
	{
		lastUpdatedTimeLabel.setText(lastUpdated + lastUpdateTime);
	}

	/**
	 * Add the given predictions to the views JTable
	 * @param predictions
	 */
	private void addPredictions(List<String> predictions)
	{
		DefaultTableModel model = (DefaultTableModel) eventListTable.getModel();
		String[] predictionTokens;
		for(String prediction : predictions)
		{
			//Time, Prediction,Team
			predictionTokens = prediction.split(",");
			model.addRow(predictionTokens);
			
			if(predictionTokens[1].equals("GOAL"))
			{
				System.out.println("UPDATING SCORE");
				String teamName = predictionTokens[2];
				if(homeTeamName.getText().contains(teamName))
				{
					int score = Integer.parseInt(homeTeamScore.getText());
					score++;
					homeTeamScore.setText(score + "");
				}
				else 
				{
					int score = Integer.parseInt(awayTeamScore.getText());
					score++;
					awayTeamScore.setText(score + "");
				}
			}
		}
	}

	/**
	 * Add new XYSeries objects to the graph for each runner in the graphValues received
	 * @param graphValues
	 */
	private void setupGraph(List<String> graphValues)
	{
		String[] runnerTokens;
		
		//Create XYSeries objects for each runner in the match odds market (thats the data it receives)
		for(int i = 0; i < graphValues.size() ; i++)
		{
			runnerTokens = graphValues.get(i).split(",");
			String runnerName = runnerTokens[0];
			double probability = Double.valueOf(runnerTokens[2]);
			XYSeries runnerData = new XYSeries(runnerName);
			runnerData.add(graphCounter, probability);
			probabilityDataset.addSeries(runnerData);
		}
		graphCounter++;
	}
	
	/**
	 * Add data
	 * @param graphValues Runners values to be added in the form of 'timestamp, runner name, value'
	 */
	private void addToGraph(List<String> graphValues)
	{
		List<Series> dataSet = probabilityDataset.getSeries();
		if(dataSet != null)
		{
			String[] runnerTokens;
			for(int i = 0; i < dataSet.size() ; i++)
			{
				runnerTokens = graphValues.get(i).split(",");
				//Timestamp, runner name and probability are passed
				double probability = Double.valueOf(runnerTokens[2]);
				
				XYSeries runnerData = (XYSeries) dataSet.get(i);
				runnerData.add(graphCounter, probability);
			}
			graphCounter++;
		}
	}

	private void setFavouredTeamName(String favouredTeam)
	{
		if(favouredTeam.equals(homeTeamName.getText()))
		{
			homeTeamName.setText(homeTeamName.getText() + " FAVOURED");
		}
		else if(favouredTeam.equals(awayTeamName.getText()))
		{
			awayTeamName.setText(awayTeamName.getText() + " FAVOURED");
		}
	}
}