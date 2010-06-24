package ktp.rpg;

/**
 * Accumulator interface for mass rolling schemes.
 */
// TODO so far interface accumulator tests faster than abstract on N280.
interface Accumulator {
	/** @return true if conditions for success or failure have been met */
	boolean isDone();
	/** Append a result to this accumulated set. */
	void result(int value);
	/**
	 * Add a flat value to this set's sum.
	 * <p/>
	 * Needed because BitDice is more efficient producing 0-N results.
	 */
	void add(int value);
	/** Minimum number of rolls in this run. */
	int minCount();
}
