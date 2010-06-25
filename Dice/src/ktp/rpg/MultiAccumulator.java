package ktp.rpg;

final class MultiAccumulator implements Accumulator {
	private int originalCount;
	private int count;
	private int sum;
	private int faces;
	private byte[] resultSet;
	
	public void init(int count, int faces) {
		this.count = this.originalCount = count;
		this.sum = 0;
		this.resultSet = new byte[count];
		this.faces = faces;
	}

	@Override
	public void add(int value) {
		sum += value;
	}

	@Override
	public int nextDie() {
		return count > 0 ? faces : 0;
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
