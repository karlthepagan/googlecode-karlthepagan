package ktp.rpg;

/**
 * d8 - clean 3 bit + 1
 */
final class D8 extends Die {
	public int roll(SecureRandomDice r) {
		return 1 + (r.nextInt() & 0x07);
	}
	
	public void mass(Accumulator a, SecureRandomDice r) {
		
		a.add(a.minCount());
		
		int rand = r.nextInt();
		a.result(rand & 0x07);
		rand >>>= 3;
		int bits = 29;
		
		while(!a.isDone()) {
			if(bits < 3) {
				rand = r.nextInt();
				bits = 32;
			}
			
			a.result(rand & 0x07);
			rand >>>= 3;
			bits -= 3;
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
		return 3;
	}

	public boolean regen(int bits) {
		return bits < 3;
	}

	public int r(int rand, int bits) {
		return rand & 0x07;
	}
}
