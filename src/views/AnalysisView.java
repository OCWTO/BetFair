package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import model.DataAnalysis;
import model.Observer;
import model.ProgramOptions;
import model.TestFile;

public class AnalysisView extends BetFairView implements Observer
{
	private static final String frameTitle = "BetFair Game View";
	private JLabel gameStartTime;
	private JLabel gameTime;
	private JLabel gameScore;
	private DataAnalysis analysis;
	private JTable eventListTable;

	
	private String lastUpdated = "Last Updated: \n";
	private JLabel homeTeamName;
	private JLabel awayTeamName;
	private JLabel homeTeamScore;
	private JLabel awayTeamScore;
	private JLabel startTimeLabel;
	private JLabel gameTimeLabel;
	private JLabel lastUpdatedTimeLabel;
	
	private static final String fontName = "Arial Rounded MT Bold";
	public AnalysisView(ProgramOptions options)
	{
		super(frameTitle, options, null);
		//super.viewListener = new LoginController(this);
		
		setupAndDisplay();
		analysis = new DataAnalysis(options);
		analysis.addObserver(this);
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
	
	private void setgameStartTime(String date)
	{
		gameStartTime.setText(date);
	}
	
	//TODO modify to work in halfmins
	private void setGameTime(String timeInMins)
	{
		gameTime.setText(timeInMins + " mins");
	}
	
	private void setGameScore(int home, int away)
	{
		gameScore.setText(home + " - " + away);
	}

	@Override
	void setupPanels()
	{
		setupDetailsPanel();
		
		
		
		//setupGameDetailsPanel();
		//setupDataPanel();
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
		Object[][] data = new Object[5][columnNames.length];
		eventListTable = new JTable(data, columnNames)
		{
			   public boolean isCellEditable(int row, int column){
			        return false;
			   }
		};
		
		tablePane = new JScrollPane(eventListTable);
		dataPanel.add(tablePane);
		
		
		tabbedPane.add("Predicted Events", dataPanel);
		tabbedPane.add("Probability Graph", new JPanel());
		
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
		//On update we either init which sets stuff
		
		//Or we receive string updates
		
		//Check if its an init update, if so then we extract from it and update fields
		
		//Otherwise its a normal update so we add its test to the logs
	}
}