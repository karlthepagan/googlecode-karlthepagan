package ktp.rpg;

/**
 * d2 - clean 1 bit
 */
final class D2 extends Die {
	public int roll(BitDice r) {
		return 1 + (r.nextInt() & 0x01);
	}
	
	public void mass(Accumulator a, BitDice r) {
		
		a.add(a.minCount());
		
		int rand = r.nextInt();
		a.result(rand & 0x01);
		rand >>>= 1;
		int bits = 31;
		
		while(!a.isDone()) {
			if(bits < 1) {
				rand = r.nextInt();
				bits = 32;
			}
			
			a.result(rand & 0x01);
			rand >>>= 1;
			bits--;
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
		return 1;
	}

	public boolean regen(int bits) {
		return bits < 1;
	}

	public int r(int rand, int bits) {
		return rand & 0x01;
	}
}
