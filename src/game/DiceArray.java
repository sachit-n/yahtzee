package game;

public class DiceArray implements java.io.Serializable {
	Die[] diceArray;
	
	public DiceArray() {
		this.diceArray = new Die[5];
		for(int i=0;i<5;i++) {
			diceArray[i] = new Die();
		}
	}
	
	public int roll() {
		int sum = 0;
		for(int i=0;i<5;i++) {
			sum += rollDie(i);
		}
		return sum;
	}
	
	// roll one of the five dices 
	public int rollDie(int dieNum) {
		return diceArray[dieNum].roll();
	}
	
	public int getCurrSum() {
		int sum = 0;
		for(int i=0;i<5;i++) {
			sum += getDieValue(i);
		}
		return sum;
	}
	
	public int getDieValue(int dieNum) {
		return diceArray[dieNum].getValue();
	}
	
	public void setDieAtIx(int dieNum) {
		diceArray[dieNum] = new Die(dieNum);
	}
	
	public int[] getDiceValues() {
		int[] diceValues = new int[5];

		diceValues[0] = this.getDieValue(0);
		diceValues[1] = this.getDieValue(1);
		diceValues[2] = this.getDieValue(2);
		diceValues[3] = this.getDieValue(3);
		diceValues[4] = this.getDieValue(4);
		return diceValues;
	}

}
