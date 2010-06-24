package ktp.rpg;

abstract class Die {
	abstract int roll(SecureRandomDice r);
	abstract void mass(Accumulator a, SecureRandomDice r);

	abstract int r(int rand, int bits);
	abstract int value(int v);
	abstract int massValue(int v);
	abstract boolean regen(int bits);
	abstract int bits(int i);
	abstract int massStart(int minCount);
}
