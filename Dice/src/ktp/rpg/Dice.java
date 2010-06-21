package ktp.rpg;

import java.util.Random;

public class Dice {
	public static IDice newSecure() {
		return new SecureRandomDice();
	}
	
	public static IDice newInstance(Random rand) {
		return new SecureRandomDice(rand);
	}
	
	public static IDice newSecure(int numSeedBytes) {
		return new SecureRandomDice(numSeedBytes);
	}
}
