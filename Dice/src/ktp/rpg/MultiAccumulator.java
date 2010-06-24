package ktp.rpg;

final class MultiAccumulator implements Accumulator {
	private int originalCount;
	public int count;
	public int sum;
	
	public void init(int count) {
		this.count = this.originalCount = count;
		this.sum = 0;
	}

	@Override
	public void add(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int minCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void result(int value) {
		// TODO Auto-generated method stub

	}

}
