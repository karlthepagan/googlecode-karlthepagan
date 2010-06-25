package ktp.rpg;

/**
 * d2 - clean 1 bit
 */
final class D2 extends Die {
	public int roll(BitDice r) {
		return 1 + (r.nextInt() & 0x01);
	}
	
	public int mass(Accumulator a, BitDice r, int remainder) {
		
		a.add(a.minCount());
		
		int rand;
		int bits;
		if(remainder == 0) {
			rand = r.nextInt();
			bits = 32;
		} else {
			rand = remainder & 0x07FFFFFF;
			bits = remainder >>> 27;
		}
		
		do {
			if(bits < 1) {
				rand = r.nextInt();
				bits = 32;
			}
			
			a.result(rand & 0x01);
			rand >>>= 1;
			bits--;
		} while(a.nextDie() == 2);
		
		// 27 or more bits remain
		if(bits >= 27)
			return 0xD8000000 | (0x05FFFFFF & rand);
		
		return bits << 27 | (0x05FFFFFF & rand);
	}
	
	public int massStart(int minCount) {
		return minCount;
	}

	public int massValue(int v) {
		return v;
	}

	public int value(int v) {
		return 1 + v;
	}
	
	public int bits(int i) {
		return 1;
	}

	public boolean regen(int bits) {
		return bits < 1;
	}

	public int r(int rand, int bits) {
		return rand & 0x01;
	}

	@Override
	int multiOffset() {
		return 1;
	}
}
