package ktp.rpg;

abstract class Die {
	abstract int roll(BitDice r);
	abstract void mass(Accumulator a, BitDice r);

	abstract int r(int rand, int bits);
	abstract int value(int v);
	abstract int massValue(int v);
	abstract boolean regen(int bits);
	abstract int bits(int i);
	abstract int massStart(int minCount);
}
