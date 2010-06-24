package ktp.rpg;

interface Accumulator {
	boolean isDone();
	void result(int value);
	void add(int value);
	int minCount();
}
