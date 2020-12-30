package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import javax.swing.JCheckBox;

public class YahtzeeGame implements java.io.Serializable {
	public String playerName;
	public int turnsRem, rollsRem;
	public boolean[] buttonsSelected = new boolean[19];
	public DiceArray diceArray;
	public int[] scoresArray;
	
	YahtzeeGame() {
		playerName = "";
		turnsRem = 13;
		rollsRem = 3;
		diceArray = new DiceArray();
		buttonsSelected = new boolean[19];
		scoresArray = new int[19];
	}
	
	YahtzeeGame(String playerName, int turnsRem, int rollsRem, DiceArray diceArray, boolean[] buttonsSelected, int[] scoresArray) {
		this.playerName = playerName;
		this.turnsRem = turnsRem;
		this.rollsRem = rollsRem;
		this.diceArray = diceArray;
		this.buttonsSelected = buttonsSelected;
		this.scoresArray = scoresArray;
	}

	public void resetScores() {
		for(int i=0;i<19;i++) {
			if(buttonsSelected[i]==false) {
				scoresArray[i] = 0;
			}
		}
	}

	public void pressedRoll(JCheckBox[] chkBxs) {
		for(int i=0;i<5;i++) {
			if(chkBxs[i].isSelected()==false) {
				diceArray.rollDie(i);
			}
		}
		rollsRem -= 1;
		updateScores();
	}
	
	private void updateScores() {
		if (buttonsSelected[0]==false) {
			updateAces();
		}
		if (buttonsSelected[1]==false) {
			updateTwos();
		}
		if (buttonsSelected[2]==false) {
			updateThrees();
		}
		if (buttonsSelected[3]==false) {
			updateFours();
		}
		if (buttonsSelected[4]==false) {
			updateFives();
		}
		if (buttonsSelected[5]==false) {
			updateSixes();
		}
		if (buttonsSelected[6]==false) {
			updateUpSubt();
		}
		if (buttonsSelected[7]==false) {
			updateUpBonus();
		}
		if (buttonsSelected[8]==false) {
			updateUpTot();
		}
		if (buttonsSelected[9]==false) {
			updateTok();
		}
		if (buttonsSelected[10]==false) {
			updateFok();
		}
		if (buttonsSelected[11]==false) {
			updateFh();
		}
		if (buttonsSelected[12]==false) {
			updateSmSt();
		}
		if (buttonsSelected[13]==false) {
			updateLgSt();
		}
		if (buttonsSelected[14]==false) {
			updateYtz();
		}
		if (buttonsSelected[15]==false) {
			updateCh();
		}
		if (buttonsSelected[16]==false) {
			updateYtzBns();
		}
		if (buttonsSelected[17]==false) {
			updateLwTot();
		}
		if (buttonsSelected[18]==false) {
			updateGrTot();
		}
	}

	private void updateAces() {
		int aceScore = 0;
		int num = 1;
		for(int i=0;i<5;i++) {
			if (diceArray.getDieValue(i)==num) {
				aceScore += num;
			}
		}
		scoresArray[0] = aceScore;
	}

	private void updateTwos() {
		int twoScore = 0;
		int num = 2;
		for(int i=0;i<5;i++) {
			if (diceArray.getDieValue(i)==num) {
				twoScore += num;
			}
		}
		scoresArray[1] = twoScore;
		
	}

	private void updateThrees() {
		int threeScore = 0;
		int num = 3;
		for(int i=0;i<5;i++) {
			if (diceArray.getDieValue(i)==num) {
				threeScore += num;
			}
		}
		scoresArray[2] = threeScore;
		
	}

	private void updateFours() {
		int fourScore = 0;
		int num = 4;
		for(int i=0;i<5;i++) {
			if (diceArray.getDieValue(i)==num) {
				fourScore += num;
			}
		}
		scoresArray[3] = fourScore;
		
	}

	private void updateFives() {
		int fiveScore = 0;
		int num = 5;
		for(int i=0;i<5;i++) {
			if (diceArray.getDieValue(i)==num) {
				fiveScore += num;
			}
		}
		scoresArray[4] = fiveScore;
		
	}

	private void updateSixes() {
		int sixScore = 0;
		int num = 6;
		for(int i=0;i<5;i++) {
			if (diceArray.getDieValue(i)==num) {
				sixScore += num;
			}
		}
		scoresArray[5] = sixScore;
	}

	private void updateUpSubt() {
		scoresArray[6] = diceArray.getCurrSum();
	}

	private void updateUpBonus() {
		scoresArray[7] = 0;
		if(scoresArray[6]>=63) {
			int upBonusScore = 35;
			scoresArray[7] = upBonusScore;
		}
	}

