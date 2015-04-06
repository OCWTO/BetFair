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
	
	public AnalysisView(ProgramOptions options)
	{
		super(frameTitle, options, null);
		setupAndDisplay();
		analysis = new DataAnalysis(options);
		analysis.addObserver(this);
		analysis.start(5000);
		//this observe that
		
		//new analysis view sooo
		//creates a recorder object, gives it betfair, observes it
		
		//Recorder will be observable
		//Class will sit above it and parse events
		//Class above it is observer and observable, it throws updates
		//after it receives events and nicely formats
		
	}
	
	public static void main(String[] args)
	{
		AnalysisView view = new AnalysisView();
	}
	
	public AnalysisView()
	{
		super(frameTitle, new ProgramOptions(), null);
		//super.viewListener = new LoginController(this);
		
		setupAndDisplay();
		//analysis = new DataAnalysis(options);
		//analysis.addObserver(this);
	}
	
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
	
	//TODO modify to work in halfmins
	private void setGameTime(String timeInMins)
	{
		gameTimeLabel.setText(timeInMins + " mins");
	}
	
	@Override
	void setupPanels()
	{
		setupDetailsPanel();
		
		
		
		//setupGameDetailsPanel();
		//setupDataPanel();
	}
	
	
	private JPanel getProbabilityGraph()
	{
		probabilityDataset = new XYSeriesCollection();

		
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Match Odds Chart", // Title
				"Time", // x-axis Label
				"Implied Probability", // y-axis Label
				probabilityDataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
		);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		JPanel graphPanel = new JPanel();
		graphPanel.add(chartPanel, BorderLayout.CENTER);
		
		return graphPanel;
	}

	private void setupDetailsPanel() 
	{
		JPanel homeTeamPanel = getHomeTeamPanel();
		JPanel gameDetailsPanel = getGameDetailsPanel();
		JPanel awayTeamPanel = getAwayTeamPanel();
		JTabbedPane dataPanel = setupDataPanel();
		
		JPanel detailsPanel = new JPanel();
		detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.X_AXIS));
		detailsPanel.add(homeTeamPanel);
		detailsPanel.add(gameDetailsPanel);
		detailsPanel.add(awayTeamPanel);
		mainContainer.add(detailsPanel);
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

	//Otherwise its a normal update so we add its test to the logs
	private void addPredictions(List<String> predictions)
	{
		DefaultTableModel model = (DefaultTableModel) eventListTable.getModel();
		String[] predictionTokens;
		for(String prediction : predictions)
		{
			//Time, Prediction,Team
			predictionTokens = prediction.split(",");
			model.addRow(predictionTokens);
		}
	}

	private void setupGraph(List<String> graphValues)
	{
		//All Strings in graphvalues are in the form of runner name, timestamp, value
		String[] runnerTokens;
		
		for(int i = 0; i < graphValues.size() ; i++)
		{
			runnerTokens = graphValues.get(i).split(",");
			String runnerName = runnerTokens[0];
			//String timestamp = runnerTokens[1];
			double probability = Double.valueOf(runnerTokens[2]);
			
			XYSeries runnerData = new XYSeries(runnerName);
			runnerData.add(graphCounter, probability);
			System.out.println("ADDING TO GRAPH " + graphCounter + " " + probability);
			probabilityDataset.addSeries(runnerData);
		}
		graphCounter++;
	}
	
	private void addToGraph(List<String> graphValues)
	{
		List<Series> dataSet = probabilityDataset.getSeries();
		
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