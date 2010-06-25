package ktp.rpg;

final class SumAccumulator implements Accumulator {
	private int originalCount;
	private int count;
	private int sum;
	private int faces;
	
	public void init(int count, int faces) {
		this.count = this.originalCount = count;
		this.sum = 0;
		this.faces = faces;
	}

	@Override
	public int nextDie() {
		return count > 0 ? faces : 0;
	}

	@Override
	public void result(int value) {
		count--;
		sum += value;
	}
	
	@Override
	public void add(int value) {
		sum += value;
	}


	@Override
	public int minCount() {
		return originalCount;
	}
	
	public int sum() {
		return sum;
	}
}
