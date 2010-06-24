package ktp.rpg;

import static org.junit.Assert.*;

import java.nio.IntBuffer;
import java.util.Random;

import ktp.rpg.BitDice;

import org.hamcrest.Matcher;
import org.junit.Test;

import test.CloseTo;

import static test.CloseTo.closeToPercent;

public class SecureRandomDiceTest {
	
	// tune FLOOR and INC so that all tests complete quickly
	// rand(inc) + floor is the increment of the loop
	public static long FLOOR = 0;
	public static long INC = 2 << 21;
	private static double VARIANCE = 0.17;
	private static double RAND_VARIANCE = 0.33;

	@Test(timeout=500)
	public void testD2() {
		Die d = new D2();
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[2];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			dist[d.r((int)i,332)]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
		Matcher<Number> closeToAve = CloseTo.closeToMeanPercent(IntBuffer.wrap(dist), VARIANCE);
		
		// how to do for every in hamcrest?
		assertThat(dist[0], closeToAve);
		assertThat(dist[1], closeToAve);
	}

	@Test(timeout=500)
	public void testRandomD2() {
		testRandom(2,INC << 1,FLOOR, RAND_VARIANCE);
	}

	@Test(timeout=500)
	public void testD3() {
		Die d = new D3();
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[3];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = d.r((int)i,32);
			assertTrue(v != -1);
			dist[(v & 0x03) - 1]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
		assertThat(dist[0], closeToPercent(dist[1],VARIANCE));
		assertThat(dist[0], closeToPercent(dist[2],VARIANCE));
		assertThat(dist[1], closeToPercent(dist[2],VARIANCE));
	}

	@Test(timeout=500)
	public void testRandomD3() {
		testRandom(3,INC << 1,FLOOR, RAND_VARIANCE);
	}

	@Test(timeout=500)
	public void testD4() {
		Die d = new D4();
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[4];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = d.r((int)i,32);
			assertTrue(v != -1);
			dist[v & 0x03]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
		assertThat(dist[0], closeToPercent(dist[1],VARIANCE));
		assertThat(dist[0], closeToPercent(dist[2],VARIANCE));
		assertThat(dist[0], closeToPercent(dist[3],VARIANCE));
		assertThat(dist[1], closeToPercent(dist[2],VARIANCE));
		assertThat(dist[2], closeToPercent(dist[3],VARIANCE));
	}

	@Test//(timeout=500)
	public void testRandomD4() {
		testRandom(4,INC << 1,FLOOR, RAND_VARIANCE);
	}

	@Test(timeout=500)
	public void testD5() {
		Die d = new D5();
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[5];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = d.r((int)i,32);
			if(v == -1)
				misses++;
			else
				dist[v & 0x07]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
//		System.out.println("d5");
//		System.out.println(misses);
//		System.out.println(count);
		
		assertThat(dist[0], closeToPercent(dist[1],VARIANCE));
		assertThat(dist[0], closeToPercent(dist[2],VARIANCE));
		assertThat(dist[0], closeToPercent(dist[3],VARIANCE));
		assertThat(dist[0], closeToPercent(dist[4],VARIANCE));
		assertThat(dist[1], closeToPercent(dist[2],VARIANCE));
		assertThat(dist[2], closeToPercent(dist[3],VARIANCE));
		assertThat(dist[3], closeToPercent(dist[4],VARIANCE));
	}

	@Test(timeout=500)
	public void testRandomD5() {
		testRandom(5,INC << 1,FLOOR, RAND_VARIANCE);
	}

	@Test//(timeout=500)
	public void testD6() {
		Die d = new D6();
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[6];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = d.r((int)i,32);
			if(v == -1)
				misses++;
			else
				dist[v & 0x07]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
//		System.out.println("d6");
//		System.out.println(misses);
//		System.out.println(count);
		
		Matcher<Number> closeToAve = CloseTo.closeToMeanPercent(IntBuffer.wrap(dist), VARIANCE);
		
		assertThat(dist[0], closeToAve);
		assertThat(dist[1], closeToAve);
		assertThat(dist[2], closeToAve);
		assertThat(dist[3], closeToAve);
		assertThat(dist[4], closeToAve);
		assertThat(dist[5], closeToAve);
	}

	@Test(timeout=500)
	public void testRandomD6() {
		testRandom(6,INC << 1,FLOOR, RAND_VARIANCE);
	}
	
	protected void testRandom(int faces, long inc, long floor, double variance) {
		Random r = new Random(2222);
		BitDice d = new BitDice(r);
		int count = 0;
		int[] dist = new int[faces];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			dist[d.d(faces) - 1]++;
			count++;
			
			i += r.nextInt((int)inc) + floor;
		}
		
