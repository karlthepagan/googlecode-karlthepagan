package x.util.concurrent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractMessageExecutor {

	static final Comparator<Object> IDENTITY_SORT = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			return System.identityHashCode(o1) - System.identityHashCode(o2);
		}
	};
	
	protected final AtomicBoolean _shutdown;
	protected final ConcurrentMap<Object, ExecutorFuture<?>> _starting;
	protected final ConcurrentMap<Object, ExecutorFuture<?>> _running;
	protected final CopyOnWriteArrayList<BlockingQueue<? super ExecutorFuture<?>>> _completions;

	public AbstractMessageExecutor() {
		super();
		_shutdown = new AtomicBoolean(false);
		_starting = new ConcurrentHashMap<Object, ExecutorFuture<?>>();
		_running = new ConcurrentHashMap<Object, ExecutorFuture<?>>();
		_completions = new CopyOnWriteArrayList<BlockingQueue<? super ExecutorFuture<?>>>();
	}

	static <T> Object[] identityArray(Collection<T> source) {
		Object[] result = source.toArray();
		Arrays.sort(result,IDENTITY_SORT);
		return result;
	}
	
	protected abstract void execute(ExecutorFuture<?> task);
	
	protected abstract <T> ExecutorFuture<T> newTaskFor(final Callable<T> task);
	
	protected abstract List<Runnable> toShutdownList(Collection<?> starting);

	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO bad api design, this feature corrects that?
		// Sun implementation will block for full timeout if not shutdown
		// I choose to return immediately if precondition not met
		if(false == _shutdown.get()) return false;
		
		if(_running.isEmpty()) return true;
		
		long timeoutNanos = System.nanoTime() + unit.toNanos(timeout);
		
		// figure out how many futures to wait for, make queue wait, preserve timeout
		ArrayBlockingQueue<Future<?>> queue = new ArrayBlockingQueue<Future<?>>(_running.size());
		_completions.add(queue);
		int count = _running.size();
		
		try {
			long waitNanos = 0;
			while ((waitNanos = timeoutNanos - System.nanoTime()) > 0
					&& count > 0) {
				if(null != queue.poll(waitNanos, TimeUnit.NANOSECONDS))
					count--;
			}
			
			while(count > 0 && null != queue.poll())
				count--;
			
			return count <= 0;
		} finally {
			_completions.remove(queue);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> List<Future<T>> invoke(Collection<? extends Callable<T>> tasks, long timeoutNanos, int doneCount,
			int successCount) throws InterruptedException {
		
		if(_shutdown.get())
			throw new RejectedExecutionException();
		
		Iterator<? extends Callable<T>> taskIter = tasks.iterator();
		ArrayList<Future<T>> runners = new ArrayList<Future<T>>(tasks.size());
		ArrayBlockingQueue<ExecutorFuture<?>> queue = new ArrayBlockingQueue<ExecutorFuture<?>>(tasks.size());
		Object[] sorted = null;
		// error discovering new EPMD backend
		// restart classcast exception in configuration
		try {
			_completions.add(queue);
			
			while(taskIter.hasNext()) {
				ExecutorFuture<T> task = newTaskFor(taskIter.next());
				execute(task);
				runners.add(task);
			}
			
			sorted = identityArray(runners);
			
			if(timeoutNanos < 0) {
				while(doneCount != 0 && successCount != 0) {
					ExecutorFuture<?> future = queue.take();
					if(contains(sorted,future)) {
						doneCount--;
					
						if(future.isSuccess())
							successCount--;
					}
				}
			} else {
				long waitNanos = 0;
				while ((waitNanos = timeoutNanos - System.nanoTime()) > 0
						&& doneCount != 0 && successCount != 0) {
					
					ExecutorFuture<?> future = queue.poll(waitNanos,
							TimeUnit.NANOSECONDS);
					if(future == null) continue;
					// continue tests timeout
					// and protects us from spurious return
					if(contains(sorted,future)) {
						doneCount--;
					
					if(future.isSuccess())
						successCount--;
					}
				}
				
				Future<T> future = null;
				while ((future = (Future<T>) queue.poll()) != null
						&& doneCount != 0 && successCount != 0) {
					if(contains(sorted,future))
						doneCount--;
				}
			}
		} finally {
			_completions.remove(queue);
		}
		
		return runners;
	}

	protected static boolean contains(Object[] identityArray, Object element) {
		return Arrays.binarySearch(identityArray, element, IDENTITY_SORT) >= 0;		
	}

	protected static int indexOf(Object[] identityArray, Object element) {
		return Arrays.binarySearch(identityArray, element);
	}

	public boolean isShutdown() {
		return _shutdown.get();
	}

	public boolean isTerminated() {
		return _shutdown.get() && _running.isEmpty();
	}

	public void shutdown() {
		_shutdown.set(true);
		for (ExecutorFuture<?> k : _running.values()) {
			k.cancel(false);
		}
		
		// is this O(n^2)?
		_running.values().removeAll(_starting.values());
	}

	public List<Runnable> shutdownNow() {
		_shutdown.set(true);
		
		for (ExecutorFuture<?> k : _running.values()) {
			k.cancel(true);
		}
		
		// is this O(n^2)?
		_running.values().removeAll(_starting.values());
		
		// returns a list of tasks "which never commenced execution"
		return toShutdownList(_starting.values());
	}
	
	protected abstract class ExecutorFuture<V> extends
		LockMessageFuture<V> {

		public abstract Object task();

		// public ATTACHMENT attachment() { return null; }

		public void onInitialize() {
			_starting.put(task(), this);
			_running.put(task(), this);
		}

		public void onStart() {
			_starting.remove(task());
		}

		@Override
		protected void onDone() {
			_running.remove(task());
			for (BlockingQueue<? super ExecutorFuture<?>> completion : _completions) {
				completion.offer(this);
			}
		}
	}
}