package ktp.rpg;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.SecureRandom;
import java.util.Random;


/**
 * Dice which make very efficient use of 
 */
class SecureRandomDice implements IDice {
	
	private final Random _r;
	private int _ints = 0;
	
	public SecureRandomDice() {
		this(4);
	}
	
	public SecureRandomDice(int numSeedBytes) {
		this(new SecureRandom(SecureRandom.getSeed(numSeedBytes)));
	}
	
	public SecureRandomDice(Random rand) {
		_r = rand;
	}
	
	/**
	 * d2 - clean 1 bit
	 */
	static int d2(int rand) {
		return rand & 0x01;
	}
	static int d2bits(int v) {
		return 1;
	}
	static int d2value(int v) {
		return 1 + v;
	}
	static int d2massStart(int count) {
		return count;
	}
	static int d2massValue(int v) {
		return v;
	}
	static boolean d2regen(int bits) {
		return bits < 1;
	}
	
	/** 
	 * d3 - +1 - == 0 recover none
	 * 00 X
	 * 01
	 * 10
	 * 11
	 * 
	 * encode used in 6 MSB (5 needed)
	 */
	static int d3(int rand) {
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
	static int d3bits(int v) {
		return v >>> 2;
	}
	static int d3value(int v) {
		return v & 0x03;
	}
	static int d3massStart(int count) {
		return 0;
	}
	static int d3massValue(int v) {
		return v & 0x03;
	}
	static boolean d3regen(int bits) {
		return bits < 2;
	}
	
	/**
	 * d4 - clean 2 bit + 1
	 */
	static int d4(int rand) {
		return rand & 0x03;
	}
	static int d4bits(int v) {
		return 2;
	}
	static int d4value(int v) {
		return 1 + v;
	}
	static int d4massStart(int count) {
		return count;
	}
	static int d4massValue(int v) {
		return v;
	}
	static boolean d4regen(int bits) {
		return bits < 2;
	}
	
	/** 
	 * d5 - > 4 recover none, > 5 recover LSB
	 * 000
	 * 001
	 * 010
	 * 011
	 * 100
	 * 101 X
	 * 110 X recover LSB
	 * 111 X
	 * 
	 * encode used in 5 MSB (5 needed)
	 */
	static int d5(int rand, int bits) {
		int wrand = rand;
		int used = 3;
		int r = wrand & 0x07;
		
		while(r > 4) {
			if(r > 5) {
				wrand = (wrand & 0x01) ^ wrand >>> 2;
				used += 2;
			} else {
				wrand >>>= 3;
				used += 3;
			}
			
			if( used > bits ) return -1;
			
			r = wrand & 0x07;
		}
		
		return r + (used << 3);
	}
	static int d5bits(int v) {
		return v >>> 3;
	}
	static int d5value(int v) {
		return 1 + (v & 0x07);
	}
	static int d5massStart(int count) {
		return count;
	}
	static int d5massValue(int v) {
		return v & 0x07;
	}
	static boolean d5regen(int bits) {
		return bits < 3;
	}
	
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
	static int d6(int rand, int bits) {
		int wrand = rand;
		int used = 3;
		int r = wrand & 0x07;
		
		while(r > 5) {
			wrand = (wrand & 0x01) ^ wrand >>> 2;
			used++;
			
			if( used > bits ) return -1;
			
			r = wrand & 0x07;
		}
		
		return r + (used << 3);
	}
	static int d6bits(int v) {
		return v >>> 3;
	}
	static int d6value(int v) {
		return 1 + (v & 0x07);
	}
	static int d6massStart(int count) {
		return count;
	}
	static int d6massValue(int v) {
		return v & 0x07;
	}
	static boolean d6regen(int bits) {
		return bits < 3;
	}
	
	/**
	 * d8 - clean 3 bit + 1
	 */
	static int d8(int rand) {
		return rand & 0x07;
	}
	static int d8bits(int v) {
		return 3;
	}
	static int d8value(int v) {
		return 1 + v;
	}
	static int d8massStart(int count) {
		return count;
	}
	static int d8massValue(int v) {
		return v;
	}
	static boolean d8regen(int bits) {
		return bits < 3;
	}
	
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
	static long d10(int rand, int bits) {
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
	static int d10bits(long v) {
		return (int)(v >>> 4);
	}
	static int d10value(long v) {
		return 1 + ((int)v & 0x0F);
	}
	static int d10massStart(int count) {
		return count;
	}
	static int d10massValue(long v) {
		return (int)v & 0x0F;
	}
	static boolean d10regen(int bits) {
		return bits < 4;
	}
	
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
	static long d12(int rand, int bits) {
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
	static int d12bits(long v) {
		return (int)(v >>> 4);
	}
	static int d12value(long v) {
		return 1 + ((int)v & 0x0F);
	}
	static int d12massStart(int count) {
		return count;
	}
	static int d12massValue(long v) {
		return (int)v & 0x0F;
	}
	static boolean d12regen(int bits) {
		return bits < 4;
	}
	
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
	static long d20(int rand, int bits) {
		int wrand = rand;
		int used = 5;
		int r = wrand & 0x01F;
		
		while(r > 19) {
			if(r > 23) {
				wrand = (wrand & 0x07) ^ wrand >>> 2;
				used += 2;
			} else {
				wrand = (wrand & 0x03) ^ wrand >>> 3;
				used += 3;
			}
			
			if( used > bits ) return -1;
			
			r = wrand & 0x01F;
		}
		
		return r + (used << 5);
	}
	static int d20bits(long v) {
		return (int)(v >>> 5);
	}
	static int d20value(long v) {
		return 1 + ((int)v & 0x01F);
	}
	static int d20massStart(int count) {
		return count;
	}
	static int d20massValue(long v) {
		return (int)v & 0x01F;
	}
	static boolean d20regen(int bits) {
		return bits < 5;
	}

	public int d(int faces) {
		int v = 0;
		long l = 0;
		switch(faces) {
		case 2:
			_ints++;
			return d2value(d2(_r.nextInt()));
		case 3:
			do {
				_ints++;
				v = d3(_r.nextInt());
			} while(v == -1);
			return d3value(v);
		case 4:
			_ints++;
			return d4value(d4(_r.nextInt()));
		case 5:
			do {
				_ints++;
				v = d5(_r.nextInt(),32);
			} while(v == -1);
			return d5value(v);
		case 6:
			do {
				_ints++;
				v = d6(_r.nextInt(),32);
			} while(v == -1);
			return d6value(v);
		case 8:
			_ints++;
			return d8value(d8(_r.nextInt()));
		case 10:
			do {
				_ints++;
				l = d10(_r.nextInt(),32);
			} while(l == -1);
			return d10value(l);
		case 12:
			do {
				_ints++;
				l = d12(_r.nextInt(),32);
			} while(l == -1);
			return d12value(l);
		case 20:
			do {
				_ints++;
				l = d20(_r.nextInt(),32);
			} while(l == -1);
			return d20value(l);
		default:
			throw new IllegalArgumentException("d" + faces + " not implemented");
		}
	}

	@Override
	public long d(int faces, int count) {
		int v = 0;
		long l = 0;
		_ints++;
		int rand = _r.nextInt();
		int bits = 32;
		int b = 0;
		long sum;
		switch(faces) {
		case 2:
			sum = d2massStart(count);
			b = d2bits(0);
			while(count > 0) {
				if(d2regen(bits)) {
					_ints++;
					rand = _r.nextInt();
					bits = 32;
				}
				
				v = d2((int)rand);
				
				sum += d2massValue(v);
				count--;
				bits -= b;
				rand = rand >>> b;
			}
			return sum;
		case 3:
			sum = d3massStart(count);
			while(count > 0) {
				if(d3regen(bits)) {
					_ints++;
					rand = _r.nextInt();
					bits = 32;
				}
				
				v = d3(rand);
				
				if(v == -1) {
					bits = 0;
					continue;
				}
				
				sum += d3massValue(v);
				count--;
				b = d3bits(v);
				bits -= b;
				rand = rand >>> b;
			}
			return sum;
		case 4:
			sum = d4massStart(count);
			b = d4bits(0);
			while(count > 0) {
				if(d4regen(bits)) {
					_ints++;
					rand = _r.nextInt();
					bits = 32;
				}
				
				v = d4(rand);
				
				sum += d4massValue(v);
				count--;
				bits -= b;
				rand = rand >>> b;
			}
			return sum;
		case 5:
			sum = d5massStart(count);
			while(count > 0) {
				if(d5regen(bits)) {
					_ints++;
					rand = _r.nextInt();
					bits = 32;
				}
				
				v = d5(rand,bits);
				
				if(v == -1) {
					bits = 0;
					continue;
				}
				
				sum += d5massValue(v);
				count--;
				b = d5bits(v);
				bits -= b;
				rand = rand >>> b;
			}
			return sum;
		case 6:
			sum = d6massStart(count);
			while(count > 0) {
				if(d6regen(bits)) {
					_ints++;
					rand = _r.nextInt();
					bits = 32;
				}
				
				v = d6(rand,bits);
				
				if(v == -1) {
					bits = 0;
					continue;
				}
				
				sum += d6massValue(v);
				count--;
				b = d6bits(v);
				bits -= b;
				rand = rand >>> b;
			}
			return sum;
		case 8:
			sum = d8massStart(count);
			b = d8bits(0);
			while(count > 0) {
				if(d8regen(bits)) {
					_ints++;
					rand = _r.nextInt();
					bits = 32;
				}
				
				v = d8(rand);
				
				sum += d8massValue(v);
				count--;
				bits -= b;
				rand = rand >>> b;
			}
			return sum;
		case 10:
			sum = d10massStart(count);
			while(count > 0) {
				if(d10regen(bits)) {
					_ints++;
					rand = _r.nextInt();
					bits = 32;
				}
				
				l = d10(rand,bits);
				
				if(l == -1) {
					bits = 0;
					continue;
				}
				
				sum += d10massValue(l);
				count--;
				b = d10bits(l);
				bits -= b;
				rand = rand >>> b;
			}
			return sum;
		case 12:
			sum = d12massStart(count);
			while(count > 0) {
				if(d12regen(bits)) {
					_ints++;
					rand = _r.nextInt();
					bits = 32;
				}
				
				l = d12(rand,bits);
				
				if(l == -1) {
					bits = 0;
					continue;
				}
				
				sum += d12massValue(l);
				count--;
				b = d12bits(l);
				bits -= b;
				rand = rand >>> b;
			}
			return sum;
		case 20:
			sum = d20massStart(count);
			while(count > 0) {
				if(d20regen(bits)) {
					_ints++;
					rand = _r.nextInt();
					bits = 32;
				}
				
				l = d20(rand,bits);
				
				if(l == -1) {
					bits = 0;
					continue;
				}
				
				sum += d20massValue(l);
				count--;
				b = d20bits(l);
				bits -= b;
				rand = rand >>> b;
			}
			return sum;
		default:
			throw new IllegalArgumentException("d" + faces + " not implemented");
		}
	}

	@Override
	public long hist(IntBuffer dst, int faces, int count, int min, int max) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] multi(int faces, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long multi(ByteBuffer dst, int faces, int count) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int totalBits() {
		return _ints * 32;
	}
}
