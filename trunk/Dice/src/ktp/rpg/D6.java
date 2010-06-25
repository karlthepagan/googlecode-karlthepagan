package ktp.rpg;

/** d6 - > 5 recover LSB
 * 000
 * 001
 * 010
 * 011
 * 100
 * 101
 * 110 X
 * 111 X
 * 
 * encode used in 5 MSB (4 needed)
 */
final class D6 extends Die {
	public int massStart(int minCount) {
		return minCount;
	}

	public int massValue(int v) {
		return v & 0x07;
	}

	public int r(int rand, int bits) {
		int wrand = rand;
		int used = 3;
		int r = wrand & 0x07;
		
		while(r > 5) {
			wrand = (wrand & 0x01) ^ wrand >>> 2;
			used += 2;
			
			if( used > bits ) return -1;
			
			r = wrand & 0x07;
		}
		
		return r + (used << 3);
	}

	public int value(int v) {
		return 1 + (v & 0x07);
	}

	public int roll(BitDice r) {
		int v;
		do {
			v = r(r.nextInt(), 32);
		} while(v == -1);
		return value(v);
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
		int v;
		
		do {
			if(bits < 3) {
				rand = r.nextInt();
				bits = 32;
			}
			
			v = rand & 0x07;
			
			if(v > 5) {
				// recover LSB
				rand = (rand & 0x01) ^ rand >>> 2;
				bits -= 2;
				continue;
			}
			
			// consumed all
			rand >>>= 3;
			bits -= 3;
			
			a.result(v & 0x07);
		} while(a.nextDie() == 6);
		
		// 27 or more bits remain
		if(bits >= 27)
			return 0xD8000000 | (0x05FFFFFF & rand);
		
		return bits << 27 | (0x05FFFFFF & rand);
	}

	public int bits(int i) {
		return i >>> 3;
	}

	public boolean regen(int bits) {
		return bits < 3;
	}

	@Override
	int multiOffset() {
		return 1;
	}
}
