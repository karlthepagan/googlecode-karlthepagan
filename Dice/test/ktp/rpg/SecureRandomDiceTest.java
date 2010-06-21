package ktp.rpg;

import static org.junit.Assert.*;

import java.util.Random;

import ktp.rpg.SecureRandomDice;

import org.junit.Test;

import static test.CloseTo.closeToPercent;

public class SecureRandomDiceTest {
	
	private static long FLOOR = 0;
	private static long INC = Integer.MAX_VALUE >> 20 - FLOOR;

	@Test
	public void testD2() {
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[2];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			dist[SecureRandomDice.d2((int)i)]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
		assertThat(dist[0], closeToPercent(dist[1],0.002));
	}

	@Test
	public void testRandomD2() {
		testRandom(2);
		testRandom2(2);
	}

	@Test
	public void testD3() {
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[3];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = SecureRandomDice.d3((int)i);
			assertTrue(v != -1);
			dist[(v & 0x03) - 1]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
		assertThat(dist[0], closeToPercent(dist[1],0.005));
		assertThat(dist[0], closeToPercent(dist[2],0.005));
		assertThat(dist[1], closeToPercent(dist[2],0.005));
	}

	@Test
	public void testRandomD3() {
		testRandom(3);
		testRandom2(3);
	}

	@Test
	public void testD4() {
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[4];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = SecureRandomDice.d4((int)i);
			assertTrue(v != -1);
			dist[v & 0x03]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
		assertThat(dist[0], closeToPercent(dist[1],0.005));
		assertThat(dist[0], closeToPercent(dist[2],0.005));
		assertThat(dist[0], closeToPercent(dist[3],0.005));
		assertThat(dist[1], closeToPercent(dist[2],0.005));
		assertThat(dist[2], closeToPercent(dist[3],0.005));
	}

	@Test
	public void testRandomD4() {
		testRandom(4);
		testRandom2(4);
	}

	@Test
	public void testD5() {
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[5];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = SecureRandomDice.d5((int)i,32);
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
		
		assertThat(dist[0], closeToPercent(dist[1],0.005));
		assertThat(dist[0], closeToPercent(dist[2],0.005));
		assertThat(dist[0], closeToPercent(dist[3],0.005));
		assertThat(dist[0], closeToPercent(dist[4],0.005));
		assertThat(dist[1], closeToPercent(dist[2],0.005));
		assertThat(dist[2], closeToPercent(dist[3],0.005));
		assertThat(dist[3], closeToPercent(dist[4],0.005));
	}

	@Test
	public void testRandomD5() {
		testRandom(5);
		testRandom2(5);
	}

	@Test
	public void testD6() {
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[6];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = SecureRandomDice.d6((int)i,32);
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
		
		assertThat(dist[0], closeToPercent(dist[1],0.01));
		assertThat(dist[0], closeToPercent(dist[2],0.01));
		assertThat(dist[0], closeToPercent(dist[3],0.01));
		assertThat(dist[0], closeToPercent(dist[4],0.01));
		assertThat(dist[0], closeToPercent(dist[5],0.01));
		assertThat(dist[1], closeToPercent(dist[2],0.01));
		assertThat(dist[2], closeToPercent(dist[3],0.01));
		assertThat(dist[3], closeToPercent(dist[4],0.01));
		assertThat(dist[4], closeToPercent(dist[5],0.01));
	}

	@Test
	public void testRandomD6() {
		testRandom(6);
		testRandom2(6);
	}
	
	public void testRandom(int faces) {
		Random r = new Random(2222);
		SecureRandomDice d = new SecureRandomDice(r);
		int count = 0;
		int[] dist = new int[faces];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			dist[d.d(faces) - 1]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
//		System.out.println("random d" + faces);
		for(int j = 0; j < faces; j++) {
//			System.out.println((j + 1) + " " + dist[j]);
			for(int k = 0; k < faces; k++) {
				if(j == k)
					continue;
				
				assertThat(dist[j], closeToPercent(dist[k],0.01));
			}
		}
	}

