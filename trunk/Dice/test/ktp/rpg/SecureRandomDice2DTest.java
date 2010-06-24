package ktp.rpg;

import java.nio.IntBuffer;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import test.CloseTo;


public class SecureRandomDice2DTest {
	public static final int COUNT = 2 << 14;
	public static final double VARIANCE = 0.0062;
	
	protected static double[] pdist(int faces)
	{
		int count = 0;
		int[] dist = new int[faces * 2];
		
		for(int i = 1; i <= faces; i++) {
			for(int j = 0; j < faces; j++) {
				dist[i + j]++;
				count++;
			}
		}
		
		double[] pdist = new double[faces * 2];
		for(int i = 0; i < dist.length; i++) {
			pdist[i] = dist[i] * 1.0 / count;
		}
		
		return pdist;
	}
	
	@Test//(timeout=500)
	public void testRandom2D2() {
		testRandom2(2,COUNT,pdist(2));
	}

	@Test//(timeout=500)
	public void testRandom2D3() {
		testRandom2(3,COUNT,pdist(3));
	}

	@Test//(timeout=500)
	public void testRandom2D4() {
		testRandom2(4,COUNT,pdist(4));
	}

	@Test//(timeout=500)
	public void testRandom2D5() {
		testRandom2(5,COUNT,pdist(5));
	}

	@Test(timeout=500)
	public void testRandom2D6() {
		testRandom2(6,COUNT,pdist(6));
	}
	
	@Test(timeout=500)
	public void testRandom2D8() {
		testRandom2(8,COUNT,pdist(8));
	}

	@Test//(timeout=500)
	public void testRandom2D10() {
		testRandom2(10,COUNT,pdist(10));
	}

	@Test(timeout=500)
	public void testRandom2D12() {
		testRandom2(12,COUNT,pdist(12));
	}

	@Test(timeout=500)
	public void testRandom2D20() {
		testRandom2(20,COUNT,pdist(20));
	}
	
	public void testRandom2(int faces, long limit, double ... pdist) {
		Random r = new Random(2222);
		SecureRandomDice d = new SecureRandomDice(r);
		int count = 0;
		int[] dist = new int[faces * 2];
		long i = Integer.MIN_VALUE;
		int ilimit = (int)(limit - Integer.MIN_VALUE);
		while(i < ilimit) {
			dist[(int)d.d(faces,2) - 1]++;
			count++;
			
			i++;
		}
		
		long sum = CloseTo.sum(IntBuffer.wrap(dist));
		double[] opdist = new double[dist.length];
		
//		System.out.println("random 2d" + faces);
		faces *= 2;
		for(int j = 0; j < faces; j++) {
			if(dist[j] == 0) continue;
			opdist[j] = dist[j] * 1.0 / sum;
//			System.out.println((j + 1) + " " + dist[j] + "\t" + opdist[j] + "\t" + pdist[j]);
			Assert.assertThat(opdist[j], CloseTo.closeTo(pdist[j], VARIANCE));
		}
	}
}
