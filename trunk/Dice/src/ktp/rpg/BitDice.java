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
		a.init(count);
		D[faces - 2].mass(a, this);
		return a.sum;
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
	
	public int nextInt() {
		_ints++;
		return _r.nextInt();
	}
}
