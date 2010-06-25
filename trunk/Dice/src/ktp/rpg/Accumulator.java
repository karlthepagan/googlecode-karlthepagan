package ktp.rpg;

/**
 * Accumulator interface for mass rolling schemes.
 */
// TODO so far interface accumulator tests faster than abstract on N280.
public interface Accumulator {
	/** @return the face count of the next roll, zero if no roll needed */
	int nextDie();
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
