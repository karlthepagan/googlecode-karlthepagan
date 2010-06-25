package ktp.rpg;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.SecureRandom;
import java.util.Random;


/**
 * Dice which make very efficient use of 
 */
class BitDice implements IDice {
	
	private final Random _r;
	private int _ints = 0;
	private static final Die[] D = {
		new D2(),
		new D3(),
		new D4(),
		new D5(),
		new D6(),
		null,
		new D8(),
		null,
		new D10(),
		null,
		new D12(),
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		new D20()
	};
	
	public BitDice(int numSeedBytes) {
		this(new SecureRandom(SecureRandom.getSeed(numSeedBytes)));
	}
	
	public BitDice(Random rand) {
		_r = rand;
	}
	
	public int d(int faces) {
		return D[faces-2].roll(this);
	}
	
	@Override
	public long d(int faces, int count) {
		SumAccumulator a = new SumAccumulator();
		a.init(count,faces);
		D[faces - 2].mass(a, this, 0);
		return a.sum();
	}

	@Override
	public long hist(IntBuffer dst, int faces, int count, int min, int max) {
		int multiOffset = multiOffset(faces);
		if(min <= 1 && max >= faces) {
			HistogramAccumulator a = new HistogramAccumulator();
			a.init(count, faces, multiOffset, dst);
			D[faces - 2].mass(a, this, 0);
			return a.sum();
		} else {
			BoundedHistogramAccumulator a = new BoundedHistogramAccumulator();
			a.init(count, faces, multiOffset, dst, min, max);
			D[faces - 2].mass(a, this, 0);
			return a.sum();
		}
	}

	@Override
	public byte[] multi(int faces, int count) {
		MultiAccumulator a = new MultiAccumulator();
		a.init(count, faces);
		D[faces - 2].mass(a, this, 0);
		return a.resultSet();
	}

	@Override
	public long multi(ByteBuffer dst, int faces, int count) {
		MultiBufferAccumulator a = new MultiBufferAccumulator();
		a.init(count,faces,dst);
		D[faces - 2].mass(a, this, 0);
		return a.sum();
	}
	
	@Override
	public int totalBits() {
		return _ints * 32;
	}
	
	public int nextInt() {
		_ints++;
		return _r.nextInt();
	}

	@Override
	public int multiOffset(int faces) {
		return D[faces-2].multiOffset();
	}

	@Override
	public void roll(Accumulator a, int faces) {
		D[faces - 2].mass(a, this, 0);
	}
	
	@Override
	public void roll(Accumulator a) {
		int faces;
		int rem = 0;
		do {
			faces = a.nextDie();
			rem = D[faces - 2].mass(a, this, rem);
		} while(faces != 0);
	}
}
