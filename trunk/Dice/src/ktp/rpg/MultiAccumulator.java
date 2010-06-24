package ktp.rpg;

final class MultiAccumulator implements Accumulator {
	private int originalCount;
	private int count;
	private int sum;
	private byte[] resultSet;
	
	public void init(int count) {
		this.count = this.originalCount = count;
		this.sum = 0;
		this.resultSet = new byte[count];
	}

	@Override
	public void add(int value) {
		sum += value;
	}

	@Override
	public boolean isDone() {
		return count <= 0;
	}

	@Override
	public int minCount() {
		return originalCount;
	}

	@Override
	public void result(int value) {
		count--;
		sum += value;
		resultSet[count] = (byte)value;
	}
	
	public int sum() {
		return sum;
	}
	
	public byte[] resultSet() {
		return resultSet;
	}
}