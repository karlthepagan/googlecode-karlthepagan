package ktp.rpg;

/** d10 - > 9 recover LSB, > 11 recover 2 LSB
 * 0000
 * 0001
 * 0010
 * 0011
 * 0100
 * 0101
 * 0110
 * 0111
 * 1000
 * 1001
 * 1010 X recover LSB
 * 1011 X
 * 1100 X recover 2 LSB
 * 1101 X
 * 1110 X
 * 1111 X
 * 
 * used encoded in 4 MSB (5 needed!)
 */
final class D10 extends Die {
	public int massStart(int minCount) {
		return minCount;
	}

	public int massValue(int v) {
		return v & 0x0F;
	}

	public int r(int rand, int bits) {
		int wrand = rand;
		int used = 4;
		int r = wrand & 0x0F;
		
		while(r > 9) {
			if(r > 11) {
				wrand = (wrand & 0x03) ^ wrand >>> 2;
				used += 2;
			} else {
				wrand = (wrand & 0x01) ^ wrand >>> 3;
				used += 3;
			}
			
			if( used > bits ) return -1;
			
			r = wrand & 0x0F;
		}
		
		return r + (used << 4);
	}

	public int value(int v) {
		return 1 + (v & 0x0F);
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
			if(bits < 4) {
				rand = r.nextInt();
				bits = 32;
			}
			
			v = rand & 0x0F;
			
			if(v > 11) {
				// recover 2 LSB
				rand = (rand & 0x03) ^ rand >>> 2;
				bits -= 2;
				continue;
			}
			
			if(v > 9) {
				// recover LSB
				rand = (rand & 0x01) ^ rand >>> 3;
				bits -= 3;
				continue;
			}
			
			// consumed all
			rand >>>= 4;
			bits -= 4;
			
			a.result(v);
		} while(a.nextDie() == 10);
		
		// 27 or more bits remain
		if(bits >= 27)
			return 0xFA000000 | rand;
		
		return bits << 27 | (0x05FFFFFF & rand);
	}

	public int bits(int i) {
		return i >>> 4;
	}

	public boolean regen(int bits) {
		return bits < 4;
	}

	@Override
	int multiOffset() {
		return 1;
	}
}
