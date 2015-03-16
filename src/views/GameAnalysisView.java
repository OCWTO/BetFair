package views;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.DataAnalysis;
import model.ProgramOptions;

public class GameAnalysisView extends BetFairView
{
	private static final String frameTitle = "BetFair Game View";
	private JLabel homeTeamName;
	private JLabel awayTeamName;
	private JLabel gameStartTime;
	private JLabel gameTime;
	private JLabel gameScore;
	private DataAnalysis analysis;
	
	public GameAnalysisView(ProgramOptions options)
	{
		super(frameTitle, options, null);
		//super.viewListener = new LoginController(this);
		
		setupAndDisplay();
		analysis = new DataAnalysis(options);
		//this observe that
		
		//new analysis view sooo
		//creates a recorder object, gives it betfair, observes it
		
		//Recorder will be observable
		//Class will sit above it and parse events
		//Class above it is observer and observable, it throws updates
		//after it receives events and nicely formats
		
	}
	
	public void setAwayTeamName(String awayTeam)
	{
		awayTeamName.setText(awayTeam);
	}
	
	public void setHomeTeamName(String homeTeam)
	{
		homeTeamName.setText(homeTeam);
	}
	
	public void setgameStartTime(String date)
	{
		gameStartTime.setText(date);
	}
	
	//TODO modify to work in halfmins
	public void setGameTime(String timeInMins)
	{
		gameTime.setText(timeInMins + " mins");
	}
	
	public void setGameScore(int home, int away)
	{
		gameScore.setText(home + " - " + away);
	}

	@Override
	void setupPanels()
	{
		setupGameDetailsPanel();
		setupDataPanel();
	}

	private void setupDataPanel()
	{
		JPanel holder = new JPanel();
		holder.setBackground(Color.BLUE);
		mainContainer.add(holder);
		
	}

	private void setupGameDetailsPanel()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		
		JPanel homeTeamDetails = new JPanel();
		homeTeamDetails.setLayout(new BoxLayout(homeTeamDetails, BoxLayout.Y_AXIS));
		JLabel homeTeam = new JLabel("Home");
		homeTeamName = new JLabel();
		homeTeamDetails.add(homeTeam);
		homeTeamDetails.add(homeTeamName);
		homeTeamDetails.setPreferredSize(new Dimension(mainContainer.getWidth()/3, 200));
		
		JPanel gameDetails = new JPanel();
		gameDetails.setLayout(new BoxLayout(gameDetails, BoxLayout.Y_AXIS));
		gameStartTime = new JLabel();
		JLabel gameTimeLabel = new JLabel("Time");
		gameTime = new JLabel("0");
		gameScore = new JLabel("0 - 0");
		gameDetails.add(gameStartTime);
		gameDetails.add(gameTimeLabel);
		gameDetails.add(gameTime);
		gameDetails.add(gameScore);
		gameDetails.setPreferredSize(new Dimension(mainContainer.getWidth()/3, 200));
		
		JPanel awayTeamDetails = new JPanel();
		awayTeamDetails.setLayout(new BoxLayout(awayTeamDetails, BoxLayout.Y_AXIS));
		JLabel awayTeam = new JLabel("Away");
		awayTeamName = new JLabel();
		awayTeamDetails.add(awayTeam);
		awayTeamDetails.add(awayTeamName);
		awayTeamDetails.setPreferredSize(new Dimension(mainContainer.getWidth()/3, 200));
		
		mainPanel.add(homeTeamDetails);
		mainPanel.add(gameDetails);
		mainPanel.add(awayTeamDetails);
		mainPanel.setPreferredSize(new Dimension(mainContainer.getWidth(), 100));
		mainContainer.add(mainPanel);
	}

	@Override
	void addMenus()
	{
		
	}

}
	