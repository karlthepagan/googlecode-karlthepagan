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
	
	public int mass(Accumulator a, BitDice r, int remainder) {
		int rand;
		int bits;
		if(remainder == 0) {
			rand = r.nextInt();
			bits = 32;
		} else {
			rand = remainder & 0x07FFFFFF;
			bits = remainder >>> 27;
		}
		int v;
		
		do {
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
		} while(a.nextDie() == 3);
		
		// 27 or more bits remain
		if(bits >= 27)
			return 0xD8000000 | (0x05FFFFFF & rand);
		
		return bits << 27 | (0x05FFFFFF & rand);
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
