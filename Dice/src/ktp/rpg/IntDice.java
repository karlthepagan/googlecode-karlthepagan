package ktp.rpg;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

/**
 * Naive Random.nextInt(faces) implementation.
 */
public class IntDice implements IDice {
	
	private final Random _r;
	private int _ints = 0;
	
	public IntDice(Random r) {
		_r = r;
	}

	@Override
	public int d(int faces) {
		_ints++;
		return 1 + _r.nextInt(faces);
	}

	@Override
	public long d(int faces, int count) {
		long sum = 0;
		while(count-- > 0) {
			sum += d(faces);
		}
		return sum;
	}

	@Override
	public long hist(IntBuffer dst, int faces, int count, int min, int max) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] multi(int faces, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long multi(ByteBuffer dst, int faces, int count) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int totalBits() {
		return _ints * 32;
	}

	@Override
	public int multiOffset(int faces) {
		return 1;
	}

	@Override
	public void roll(Accumulator a, int faces) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void roll(Accumulator a) {
		// TODO Auto-generated method stub
		
	}
}
