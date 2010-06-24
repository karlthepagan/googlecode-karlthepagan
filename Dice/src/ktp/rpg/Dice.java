package ktp.rpg;

import java.util.Random;

/**
 * Static factory for the implementations.
 * 
 * External state in java.security.SecureRandom via SPI.
 * 
 * Avoid state by using the newInstance(Random) method.
 */
public class Dice {
	/**
	 * Creates a bit efficient dice roller backed by
	 * java.security.SecureRandom, initialized with a 4 byte
	 * seed.
	 */
	public static IDice newSecure() {
		return newSecure(4);
	}
	
	/**
	 * Creates a bit efficient dice roller backed by
	 * the given Random implementation.
	 */
	public static IDice newInstance(Random rand) {
		return new BitDice(rand);
	}
	
	/**
	 * Creates a bit efficient dice roller backed by
	 * java.security.SecureRandom, initialized with the
	 * given number of seed bytes.
	 */
	public static IDice newSecure(int numSeedBytes) {
		return new BitDice(numSeedBytes);
	}
}
