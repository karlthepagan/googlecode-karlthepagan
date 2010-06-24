package ktp.rpg;

import java.nio.IntBuffer;

final class BoundedHistogramAccumulator implements Accumulator {
	private int count;
	private int sum;
	private int histOffset;
	private IntBuffer resultHist;
	private byte min;
	private byte max;
	
	public void init(int count, int multiOffset, IntBuffer dst, int min, int max) {
		this.count = count;
		this.sum = 0;
		resultHist = dst;
		histOffset = dst.position() - 1 + multiOffset; // 0-(N-1)
		this.min = (byte)(min - multiOffset);
		this.max = (byte)(max - multiOffset);
	}
	
	public void dispose() {
		this.resultHist = null;
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
		return 0;
	}

	@Override
	public void result(int value) {
		count--;
		if(value < min || value > max)
			return;
		
		sum += value;
		sum++;
		
		// TODO best optimization?
		resultHist.put(histOffset + value,
				resultHist.get(histOffset + value) + 1);
	}
	
	public int sum() {
		return sum;
	}
}
