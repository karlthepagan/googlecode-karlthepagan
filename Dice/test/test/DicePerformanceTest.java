package test;

import java.util.Random;

import ktp.rpg.Dice;
import ktp.rpg.IDice;

public abstract class DicePerformanceTest {
	protected abstract IDice getImpl(Random r);
	protected abstract Random getRandom(long seed);
	
	protected void timeRandom(int faces, int count, int loops) {
		System.out.print("d" + faces + " ");
		long min = Long.MAX_VALUE;
		double minEfficiency = Double.MAX_VALUE;
		int limit = count;
		for(int i = 0; i < loops; i++) {
			Random r = getRandom(Long.MAX_VALUE / (i + 1));
			IDice d = getImpl(r);
			long t = System.nanoTime();
			d.d(faces,limit);
			t = System.nanoTime() - t;
			double e = 1.0 * limit * minBitsForFace(faces)/ d.totalBits();
			if(e < minEfficiency) minEfficiency = e;
			if(t < min) min = t;
		}
		System.out.println(min + "ns " + minEfficiency);
	}
	
	protected static int minBitsForFace(int faces) {
		switch(faces) {
		case 2: return 1;
		case 3: return 2;
		case 4: return 2;
		case 5: return 3;
		case 6: return 3;
		case 8: return 3;
		case 10: return 4;
		case 12: return 4;
		case 20: return 5;
		}
		return -1;
	}
}
