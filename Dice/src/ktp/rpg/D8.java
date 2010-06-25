package ktp.rpg;

/**
 * d8 - clean 3 bit + 1
 */
final class D8 extends Die {
	public int roll(BitDice r) {
		return 1 + (r.nextInt() & 0x07);
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
			if(bits < 3) {
				rand = r.nextInt();
				bits = 32;
			}
			
			a.result(rand & 0x07);
			rand >>>= 3;
			bits -= 3;
		} while(a.nextDie() == 8);

		
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
		return 3;
	}

	public boolean regen(int bits) {
		return bits < 3;
	}

	public int r(int rand, int bits) {
		return rand & 0x07;
	}

	@Override
	int multiOffset() {
		return 1;
	}
}
