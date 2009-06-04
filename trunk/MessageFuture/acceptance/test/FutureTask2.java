package test;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class FutureTask2<V> extends FutureTask<V> {
	public FutureTask2(Callable<V> callable) {
		super(callable);
	}

	public void set(V value) {
		super.set(value);
	}
	
	public void setException(Throwable t) {
		super.setException(t);
	}
}
