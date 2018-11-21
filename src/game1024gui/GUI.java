package game1024gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.*;
import javax.swing.border.Border;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import game1024.*;

/**
 * My 1024 GUI
 * @author JustinDowty
 *
 */
public class GUI extends JFrame implements KeyListener, ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private JPanel board;
	private JPanel resizeDialog;
	private JPanel statsPanel;
	private JLabel[][] tiles;
	private NumberGame game;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem reset;
	private JMenuItem resizeReset;
	private JTextField userBox = new JTextField();
	private JTextField heightBox = new JTextField("4", 3);
	private JTextField widthBox = new JTextField("4", 3);
	private JTextField valueBox = new JTextField("1024", 3);
	private JTextField timeBox = new JTextField("1000", 3);
	private Timer timer = new Timer();
	private int slides = 0;
	private int wins = 0;
	private int loses = 0;
	private int timeLeft;
	private JLabel highScoreLabel = new JLabel("  High Score -   ");
	private JLabel scoreLabel = new JLabel("  Score: 0   ");
	private JLabel timeLabel = new JLabel("  Time Remaining:   ");
	private JLabel slideLabel = new JLabel("  Slides: 0    ");
	private JLabel winsLabel = new JLabel("  Wins: 0    ");
	private JLabel losesLabel = new JLabel("  Loses: 0   ");
	private String currentUser;
	private int currentTimerVal = 100;
	private int currentHeight = 4; // current height of board used for reset
	private int currentWidth = 4;  // current width of board
	private int currentWinningVal = 1024; // current winning val of board
	private boolean inGame; // Checks to make sure the player is still playing
							// when player wins or loses this value is set to false
							// value gets set back to true when new game is started
	
	/**
	 * Constructor builds the GUI and calls private methods to assist in doing so
	 */
	public GUI(){
		mainPanel = new JPanel(new FlowLayout());
		mainPanel.setBackground(Color.GREEN);
		resizeDialog = new JPanel(new GridLayout(5,2));
		setUpResizeDialog();
		game = new NumberGame();
		resizeResetDialog(); // initializes board
		addKeyListener(this);
		setUpMenus();
		this.setTitle("1024 Game");
		try{
			BufferedReader br = new BufferedReader(new FileReader("HighScores.txt"));
			highScoreLabel.setText("  High Score - " + br.readLine() + "   ");
			br.close();
		}
		catch(Exception ex){};
	}
	
	/**
	 * updates stats labels to current stats
	 */
	private void updateStats(){
		scoreLabel.setText("  Score: " + game.getCurrentScore() + "   ");
		slideLabel.setText("  Slides: " + slides + "   ");
		winsLabel.setText("  Wins: " + wins + "   ");
		losesLabel.setText("  Loses: " + loses + "   ");
		try{
			BufferedReader br = new BufferedReader(new FileReader("HighScores.txt"));
			highScoreLabel.setText("  High Score - " + br.readLine() + "   ");
			br.close();
		}
		catch(Exception ex){}
	}
	
	/**
	 * starts timer and updates timer label for each second
	 * @param seconds
	 */
	private void startTimer(int seconds){
		timer = new Timer();
		timeLeft = seconds;
		timer.schedule(new TimerTask(){
			public void run() {
                timeLabel.setText("  Time Remaining: " + timeLeft-- + " seconds.  ");
                if(timeLeft < 0){
                	timer.cancel();
                	lostDialog();
                }
            }  
        },0, 1000);
	}
	
	/**
	 * resizes and resets the game board and game. initializes games board and functions
	 * @param h int height
	 * @param w int width
	 * @param winningVal int winning value
	 * @param timerVal int timer in seconds
	 * @param user String user name
	 */
	private void gameBoardResizeReset(int h, int w, int winningVal, int timerVal, String user){
		inGame = true;
		currentUser = user;
		currentHeight = h;
		currentWidth = w;
		currentWinningVal = winningVal;
		currentTimerVal = timerVal;
		game.resizeBoard(h, w, winningVal);
		game.reset();
		board = new JPanel(new GridLayout(h,w));
		Dimension tileSize = new Dimension(100,100);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 8);
		tiles = new JLabel[h][w];
		for(int i = 0; i < h; i++){
			for(int j = 0; j < w; j++){
				tiles[i][j] = new JLabel("", JLabel.CENTER);
				tiles[i][j].setOpaque(true);
				tiles[i][j].setFont (tiles[i][j].getFont ().deriveFont (22.0f));
		        tiles[i][j].setPreferredSize(tileSize);
		        tiles[i][j].setBackground(Color.cyan);
		        tiles[i][j].setBorder(border);
		        board.add(tiles[i][j]);
			}
		}
		startTimer(timerVal);
		try{
			mainPanel.remove(statsPanel);
		}
		catch(NullPointerException ex){}
		mainPanel.add(board, BorderLayout.CENTER);
		setUpStatsPanel();
		add(mainPanel);
		this.pack();
	}
	
	private void renderBoard(){
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles[0].length; j++){
				tiles[i][j].setText("");
				tiles[i][j].setBackground(Color.cyan);
			}
		}
		ArrayList<Cell> cells = game.getNonEmptyTiles();
		for(Cell c: cells){
			tiles[c.row][c.column].setText("" + c.value);
			if(c.value == 1 || c.value == 2){
				tiles[c.row][c.column].setBackground(new Color(255, 255, 179));
			}
			else if(c.value == 4){
				tiles[c.row][c.column].setBackground(new Color(255, 255, 132));
			}
			else if(c.value == 8){
				tiles[c.row][c.column].setBackground(new Color(255, 255, 100));
			}
			else if(c.value == 16){
				tiles[c.row][c.column].setBackground(new Color(255, 225, 90));
			}
			else if(c.value == 32){
				tiles[c.row][c.column].setBackground(new Color(255, 195, 80));
			}
			else if(c.value == 64){
				tiles[c.row][c.column].setBackground(new Color(255, 175, 60));
			}
			else if(c.value == 128){
				tiles[c.row][c.column].setBackground(new Color(255, 155, 45));
			}
			else if(c.value == 256){
				tiles[c.row][c.column].setBackground(new Color(255, 125, 35));
			}
			else if(c.value == 512){
				tiles[c.row][c.column].setBackground(new Color(255, 90, 25));
			}
			else if(c.value == 1024){
				tiles[c.row][c.column].setBackground(new Color(255, 60, 10));
			}
			else if(c.value == 2048){
				tiles[c.row][c.column].setBackground(new Color(255, 20, 0));
			}
			else{
				tiles[c.row][c.column].setBackground(new Color(255, 0, 0));
			}
		}
	}
	
	public void keyPressed(KeyEvent e){
		if((e.getKeyChar() == 'W' || e.getKeyChar() == 'w') && inGame){
			if(game.slide(SlideDirection.UP)){
				slides++;
				updateStats();
			}
			renderBoard();
		}
		if((e.getKeyChar() == 'A' || e.getKeyChar() == 'a') && inGame){
			if(game.slide(SlideDirection.LEFT)){
				slides++;
				updateStats();
			}
			renderBoard();
		}
		if((e.getKeyChar() == 'D' || e.getKeyChar() == 'd') && inGame){
			if(game.slide(SlideDirection.RIGHT)){
				slides++;
				updateStats();
			}
			renderBoard();
		}
		if((e.getKeyChar() == 'S' || e.getKeyChar() == 's') && inGame){
			if(game.slide(SlideDirection.DOWN)){
				slides++;
				updateStats();
			}
			renderBoard();
		}
		if(e.getKeyChar() == 'U' || e.getKeyChar() == 'u'){
			try{
				game.undo();
				slides--;
				updateStats();
				renderBoard();
			}
			catch(IllegalStateException ex){
				JOptionPane.showMessageDialog(this,  "Cannot undo any further!");
			}			
		}
		if(game.getStatus() == GameStatus.USER_WON && inGame){
			wonDialog();
		}
		if(game.getStatus() == GameStatus.USER_LOST && inGame){
			lostDialog();
		}
	}
	
	/**
	 * Used to call the appropriate reset or resize/reset dialog box for the menu items
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == reset){
			try{
				mainPanel.remove(board);
			}
			catch(NullPointerException ex){}
			// cancels the timer, catches the exception of the timer already being cancelled
			try{
				timer.cancel();
			}
			catch(Exception ex){}
			slides = 0;
			gameBoardResizeReset(currentHeight, currentWidth, currentWinningVal, currentTimerVal, currentUser);
			renderBoard();
			updateStats();
			inGame = true;
			
		}
		if(e.getSource() == resizeReset){
			try{
				mainPanel.remove(board);
			}
			catch(NullPointerException ex){}
			try{
				timer.cancel();
			}
			catch(Exception ex){}
			slides = 0;
			resizeResetDialog();
			updateStats();
			inGame = true;
		}
	}
	
	// instantiates board and gets called for any new resize/reset of game
	private void resizeResetDialog(){
		int h = 0;
		int w = 0;
		int winningVal = 0;
		int s = 0;
		try{
			int choice = JOptionPane.showConfirmDialog(null, resizeDialog, "Enter board dimensions and winning value", JOptionPane.OK_CANCEL_OPTION);
			if(choice == JOptionPane.OK_OPTION){
				currentUser = userBox.getText();
				h = Integer.parseInt(heightBox.getText());
				w = Integer.parseInt(widthBox.getText());
				if(h == 0 || w == 0){
					throw new NumberFormatException();
				}
				winningVal = Integer.parseInt(valueBox.getText());
				s = Integer.parseInt(timeBox.getText());
				if(s < 0){
					throw new NegativeArraySizeException();
				}
				gameBoardResizeReset(h, w, winningVal, s, currentUser);
				renderBoard();
			}
		}
		catch(NumberFormatException ex){
			JOptionPane.showMessageDialog(this, "Enter valid integers!");
			resizeResetDialog();
		}
		catch(IllegalArgumentException ex){
			JOptionPane.showMessageDialog(this, "Enter valid power of 2 for winning value!");
			resizeResetDialog();
		}
		catch(NegativeArraySizeException ex){
			JOptionPane.showMessageDialog(this, "Enter positive integers!");
			resizeResetDialog();
		}
	}
	
	private void wonDialog(){
		timer.cancel();
		renderBoard();
		JOptionPane.showMessageDialog(this, "Congrats " + currentUser + "!! You win!!\nStats:\nScore: " + game.getCurrentScore() + "\nSlides:  " + slides + "\nTime: " + (currentTimerVal - timeLeft - 1) + " seconds");
		if(inGame){
			wins++;
		}
		addScore();
		updateStats();
		inGame = false;
	}
	
	private void lostDialog(){
		timer.cancel();
		renderBoard();
		JOptionPane.showMessageDialog(this, "OH NO " + currentUser + "!! You lost.\nStats:\nScore: " + game.getCurrentScore() + "\nSlides:  " + slides + "\nTime: " + (currentTimerVal - timeLeft - 1) + " seconds");
		if(inGame){
			loses++;
		}
		addScore();
		updateStats();
		inGame = false;
	}
	
	/**
	 * sets up the resize dialog box
	 */
	private void setUpResizeDialog(){
		resizeDialog.add(new JLabel("Enter Name: "));
		resizeDialog.add(userBox);
		resizeDialog.add(new JLabel("Enter Height (in tiles): "));
		resizeDialog.add(heightBox);
		resizeDialog.add(new JLabel("Enter Width (in tiles): "));
		resizeDialog.add(widthBox);
		resizeDialog.add(new JLabel("Enter Winning Value (power of 2): "));
		resizeDialog.add(valueBox);
		resizeDialog.add(new JLabel("Enter time limit (in seconds): "));
		resizeDialog.add(timeBox);
	}
	
	/**
	 * sets up the stats panel for the game
	 */
	private void setUpStatsPanel(){
		statsPanel = new JPanel(new GridLayout(6,1)); // stats panel must be redefined to be in the 
								                      // right location with flow layout
		Border statsPanelBorder = BorderFactory.createLineBorder(Color.BLACK, 8);
		Border statsBorder = BorderFactory.createLineBorder(Color.red, 8);
		highScoreLabel.setFont(highScoreLabel.getFont().deriveFont(22.0f));
		highScoreLabel.setBorder(statsBorder);
		scoreLabel.setFont(scoreLabel.getFont ().deriveFont(22.0f));
		scoreLabel.setBorder(statsBorder);
		timeLabel.setFont(timeLabel.getFont ().deriveFont(22.0f));
		timeLabel.setBorder(statsBorder);
		slideLabel.setFont(slideLabel.getFont().deriveFont(22.0f));
		slideLabel.setBorder(statsBorder);
		winsLabel.setFont(winsLabel.getFont().deriveFont(22.0f));
		winsLabel.setBorder(statsBorder);
		losesLabel.setFont(losesLabel.getFont().deriveFont(22.0f));
		losesLabel.setBorder(statsBorder);
		statsPanel.setBorder(statsPanelBorder);
		statsPanel.setBackground(Color.LIGHT_GRAY);
		statsPanel.add(highScoreLabel);
		statsPanel.add(scoreLabel);
		statsPanel.add(timeLabel);
		statsPanel.add(slideLabel);
		statsPanel.add(winsLabel);
		statsPanel.add(losesLabel);
		mainPanel.add(statsPanel, BorderLayout.EAST);
	}
	
	private void setUpMenus(){
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		reset = new JMenuItem("Reset Game");
		resizeReset = new JMenuItem("Reset Game/Resize Board");
		reset.addActionListener(this);
		resizeReset.addActionListener(this);
		menu.add(reset);
		menu.add(resizeReset);
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}
	
	/**
	 * Adds the current play thorugh's score to the HighScores.txt file
	 */
	private void addScore(){
		File file = new File("HighScores.txt");
		// Writes to a new HighScores.txt file if a file is not currently there
		if(!file.exists()){
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter("HighScores.txt"));
				bw.write(currentUser + " : " + game.getCurrentScore() + "\r\n");
				bw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		else{
			try{
				String line = null;
				Boolean hasWritten = false;
				BufferedWriter bw = new BufferedWriter(new FileWriter("Temp.txt"));
				BufferedReader br = new BufferedReader(new FileReader("HighScores.txt"));
				while((line = br.readLine()) != null){
					String[] splitLine = line.split(" : ");
					if(!hasWritten && Integer.parseInt(splitLine[1]) <= game.getCurrentScore()){
						bw.write(currentUser + " : " + game.getCurrentScore() + "\r\n");
						hasWritten = true;
					}
					bw.write(line + "\r\n");
				}
				bw.close();
				br.close();
				File oldFile = new File("HighScores.txt");
				oldFile.delete();
				File tempFile = new File("Temp.txt");
				File newFile = new File("HighScores.txt");
				tempFile.renameTo(newFile);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	//UNUSED
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
	
	public static void main(String[] args){
		GUI gui = new GUI();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    gui.pack();
	    gui.setLocationRelativeTo(null);
	    gui.setVisible(true);
	}
	
	
}
