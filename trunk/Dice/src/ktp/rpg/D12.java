package ktp.rpg;

/** d12 - > 11 recover 2 LSB
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
 * 1010
 * 1011
 * 1100 X recover 2 LSB
 * 1101 X
 * 1110 X
 * 1111 X
 * 
 * (used - 4) / 2 encoded in 4 MSB (4 needed!)
 */
final class D12 extends Die {
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
		
		while(r > 11) {
			wrand = (wrand & 0x03) ^ wrand >>> 2;
			used += 2;
			
			if( used > bits ) return -1;
			
			r = wrand & 0x0F;
		}
		
		return r + (used << 4);
	}

	public int value(int v) {
		return 1 + (v & 0x0F);
	}

	public int roll(SecureRandomDice r) {
		int v;
		do {
			v = r(r.nextInt(), 32);
		} while(v == -1);
		return value(v);
	}
	
	public void mass(Accumulator a, SecureRandomDice r) {
		a.add(a.minCount());
		
		int rand = r.nextInt();
		int bits = 32;
		int v;
		
		while(!a.isDone()) {
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
			
			// consumed all
			rand >>>= 4;
			bits -= 4;
			
			a.result(v);
		}
	}

	public int bits(int i) {
		return i >>> 4;
	}

	public boolean regen(int bits) {
		return bits < 4;
	}
}
