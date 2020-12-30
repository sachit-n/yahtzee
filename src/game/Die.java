package game;

import java.util.Random;

public class Die implements java.io.Serializable {
	private int value;
	
	public Die() {
		this.value = 1;
	}
	
	public Die(int value) {
		this.value = value;
	}
	
	public int roll() {
		Random rand = new Random();
		this.value = rand.nextInt(6) + 1;
		return this.value;
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}


}
