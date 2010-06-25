package ktp.rpg;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface IDice {
	/**
	 * roll one die with the number of faces
	 * @param faces 2,3,4,5,6,8,10,12,20
	 * @return value of the roll
	 */
	int d(int faces);
	
	/**
	 * roll multiple dice with the number of faces
	 * @param faces 2,3,4,5,6,8,10,12,20
	 * @param count number of rolls
	 * @return sum of the roll values
	 */
	long d(int faces, int count);
	
	/**
	 * Roll multiple dice and return all individual values
	 * @param faces 2,3,4,5,6,8,10,12,20
	 * @param count number of rolls
	 * @return array of result values
	 */
	byte[] multi(int faces, int count);
	
	/**
	 * Offset applied to multi or hist roll results in order
	 * to get the exact face number.
	 */
	int multiOffset(int faces);
	
	/**
	 * Roll multiple dice and return all values in a buffer
	 * @param dst destination for the result values
	 * @param faces 2,3,4,5,6,8,10,12,20
	 * @param count number of rolls
	 * @return sum of the roll values
	 */
	long multi(ByteBuffer dst, int faces, int count);
	
	/**
	 * Roll multiple dice and return all values in a buffer showing the number of rolls with each value.
	 * Index zero is the total sum.
	 * @param dst destination for the result histogram
	 * @param faces 2,3,4,5,6,8,10,12,20
	 * @param count number of rolls
	 * @param min minimum value to be included in sum and hist
	 * @param max maximum value to be included in sum and hist
	 * @return sum of the roll values
	 */
	long hist(IntBuffer dst, int faces, int count, int min, int max);
	
	/**
	 * 
	 * @param a
	 * @param faces
	 */
	void roll(Accumulator a, int faces);
	
	/**
	 * 
	 * @param a
	 */
	public void roll(Accumulator a);
	
//	void m(ShortBuffer dst, ShortBuffer spec);
//	short[] m(short[] faces);
//	short[] m(short[] faces, short[] count);
	
	/**
	 * Total bits consumed.
	 */
	int totalBits();
}
