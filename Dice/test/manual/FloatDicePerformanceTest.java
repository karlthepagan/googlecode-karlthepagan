package manual;

import java.util.Random;

import ktp.rpg.FloatDice;
import ktp.rpg.IDice;

import org.junit.Test;

import test.DicePerformanceTest;


public class FloatDicePerformanceTest extends DicePerformanceTest {
	public static final int COUNT = 100000;
	public static final int LOOPS = 100;
	
	@Test
	public void testRandomD2() {
		timeRandom(2,COUNT,LOOPS);
	}
	
	@Test
	public void testRandomD3() {
		timeRandom(3,COUNT,LOOPS);
	}
	
	@Test
	public void testRandomD4() {
		timeRandom(4,COUNT,LOOPS);
	}
	
	@Test
	public void testRandomD5() {
		timeRandom(5,COUNT,LOOPS);
	}
	
	@Test
	public void testRandomD6() {
		timeRandom(6,COUNT,LOOPS);
	}
	
	@Test
	public void testRandomD8() {
		timeRandom(8,COUNT,LOOPS);
	}
	
	@Test
	public void testRandomD10() {
		timeRandom(10,COUNT,LOOPS);
	}
	
	@Test
	public void testRandomD12() {
		timeRandom(12,COUNT,LOOPS);
	}
	
	@Test
	public void testRandomD20() {
		timeRandom(20,COUNT,LOOPS);
	}
	
	@Override
	protected IDice getImpl(Random r) {
		return new FloatDice(r);
	}

	@Override
	protected Random getRandom(long seed) {
		return new Random(seed);
//		return new SecureRandom();
	}
}
