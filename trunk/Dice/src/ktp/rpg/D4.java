package ktp.rpg;

/**
 * d4 - clean 2 bit + 1
 */
final class D4 extends Die {
	public int roll(BitDice r) {
		return 1 + (r.nextInt() & 0x03);
	}
	
	public void mass(Accumulator a, BitDice r) {
		
		a.add(a.minCount());
		
		int rand = r.nextInt();
		a.result(rand & 0x03);
		rand >>>= 2;
		int bits = 30;
		
		while(!a.isDone()) {
			if(bits < 2) {
				rand = r.nextInt();
				bits = 32;
			}
			
			a.result(rand & 0x03);
			rand >>>= 2;
			bits -= 2;
		}
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
		return 2;
	}

	public boolean regen(int bits) {
		return bits < 2;
	}

	public int r(int rand, int bits) {
		return rand & 0x03;
	}
}