		Matcher<Number> closeToAve = CloseTo.closeToMeanPercent(IntBuffer.wrap(dist), variance);
		
//		System.out.println("random d" + faces);
		for(int j = 0; j < faces; j++) {
//			System.out.println((j + 1) + " " + dist[j]);
			assertThat(j + " ", dist[j], closeToAve);
		}
	}

	@Test(timeout=500)
	public void testD8() {
		Die d = new D8();
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[8];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = d.r((int)i,32);
			assertTrue(v != -1);
			dist[v & 0x07]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
		Matcher<Number> closeToAve = CloseTo.closeToMeanPercent(IntBuffer.wrap(dist), VARIANCE);
		
		assertThat(dist[0], closeToAve);
		assertThat(dist[1], closeToAve);
		assertThat(dist[2], closeToAve);
		assertThat(dist[3], closeToAve);
		assertThat(dist[4], closeToAve);
		assertThat(dist[5], closeToAve);
		assertThat(dist[6], closeToAve);
		assertThat(dist[7], closeToAve);
	}

	@Test(timeout=500)
	public void testRandomD8() {
		testRandom(8,INC << 1,FLOOR,RAND_VARIANCE);
	}

	@Test(timeout=500)
	public void testD10() {
		Die d = new D10();
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[10];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			long v = d.r((int)i,32);
			if(v == -1)
				misses++;
			else
				dist[(int)(v & 0x0F)]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
//		System.out.println("d10");
//		System.out.println(misses);
//		System.out.println(count);
		
		Matcher<Number> closeToAve = CloseTo.closeToMeanPercent(IntBuffer.wrap(dist), VARIANCE);
		
		assertThat(dist[0], closeToAve);
		assertThat(dist[1], closeToAve);
		assertThat(dist[2], closeToAve);
		assertThat(dist[3], closeToAve);
		assertThat(dist[4], closeToAve);
		assertThat(dist[5], closeToAve);
		assertThat(dist[6], closeToAve);
		assertThat(dist[7], closeToAve);
		assertThat(dist[8], closeToAve);
		assertThat(dist[9], closeToAve);
	}

	@Test(timeout=500)
	public void testRandomD10() {
		testRandom(10,INC << 1,FLOOR, RAND_VARIANCE);
	}

	@Test(timeout=500)
	public void testD12() {
		Die d = new D12();
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[12];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			long v = d.r((int)i,32);
			if(v == -1)
				misses++;
			else
				dist[(int)(v & 0x0F)]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
//		System.out.println("d12");
//		System.out.println(misses);
//		System.out.println(count);
		
//		System.out.println(dist[0]);
//		System.out.println(dist[1]);
//		System.out.println(dist[2]);
//		System.out.println(dist[3]);
//		System.out.println(dist[4]);
//		System.out.println(dist[5]);
//		System.out.println(dist[6]);
//		System.out.println(dist[7]);
//		System.out.println(dist[8]);
//		System.out.println(dist[9]);
//		System.out.println(dist[10]);
//		System.out.println(dist[11]);
		
		Matcher<Number> closeToAve = CloseTo.closeToMeanPercent(IntBuffer.wrap(dist), VARIANCE);
		
		assertThat(dist[0], closeToAve);
		assertThat(dist[1], closeToAve);
		assertThat(dist[2], closeToAve);
		assertThat(dist[3], closeToAve);
		assertThat(dist[4], closeToAve);
		assertThat(dist[5], closeToAve);
		assertThat(dist[6], closeToAve);
		assertThat(dist[7], closeToAve);
		assertThat(dist[8], closeToAve);
		assertThat(dist[9], closeToAve);
		assertThat(dist[10], closeToAve);
		assertThat(dist[11], closeToAve);
	}

	@Test(timeout=500)
	public void testRandomD12() {
		testRandom(12,INC << 1,FLOOR, RAND_VARIANCE);
	}

	@Test(timeout=500)
	public void testD20() {
		Die d = new D20();
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[20];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			long v = d.r((int)i,32);
			if(v == -1)
				misses++;
			else
				dist[(int)(v & 0x01F)]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
//		System.out.println("d20");
//		System.out.println(misses);
//		System.out.println(count);
		
//		System.out.println(dist[0]);
//		System.out.println(dist[1]);
//		System.out.println(dist[2]);
//		System.out.println(dist[3]);
//		System.out.println(dist[4]);
//		System.out.println(dist[5]);
//		System.out.println(dist[6]);
//		System.out.println(dist[7]);
//		System.out.println(dist[8]);
//		System.out.println(dist[9]);
//		System.out.println(dist[10]);
//		System.out.println(dist[11]);
//		System.out.println(dist[12]);
//		System.out.println(dist[13]);
//		System.out.println(dist[14]);
//		System.out.println(dist[15]);
//		System.out.println(dist[16]);
//		System.out.println(dist[17]);
//		System.out.println(dist[18]);
//		System.out.println(dist[19]);
		
		Matcher<Number> closeToAve = CloseTo.closeToMeanPercent(IntBuffer.wrap(dist), VARIANCE);
		
		assertThat(dist[0], closeToAve);
		assertThat(dist[1], closeToAve);
		assertThat(dist[2], closeToAve);
		assertThat(dist[3], closeToAve);
		assertThat(dist[4], closeToAve);
		assertThat(dist[5], closeToAve);
		assertThat(dist[6], closeToAve);
		assertThat(dist[7], closeToAve);
		assertThat(dist[8], closeToAve);
		assertThat(dist[9], closeToAve);
		assertThat(dist[10], closeToAve);
		assertThat(dist[11], closeToAve);
		assertThat(dist[12], closeToAve);
		assertThat(dist[13], closeToAve);
		assertThat(dist[14], closeToAve);
		assertThat(dist[15], closeToAve);
		assertThat(dist[16], closeToAve);
		assertThat(dist[17], closeToAve);
		assertThat(dist[18], closeToAve);
		assertThat(dist[19], closeToAve);
	}

	@Test(timeout=500)
	public void testRandomD20() {
		testRandom(20,INC << 1,FLOOR, RAND_VARIANCE);
	}
}
