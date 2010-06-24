package ktp.rpg;

/** 
 * d3 - +1 - == 0 recover none
 * 00 X
 * 01
 * 10
 * 11
 * 
 * encode used in 6 MSB (5 needed)
 */
final class D3 extends Die {
	public int massStart(int minCount) {
		return 0;
	}

	public int massValue(int v) {
		return v & 0x03;
	}

	public int r(int rand, int bits) {
		if(rand == 0) return -1; // ensures that we have one valid result
		
		int wrand = rand;
		int used = 2;
		int r = wrand & 0x03;
		
		while(r == 0)
		{
			used += 2;
			wrand >>>= 2;
			r = wrand & 0x03;
		}
		
		return r + (used << 2);
	}

	public int value(int v) {
		return v & 0x03;
	}

	public int roll(BitDice r) {
		int rand = 0;
		int rv;
		
		// ensures that we have one valid result
		do {
			rand = r.nextInt();
		} while( rand == 0 );
		
		int used = 2;
		
		rv = rand & 0x03;
		
		while(rv == 0)
		{
			used += 2;
			rand >>>= 2;
			rv = rand & 0x03;
		}
		
		return rv;
	}
	
	public void mass(Accumulator a, BitDice r) {
		int rand = r.nextInt();
		int bits = 32;
		
		int v;
		
		while(!a.isDone()) {
			if( rand == 0 || bits < 2 ) {
				rand = r.nextInt();
				bits = 32;
			}
			
			do {
				bits -= 2;
				v = rand & 0x03;
				rand >>>= 2;
			} while(v == 0);
			
			a.result(v);
		}
	}

	public int bits(int i) {
		return i >>> 2;
	}

	public boolean regen(int bits) {
		return bits < 2;
	}

	@Override
	int multiOffset() {
		return 0;
	}
}