	private void updateUpTot() {
		int upTotScore = scoresArray[6] + scoresArray[7];
		scoresArray[8] = upTotScore;
	}

	private void updateTok() {
		scoresArray[9] = 0;
		boolean tok = false;
		for (int i = 1; i <= 5; i++) {
			int count = 0;
			for (int j = 0; j < 5; j++) {
				if (diceArray.getDieValue(j) == i) {
					count++;
				}				
				if (count > 2) {
					tok = true;
				}	
			}
		}
		if (tok) {
			scoresArray[9] = diceArray.getCurrSum();
		}
	}

	private void updateFok() {
		scoresArray[10] = 0;
		boolean fok = false;
		for (int i = 1; i <= 5; i++) {
			int count = 0;
			for (int j = 0; j < 5; j++) {
				if (diceArray.getDieValue(j) == i) {
					count++;
				}				
				if (count > 3) {
					fok = true;
				}	
			}
		}
		if (fok) {
			scoresArray[10] = diceArray.getCurrSum();
		}
	}

	private void updateFh() {
		scoresArray[11] = 0;

		int[] diceValues = diceArray.getDiceValues();
		Arrays.sort(diceValues);
		
		if ((((diceValues[0] == diceValues[1]) && (diceValues[1] == diceValues[2]))
				&& (diceValues[3] == diceValues[4]) && 
		(diceValues[2] != diceValues[3])) 
				|| ((diceValues[0] == diceValues[1]) && 
						((diceValues[2] == diceValues[3]) && (diceValues[3] == diceValues[4]))
				&& (diceValues[1] != diceValues[2])))
		{
			scoresArray[11] = 25;
		}
		
	}

	private void updateSmSt() {
		scoresArray[12] = 0;

		int[] diceValues = diceArray.getDiceValues();
		Arrays.sort(diceValues);

		for (int j = 0; j < 4; j++)
		{
			int temp = 0;
			if (diceValues[j] == diceValues[j + 1])
			{
				temp = diceValues[j];

				for (int k = j; k < 4; k++)
				{
					diceValues[k] = diceValues[k + 1];
				}

				diceValues[4] = temp;
			}
		}

		if (((diceValues[0] == 1) && (diceValues[1] == 2) && (diceValues[2] == 3) && (diceValues[3] == 4))
				|| ((diceValues[0] == 2) && (diceValues[1] == 3) && (diceValues[2] == 4) && (diceValues[3] == 5))
				|| ((diceValues[0] == 3) && (diceValues[1] == 4) && (diceValues[2] == 5) && (diceValues[3] == 6))
				|| ((diceValues[1] == 1) && (diceValues[2] == 2) && (diceValues[3] == 3) && (diceValues[4] == 4))
				|| ((diceValues[1] == 2) && (diceValues[2] == 3) && (diceValues[3] == 4) && (diceValues[4] == 5))
				|| ((diceValues[1] == 3) && (diceValues[2] == 4) && (diceValues[3] == 5) && (diceValues[4] == 6)))
		{
			scoresArray[12] = 30;
		}
		
	}
	
	private void updateLgSt() {
		scoresArray[13] = 0;

		int[] diceValues = diceArray.getDiceValues();
		Arrays.sort(diceValues);

		if (((diceValues[0] == 1) && (diceValues[1] == 2) && (diceValues[2] == 3)
				&& (diceValues[3] == 4) && (diceValues[4] == 5))
				|| ((diceValues[0] == 2) && (diceValues[1] == 3) && (diceValues[2] == 4)
						&& (diceValues[3] == 5) && (diceValues[4] == 6)))
		{
			scoresArray[13] = 40;
		}
	}
	
	private void updateYtz() {
		scoresArray[14] = 0;
		if (isYtz()) {
			scoresArray[14] = 50;
		}
	}
	
	private boolean isYtz() {
		int[] diceValues = diceArray.getDiceValues();
		if (diceValues[1] == diceValues[0] && diceValues[2] == diceValues[0] && diceValues[3] == diceValues[0] && diceValues[4] == diceValues[0]) {
			return true;
		}
		return false;
	}

	private void updateCh() {
		scoresArray[15] = diceArray.getCurrSum();
	}

	private void updateYtzBns() {
		if (isYtz() && buttonsSelected[14]) {
			scoresArray[16] = 100;
		}
	}

	private void updateLwTot() {
		scoresArray[17] = 0;
		for(int i=9;i<17;i++) {
			scoresArray[17] += scoresArray[i];
		}
	}

	private void updateGrTot() {
		scoresArray[18] = scoresArray[8]+scoresArray[17];	
	}
}
