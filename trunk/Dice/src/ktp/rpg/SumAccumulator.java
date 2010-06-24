package ktp.rpg;

final class SumAccumulator implements Accumulator {
	private int originalCount;
	public int count;
	public int sum;
	
	public void init(int count) {
		this.count = this.originalCount = count;
		this.sum = 0;
	}

	@Override
	public boolean isDone() {
		return count <= 0;
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
}
