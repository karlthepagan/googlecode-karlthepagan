package ktp.rpg;

import java.nio.IntBuffer;

final class HistogramAccumulator implements Accumulator {
	private int originalCount;
	private int count;
	private int sum;
	private int histOffset;
	private int faces;
	private IntBuffer resultHist;
	
	public void init(int count, int faces, int multiOffset, IntBuffer dst) {
		this.count = this.originalCount = count;
		this.sum = 0;
		this.faces = faces;
		resultHist = dst;
		histOffset = dst.position() - 1 + multiOffset; // 0-(N-1)
	}
	
	public void dispose() {
		this.resultHist = null;
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
		
		// TODO best optimization?
		resultHist.put(histOffset + value,
				resultHist.get(histOffset + value) + 1);
	}
	
	public int sum() {
		return sum;
	}
}