	public void testRandom2(int faces) {
		Random r = new Random(2222);
		SecureRandomDice d = new SecureRandomDice(r);
		int count = 0;
		int[] dist = new int[faces * 2];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			dist[(int)d.d(faces,2) - 1]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
		System.out.println("random 2d" + faces);
		faces *= 2;
		for(int j = 0; j < faces; j++) {
			System.out.println((j + 1) + " " + dist[j]);
		}
	}

	@Test
	public void testD8() {
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[8];
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			int v = SecureRandomDice.d8((int)i);
			assertTrue(String.valueOf(i),v != -1);
			dist[v & 0x07]++;
			count++;
			
			i += r.nextInt((int)INC) + FLOOR;
		}
		
		assertThat(dist[0], closeToPercent(dist[1],0.01));
		assertThat(dist[0], closeToPercent(dist[2],0.01));
		assertThat(dist[0], closeToPercent(dist[3],0.01));
		assertThat(dist[0], closeToPercent(dist[4],0.01));
		assertThat(dist[0], closeToPercent(dist[5],0.01));
		assertThat(dist[0], closeToPercent(dist[6],0.01));
		assertThat(dist[0], closeToPercent(dist[7],0.01));
		assertThat(dist[1], closeToPercent(dist[2],0.01));
		assertThat(dist[2], closeToPercent(dist[3],0.01));
		assertThat(dist[3], closeToPercent(dist[4],0.01));
		assertThat(dist[4], closeToPercent(dist[5],0.01));
		assertThat(dist[5], closeToPercent(dist[6],0.01));
		assertThat(dist[6], closeToPercent(dist[7],0.01));
	}

	@Test
	public void testRandomD8() {
		testRandom(8);
		testRandom2(8);
	}

	@Test
	public void testD10() {
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[10];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			long v = SecureRandomDice.d10((int)i,32);
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
		
		assertThat(dist[0], closeToPercent(dist[1],0.01));
		assertThat(dist[0], closeToPercent(dist[2],0.01));
		assertThat(dist[0], closeToPercent(dist[3],0.01));
		assertThat(dist[0], closeToPercent(dist[4],0.01));
		assertThat(dist[0], closeToPercent(dist[5],0.01));
		assertThat(dist[0], closeToPercent(dist[6],0.01));
		assertThat(dist[0], closeToPercent(dist[7],0.01));
		assertThat(dist[0], closeToPercent(dist[8],0.01));
		assertThat(dist[0], closeToPercent(dist[9],0.01));
		assertThat(dist[1], closeToPercent(dist[2],0.01));
		assertThat(dist[2], closeToPercent(dist[3],0.01));
		assertThat(dist[3], closeToPercent(dist[4],0.01));
		assertThat(dist[4], closeToPercent(dist[5],0.01));
		assertThat(dist[5], closeToPercent(dist[6],0.01));
		assertThat(dist[6], closeToPercent(dist[7],0.01));
	}

	@Test
	public void testRandomD10() {
		testRandom(10);
		testRandom2(10);
	}

	@Test
	public void testD12() {
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[12];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			long v = SecureRandomDice.d12((int)i,32);
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
		
		assertThat(dist[0], closeToPercent(dist[1],0.01));
		assertThat(dist[0], closeToPercent(dist[2],0.01));
		assertThat(dist[0], closeToPercent(dist[3],0.01));
		assertThat(dist[0], closeToPercent(dist[4],0.01));
		assertThat(dist[0], closeToPercent(dist[5],0.01));
		assertThat(dist[0], closeToPercent(dist[6],0.01));
		assertThat(dist[0], closeToPercent(dist[7],0.01));
		assertThat(dist[0], closeToPercent(dist[8],0.01));
		assertThat(dist[0], closeToPercent(dist[9],0.01));
		assertThat(dist[0], closeToPercent(dist[10],0.01));
		assertThat(dist[0], closeToPercent(dist[11],0.01));
		assertThat(dist[1], closeToPercent(dist[2],0.01));
		assertThat(dist[2], closeToPercent(dist[3],0.01));
		assertThat(dist[3], closeToPercent(dist[4],0.01));
		assertThat(dist[4], closeToPercent(dist[5],0.01));
		assertThat(dist[5], closeToPercent(dist[6],0.01));
		assertThat(dist[6], closeToPercent(dist[7],0.01));
		assertThat(dist[7], closeToPercent(dist[8],0.01));
		assertThat(dist[8], closeToPercent(dist[9],0.01));
	}

	@Test
	public void testRandomD12() {
		testRandom(12);
		testRandom2(12);
	}

	@Test
	public void testD20() {
		Random r = new Random(2222);
		int count = 0;
		int[] dist = new int[20];
		int misses = 0;
		long i = Integer.MIN_VALUE;
		while(i < Integer.MAX_VALUE) {
			long v = SecureRandomDice.d20((int)i,32);
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
		
		assertThat(dist[0], closeToPercent(dist[1],0.01));
		assertThat(dist[0], closeToPercent(dist[2],0.01));
		assertThat(dist[0], closeToPercent(dist[3],0.01));
		assertThat(dist[0], closeToPercent(dist[4],0.01));
		assertThat(dist[0], closeToPercent(dist[5],0.01));
		assertThat(dist[0], closeToPercent(dist[6],0.01));
		assertThat(dist[0], closeToPercent(dist[7],0.01));
		assertThat(dist[0], closeToPercent(dist[8],0.01));
		assertThat(dist[0], closeToPercent(dist[9],0.01));
		assertThat(dist[0], closeToPercent(dist[10],0.01));
		assertThat(dist[0], closeToPercent(dist[11],0.01));
		assertThat(dist[0], closeToPercent(dist[12],0.01));
		assertThat(dist[0], closeToPercent(dist[13],0.01));
		assertThat(dist[0], closeToPercent(dist[14],0.01));
		assertThat(dist[0], closeToPercent(dist[15],0.01));
		assertThat(dist[0], closeToPercent(dist[16],0.01));
		assertThat(dist[0], closeToPercent(dist[17],0.01));
		assertThat(dist[0], closeToPercent(dist[18],0.01));
		assertThat(dist[0], closeToPercent(dist[19],0.01));
		assertThat(dist[1], closeToPercent(dist[2],0.01));
		assertThat(dist[2], closeToPercent(dist[3],0.01));
		assertThat(dist[3], closeToPercent(dist[4],0.01));
		assertThat(dist[4], closeToPercent(dist[5],0.01));
		assertThat(dist[5], closeToPercent(dist[6],0.01));
		assertThat(dist[6], closeToPercent(dist[7],0.01));
		assertThat(dist[7], closeToPercent(dist[7],0.01));
		assertThat(dist[8], closeToPercent(dist[7],0.01));
		assertThat(dist[9], closeToPercent(dist[10],0.01));
		assertThat(dist[10], closeToPercent(dist[11],0.01));
		assertThat(dist[11], closeToPercent(dist[12],0.01));
		assertThat(dist[12], closeToPercent(dist[13],0.01));
		assertThat(dist[13], closeToPercent(dist[14],0.01));
		assertThat(dist[14], closeToPercent(dist[15],0.01));
		assertThat(dist[15], closeToPercent(dist[16],0.01));
		assertThat(dist[16], closeToPercent(dist[17],0.01));
		assertThat(dist[17], closeToPercent(dist[18],0.01));
		assertThat(dist[18], closeToPercent(dist[19],0.01));
	}

	@Test
	public void testRandomD20() {
		testRandom(20);
		testRandom2(20);
	}
}
