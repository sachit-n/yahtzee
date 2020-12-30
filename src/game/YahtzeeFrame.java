package game;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import java.sql.ResultSet;
import java.sql.SQLException;

import game.ImagePanel;


public class YahtzeeFrame extends JFrame {
	// defining panels and frames
	private YahtzeeGame yahtzeeGame;
	
	private JPanel masterPanel, namePanel, remPanel, dicePanel, diceSelPanel, scoresPanel, scoreBtnsPnl, scoreFldsPnl, rollBtnPnl; // ToDo
	
	private JMenuBar menuBar; 
	private JMenu gameMenu;
	private JMenuItem nwGmeBtn, ldGmeBtn, svGmeBtn, exitBtn;
	
	ImagePanel[] diceImgs;
	JCheckBox[] dieChbxs;
	
	private JLabel nameLabel, remLabel;
	private JTextField nameText;
	
	private JTextField[] scoreTextFields;
	private JButton[] scoreBtns; // ith value contains ith button. some values are null which correspond to scores that are labels and not buttons (e.g. at index 18 which contains grand total, value is null as it is not a button)
	
	private JButton rollBtn; // roll button
	
	String hostName = "localhost";
	String[][] savesData;
	String gameChosen;
	JFrame gmeSelectorFrame;
	
	
	public YahtzeeFrame() {
		yahtzeeGame = new YahtzeeGame();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 600);
//		this.setResizable(false);
		createMenuBar();
		createNamePanel(); // Player Name Panel
		createDicePanel();
		createScorePanel();
		createRemPanel();
		createRollPanel();
		assemblePanels();
	}
	
	private void createMenuBar() {
		// Creating Menu Bar
		menuBar = new JMenuBar();
		gameMenu = new JMenu("Game");
		nwGmeBtn = new JMenuItem("New Game");
		nwGmeBtn.addActionListener(l->{
			yahtzeeGame = new YahtzeeGame();
			refreshUI();
		});
		ldGmeBtn = new JMenuItem("Load Game");
		svGmeBtn = new JMenuItem("Save Game");
		exitBtn = new JMenuItem("Exit");
		gameMenu.add(nwGmeBtn);
		gameMenu.add(ldGmeBtn);
		ldGmeBtn.addActionListener(l->{
			Socket socket;
			try {
				socket = new Socket(hostName, 8080);
				ObjectOutputStream toServer =
				          new ObjectOutputStream(socket.getOutputStream());
				toServer.writeObject(new Integer(1));
		        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
		        Object object = fromServer.readObject();
		        savesData = (String[][])object;
		        System.out.println(savesData[0][0]);
		        System.out.println(savesData[1][0]);
		        System.out.println(savesData[2][0]);
		        System.out.println("Recieved Games Saves Data from Server");
		        createLoadWindow();
		        
		        while(gameChosen==null) {
		        	// Wait
		        }
		        
		        ObjectOutputStream toServer2 =
				          new ObjectOutputStream(socket.getOutputStream());
				toServer2.writeObject(this.gameChosen);
				ObjectInputStream fromServer2 = new ObjectInputStream(socket.getInputStream());
		        Object object2 = fromServer2.readObject();
		        ResultSet gamesData = (ResultSet)object;
		        int j=0;
		        try {
					while(gamesData.next()) {
						yahtzeeGame.playerName = (String)gamesData.getObject(3);
						yahtzeeGame.turnsRem = Integer.parseInt((String)gamesData.getObject(4));
						yahtzeeGame.rollsRem = Integer.parseInt((String)gamesData.getObject(5));
						for(int i=6;i<11;i++) {
							yahtzeeGame.diceArray.setDieAtIx(Integer.parseInt((String)gamesData.getObject(i)));
						}
						for(int i=6;i<11;i++) {
							yahtzeeGame.diceArray.setDieAtIx(Integer.parseInt((String)gamesData.getObject(i)));
						}
						for(int i=11;i<30;i++) {
							if(gamesData.getObject(i).equals("true")) {
								yahtzeeGame.buttonsSelected[i] = true;
							}
							else {
								yahtzeeGame.buttonsSelected[i] = false;
							}
						}
						
						for(int i=31;i<49;i++) {
							yahtzeeGame.scoresArray[i] = Integer.parseInt((String)gamesData.getObject(i));
						}
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		gameMenu.add(svGmeBtn);
		svGmeBtn.addActionListener(l->{
			Socket socket;
			try {
				socket = new Socket(hostName, 8080);
		        // Create an output stream to the server
		        ObjectOutputStream toServer =
		          new ObjectOutputStream(socket.getOutputStream());
		        toServer.writeObject(yahtzeeGame);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		gameMenu.add(exitBtn);
		menuBar.add(gameMenu);
		this.setJMenuBar(menuBar);
	}

	private void createLoadWindow() {
		JFrame gmeSelectrFrame = new JFrame();
		gmeSelectrFrame.add(new JLabel("Test Label"));
		gmeSelectrFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gmeSelectrFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gmeSelectrFrame.setSize(1000,500);
		gmeSelectrFrame.setVisible(true);
		JPanel gamesPnl = new JPanel();
		gamesPnl.setLayout(new GridLayout(10, 2));
		JButton[] selGmeBtns = new JButton[10];
		for(int i=0;i<10;i++) {
			JLabel gmeLabel = new JLabel("playerName: " + savesData[2][i] + "Timestamp: " + savesData[1][i]);
			selGmeBtns[i] = new JButton("Select");
			gamesPnl.add(gmeLabel);
			gamesPnl.add(selGmeBtns[i]);
			selGmeBtns[i].addActionListener(new gameChosen(i));
		}
		gmeSelectrFrame.add(gamesPnl);
		this.add(gmeSelectrFrame);
		
	}
	
	public class gameChosen implements ActionListener {
		String gmId;
		
		gameChosen(int idx) {
			this.gmId = savesData[0][idx];
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			gameChosen = this.gmId;
			gmeSelectorFrame.setVisible(false);
			
		}
		
	}

	private void assemblePanels() {
		masterPanel = new JPanel();
		masterPanel.setLayout(new BorderLayout());
		masterPanel.add(namePanel, BorderLayout.NORTH);
		masterPanel.add(dicePanel, BorderLayout.EAST);
		masterPanel.add(scoresPanel, BorderLayout.CENTER);
		masterPanel.add(remPanel, BorderLayout.WEST);
		masterPanel.add(rollBtnPnl, BorderLayout.SOUTH);
		this.add(masterPanel);
		
	}
	
	private void createNamePanel() {
		namePanel = new JPanel();
		nameLabel = new JLabel("Player Name: ");
		nameText = new JTextField(10);
		nameText.addActionListener(l->{
			yahtzeeGame.playerName = nameText.getText();
		});
		namePanel.add(nameLabel);
		namePanel.add(nameText);
	}

	private void createDicePanel() {
		dieChbxs = new JCheckBox[5];
		dieChbxs[0] = new JCheckBox();
		dieChbxs[1] = new JCheckBox();
		dieChbxs[2] = new JCheckBox();
		dieChbxs[3] = new JCheckBox();
		dieChbxs[4] = new JCheckBox();
		
		dicePanel = new JPanel();
		dicePanel.setLayout(new GridLayout(10,1));
		diceImgs = new ImagePanel[5];
		for(int i=0;i<5;i++) {
			diceImgs[i] = new ImagePanel("die" + yahtzeeGame.diceArray.getDieValue(i) + ".png");
			diceImgs[i].scaleImage(0.2);
			dicePanel.add(diceImgs[i]);
			JPanel chBxPanel = new JPanel();
			chBxPanel.add(new JLabel("Hold"));
			chBxPanel.add(dieChbxs[i]);
			dicePanel.add(chBxPanel);
			dieChbxs[i].setEnabled(false);
		}
	}
	
	
	private void createScorePanel() {
		scoresPanel = new JPanel();
		scoresPanel.setLayout(new GridLayout(1, 2));
		
		scoreBtnsPnl = new JPanel();
		scoreBtnsPnl.setLayout(new GridLayout(19, 1));
		
		scoreBtns = new JButton[19];
		
		JPanel acesPanel = new JPanel();
		scoreBtns[0] = new JButton("Aces"); 
		acesPanel.add(scoreBtns[0]);
		scoreBtnsPnl.add(acesPanel);
		
		JPanel twosPanel = new JPanel();
		scoreBtns[1] = new JButton("Twos");
		twosPanel.add(scoreBtns[1]);
		scoreBtnsPnl.add(twosPanel);
		
		JPanel threesPanel = new JPanel();
		scoreBtns[2] = new JButton("Threes");
		threesPanel.add(scoreBtns[2]);
		scoreBtnsPnl.add(threesPanel);
		
		JPanel foursPanel = new JPanel();
		scoreBtns[3] = new JButton("Fours");
		foursPanel.add(scoreBtns[3]); 
		scoreBtnsPnl.add(foursPanel);
		
		JPanel fivesPanel = new JPanel();
		scoreBtns[4] = new JButton("Fives");
		fivesPanel.add(scoreBtns[4]);
		scoreBtnsPnl.add(fivesPanel);
		
		JPanel sixesPanel = new JPanel();
		scoreBtns[5] = new JButton("Sixes");
		sixesPanel.add(scoreBtns[5]);
		scoreBtnsPnl.add(sixesPanel);
		
		JPanel upSecSubtPnl = new JPanel();
		upSecSubtPnl.add(new JLabel("Subtotal"));
		scoreBtnsPnl.add(upSecSubtPnl);
		
		JPanel upSecBnsPnl = new JPanel();
		upSecBnsPnl.add(new JLabel("Bonus"));
		scoreBtnsPnl.add(upSecBnsPnl);
		
		JPanel upSecTotPnl = new JPanel();
		upSecTotPnl.add(new JLabel("Total"));
		scoreBtnsPnl.add(upSecTotPnl);
	
		
		JPanel thrOfKndPnl = new JPanel();
		scoreBtns[9] = new JButton("3 of a kind");
		thrOfKndPnl.add(scoreBtns[9]);
		scoreBtnsPnl.add(thrOfKndPnl);
		
		JPanel frOfKndPnl = new JPanel();
		scoreBtns[10] = new JButton("4 of a kind");
		frOfKndPnl.add(scoreBtns[10]);
		scoreBtnsPnl.add(frOfKndPnl);
		
		JPanel fhPanel = new JPanel();
		scoreBtns[11] = new JButton("Full House");
		fhPanel.add(scoreBtns[11]);
		scoreBtnsPnl.add(fhPanel);
		
		JPanel smStPanel = new JPanel();
		scoreBtns[12] = new JButton("Small Straight");
		smStPanel.add(scoreBtns[12]);
		scoreBtnsPnl.add(smStPanel);
		
		JPanel lgStPanel = new JPanel();
		scoreBtns[13] = new JButton("Large Straight"); 
		lgStPanel.add(scoreBtns[13]);
		scoreBtnsPnl.add(lgStPanel);
		
		JPanel yhtzPnl = new JPanel();
		scoreBtns[14] = new JButton("Yahtzee");
		yhtzPnl.add(scoreBtns[14]);
		scoreBtnsPnl.add(yhtzPnl);
		
		JPanel chPanel = new JPanel();
		scoreBtns[15] = new JButton("Chance");
		chPanel.add(scoreBtns[15]);
		scoreBtnsPnl.add(chPanel);
		
		JPanel ytzBnsPnl = new JPanel();
		JLabel ytzBnsLbl = new JLabel("Yahtzee Bonus");
		ytzBnsPnl.add(ytzBnsLbl);
		scoreBtnsPnl.add(ytzBnsPnl);
		
		JPanel lwSecSubtPnl = new JPanel();
		JLabel lwSecSubtLbl = new JLabel("Subtotal");
		lwSecSubtPnl.add(lwSecSubtLbl);
		scoreBtnsPnl.add(lwSecSubtPnl);
		
		JPanel grTotPnl = new JPanel();
		JLabel grTotLbl = new JLabel("Grand Total");
		grTotPnl.add(grTotLbl);
		scoreBtnsPnl.add(grTotPnl);
		
		scoresPanel.add(scoreBtnsPnl);
		
		
		scoreTextFields = new JTextField[19];
		scoreFldsPnl = new JPanel();
		scoreFldsPnl.setLayout(new GridLayout(19, 1));
		
		int tfSize = 3; 

		JPanel acesPanel2 = new JPanel();
		scoreTextFields[0] = new JTextField(tfSize); 
		scoreTextFields[0].setEditable(false);
		acesPanel2.add(scoreTextFields[0]);
		scoreFldsPnl.add(acesPanel2);
		
		JPanel twosPanel2 = new JPanel();
		scoreTextFields[1] = new JTextField(tfSize); 
		scoreTextFields[1].setEditable(false);
		twosPanel2.add(scoreTextFields[1]);
		scoreFldsPnl.add(twosPanel2);
		
		JPanel threesPanel2 = new JPanel();
		scoreTextFields[2] = new JTextField(tfSize); 
		scoreTextFields[2].setEditable(false);
		threesPanel2.add(scoreTextFields[2]);
		scoreFldsPnl.add(threesPanel2);
		
		JPanel foursPanel2 = new JPanel();
		scoreTextFields[3] = new JTextField(tfSize); 
		scoreTextFields[3].setEditable(false);;
		foursPanel2.add(scoreTextFields[3]);
		scoreFldsPnl.add(foursPanel2);
		
		JPanel fivesPanel2 = new JPanel();
		scoreTextFields[4] = new JTextField(tfSize); 
		scoreTextFields[4].setEditable(false);
		fivesPanel2.add(scoreTextFields[4]);
		scoreFldsPnl.add(fivesPanel2);
		
		JPanel sixesPanel2 = new JPanel();
		scoreTextFields[5] = new JTextField(tfSize); 
		scoreTextFields[5].setEditable(false);
		sixesPanel2.add(scoreTextFields[5]);
		scoreFldsPnl.add(sixesPanel2);
		
		JPanel upSecSubtPnl2 = new JPanel();
		scoreTextFields[6] = new JTextField(tfSize); 
		scoreTextFields[6].setEditable(false);
		upSecSubtPnl2.add(scoreTextFields[6]);
		scoreFldsPnl.add(upSecSubtPnl2);
		
		JPanel upSecBnsPnl2 = new JPanel();
		scoreTextFields[7] = new JTextField(tfSize); 
		scoreTextFields[7].setEditable(false);
		upSecBnsPnl2.add(scoreTextFields[7]);
		scoreFldsPnl.add(upSecBnsPnl2);
		
		JPanel upSecTotPnl2 = new JPanel();
		scoreTextFields[8] = new JTextField(tfSize); scoreTextFields[8].setEditable(false);;
		upSecTotPnl2.add(scoreTextFields[8]);
		scoreFldsPnl.add(upSecTotPnl2);
	
		
		JPanel thrOfKndPnl2 = new JPanel();
		scoreTextFields[9] = new JTextField(tfSize); scoreTextFields[9].setEditable(false);;
		thrOfKndPnl2.add(scoreTextFields[9]);
		scoreFldsPnl.add(thrOfKndPnl2);
		
		JPanel frOfKndPnl2 = new JPanel();
		scoreTextFields[10] = new JTextField(tfSize); scoreTextFields[01].setEditable(false);;
		frOfKndPnl2.add(scoreTextFields[10]);
		scoreFldsPnl.add(frOfKndPnl2);
		
		JPanel fhPanel2 = new JPanel();
		scoreTextFields[11] = new JTextField(tfSize); scoreTextFields[11].setEditable(false);;
		fhPanel2.add(scoreTextFields[11]);
		scoreFldsPnl.add(fhPanel2);
		
		JPanel smStPanel2 = new JPanel();
		scoreTextFields[12] = new JTextField(tfSize); scoreTextFields[12].setEditable(false);;
		smStPanel2.add(scoreTextFields[12]);
		scoreFldsPnl.add(smStPanel2);
		
		JPanel lgStPanel2 = new JPanel();
		scoreTextFields[13] = new JTextField(tfSize); scoreTextFields[13].setEditable(false);;
		lgStPanel2.add(scoreTextFields[13]);
		scoreFldsPnl.add(lgStPanel2);
		
		JPanel yhtzPnl2 = new JPanel();
		scoreTextFields[14] = new JTextField(tfSize); scoreTextFields[14].setEditable(false);;
		yhtzPnl2.add(scoreTextFields[14]);
		scoreFldsPnl.add(yhtzPnl2);
		
		JPanel chPanel2 = new JPanel();
		scoreTextFields[15] = new JTextField(tfSize); scoreTextFields[15].setEditable(false);;
		chPanel2.add(scoreTextFields[15]);
		scoreFldsPnl.add(chPanel2);
		
		JPanel ytzBnsPnl2 = new JPanel();
		scoreTextFields[16] = new JTextField(tfSize); scoreTextFields[16].setEditable(false);;
		ytzBnsPnl2.add(scoreTextFields[16]);
		scoreFldsPnl.add(ytzBnsPnl2);
		
		JPanel lwSecSubtPnl2 = new JPanel();
		scoreTextFields[17] = new JTextField(tfSize); scoreTextFields[17].setEditable(false);
		lwSecSubtPnl2.add(scoreTextFields[17]);
		scoreFldsPnl.add(lwSecSubtPnl2);
		
		JPanel grTotPnl2 = new JPanel();
		scoreTextFields[18] = new JTextField(tfSize); scoreTextFields[18].setEditable(false);
		grTotPnl2.add(scoreTextFields[18]);
		scoreFldsPnl.add(grTotPnl2);
		
		scoresPanel.add(scoreFldsPnl);
		
		for(int i=0;i<19;i++) {
			if(scoreBtns[i]!=null) {
				scoreBtns[i].addActionListener(new PressedScoreBtn(i));
			}
		}	
	}
	
	public class PressedScoreBtn implements ActionListener {
	    private int btnIx;

	    public PressedScoreBtn(int btnIx) {
	        this.btnIx = btnIx;
	    }

		@Override
		public void actionPerformed(ActionEvent e) {
			if (yahtzeeGame.rollsRem<3) { //remRoll=3 means dice hasnt been rolled (either game hasnt started or button is already chosen 
				yahtzeeGame.buttonsSelected[btnIx] = true;
				yahtzeeGame.diceArray = new DiceArray();
				yahtzeeGame.turnsRem -= 1;
				if(yahtzeeGame.turnsRem>0) {
					yahtzeeGame.resetScores();
					yahtzeeGame.rollsRem = 3;
				}
				else {
					disableScoreBtns();
					rollBtn.setEnabled(false);
				}
				refreshUI();
			}
		}
		
	}
	
	private void updateScoreFields() {
		for(int i=0;i<19;i++) {
			scoreTextFields[i].setText("" + yahtzeeGame.scoresArray[i]);
		}
	}
	
	private void createRemPanel() {
		remPanel = new JPanel();
		remPanel.setLayout(new GridLayout(1,1));
		remLabel = new JLabel("<html>Rolls Rem: " + yahtzeeGame.rollsRem + "<br/>Turns Rem: " + yahtzeeGame.turnsRem + "</html>");
		remPanel.add(remLabel);
	}
	
	private void updateRemLabel() {
		remLabel.setText("<html>Rolls Rem: " + yahtzeeGame.rollsRem + "<br/>Turns Rem: " + yahtzeeGame.turnsRem + "</html>");
	}

	private void createRollPanel() {
		rollBtnPnl = new JPanel();
		rollBtn = new JButton("Roll Dice");
		rollBtn.addActionListener(e -> {
			yahtzeeGame.pressedRoll(dieChbxs);
			this.refreshUI();
		});
		
		rollBtnPnl.add(rollBtn);
		
	}	
	
	private void refreshUI() {
		for(int i=0;i<5;i++) {
			diceImgs[i].setImage("die" + yahtzeeGame.diceArray.getDieValue(i) + ".png");
			diceImgs[i].scaleImage(0.25);
		}
		updateRemLabel();
		updateScoreFields();
		
		if (yahtzeeGame.rollsRem<1) {
			rollBtn.setEnabled(false);
		}
		if (yahtzeeGame.rollsRem==3) {
			rollBtn.setEnabled(true);
			disableChbxs();
			disableScoreBtns();
		}
		else {
			enableChbxs();
			enableScoreBtns();
		}
		
		if (yahtzeeGame.turnsRem==13) { //if newgame
			enableScoreBtns();
		}
	}
	
	private void enableScoreBtns() {
		for(int j=0;j<19;j++) {
			if(scoreBtns[j]!=null && yahtzeeGame.buttonsSelected[j]==false) {
				scoreBtns[j].setEnabled(true);
			}
		}
	}
	
	private void disableScoreBtns() {
		for(int j=0;j<19;j++) {
			if(scoreBtns[j]!=null) {
				scoreBtns[j].setEnabled(false);
			}
		}
	}

	private void disableChbxs() {
		for(int j=0;j<5;j++) {
			dieChbxs[j].setSelected(false);
			dieChbxs[j].setEnabled(false);
		}
	}
	
	private void enableChbxs() {
		for(int j=0;j<5;j++) {
			dieChbxs[j].setEnabled(true);
		}
	}
	
	public static void main(String args[]) {
		YahtzeeFrame yahtzee = new YahtzeeFrame();
		yahtzee.setVisible(true);
	}
}











