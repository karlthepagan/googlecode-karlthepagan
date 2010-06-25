package ktp.rpg;

/** d20 - > 19 recover 2 LSB - > 23 recover 3 LSB
 * 00000
 * 01111
 * 10000
 * 10001
 * 10010
 * 10011
 * 10100 X recover 2 LSB
 * 10101 X
 * 10110 X
 * 10111 X
 * 11000 X recover 3 LSB
 * 11001 X
 * 11010 X
 * 11011 X
 * 11100 X
 * 11101 X
 * 11110 X
 * 11111 X
 * 
 * used - 5 stored in MSB (5 needed)
 */
final class D20 extends Die {
	public int massStart(int minCount) {
		return minCount;
	}

	public int massValue(int v) {
		return v & 0x1F;
	}

	public int r(int rand, int bits) {
		int wrand = rand;
		int used = 5;
		int r = wrand & 0x1F;
		
		while(r > 19) {
			if(r > 23) {
				wrand = (wrand & 0x07) ^ wrand >>> 2;
				used += 2;
			} else {
				wrand = (wrand & 0x03) ^ wrand >>> 3;
				used += 3;
			}
			
			if( used > bits ) return -1;
			
			r = wrand & 0x1F;
		}
		
		return r + (used << 5);
	}

	public int value(int v) {
		return 1 + (v & 0x1F);
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
			if(bits < 5) {
				rand = r.nextInt();
				bits = 32;
			}
			
			v = rand & 0x1F;
			
			if(v > 19) {
				// recover 2 LSB
				rand = (rand & 0x03) ^ rand >>> 3;
				bits -= 3;
				continue;
			}
			
			if(v > 23) {
				// recover 3 LSB
				rand = (rand & 0x07) ^ rand >>> 2;
				bits -= 3;
				continue;
			}
			
			// consumed all
			rand >>>= 5;
			bits -= 5;
			
			a.result(v);
		} while(a.nextDie() == 20);
		
		// 27 or more bits remain
		if(bits >= 27)
			return 0xD8000000 | (0x05FFFFFF & rand);
		
		return bits << 27 | (0x05FFFFFF & rand);
	}

	public int bits(int i) {
		return i >>> 5;
	}

	public boolean regen(int bits) {
		return bits < 5;
	}

	@Override
	int multiOffset() {
		return 1;
	}
}
