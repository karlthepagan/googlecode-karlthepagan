/*
 * This file is part of the MessageFuture library,
 * Copyright 2009 karlthepagan@gmaiil.com
 * 
 * The MessageFuture library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * The MessageFuture library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MessageFuture library.  If not, see http://www.gnu.org/licenses/
 */
package x.util.concurrent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;


public class LocalExecutor implements ExecutorService {
	static final Comparator<Object> IDENTITY_SORT = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			return System.identityHashCode(o1) - System.identityHashCode(o2);
		}
	};
	
	private final Executor _exec;
	private final AtomicBoolean _shutdown;
	private final ConcurrentMap<RunnableLockMessageFuture<?>, Object> _starting;
	private final ConcurrentMap<RunnableLockMessageFuture<?>, Object> _running;
	private final CopyOnWriteArrayList<BlockingQueue<? super RunnableLockMessageFuture<?>>> _completions;
	
	public LocalExecutor() {
		this(Executors.newSingleThreadExecutor());
	}
	
	public LocalExecutor(Executor executor) {
		_exec = executor;
		_shutdown = new AtomicBoolean(false);
		_starting = new ConcurrentHashMap<RunnableLockMessageFuture<?>, Object>();
		_running = new ConcurrentHashMap<RunnableLockMessageFuture<?>, Object>();
		_completions = new CopyOnWriteArrayList<BlockingQueue<? super RunnableLockMessageFuture<?>>>();
	}
	
	protected <V> RunnableLockMessageFuture<V> newTaskFor(final Runnable run, final V data) {
		RunnableLockMessageFuture<V> task = new RunnableLockMessageFuture<V>() {
			@Override
			protected V innerRun() throws Exception {
				run.run();
				return data;
			}
			
			@Override
			protected void onCancel(boolean mayInterruptIfRunning) {
			}
		};
		task.onInitialize(run);
		return task;
	}
	
	protected <V> RunnableLockMessageFuture<V> newTaskFor(final Callable<V> task) {
		RunnableLockMessageFuture<V> runTask = new RunnableLockMessageFuture<V>() {
			@Override
			protected V innerRun() throws Exception {
				return task.call();
			}
			
			@Override
			protected void onCancel(boolean mayInterruptIfRunning) {
			}
		};
		runTask.onInitialize(task);
		return runTask;
	}

	@Override
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
	
	// mercilessly refactor!
	@SuppressWarnings("unchecked")
	protected <T> List<Future<T>> invoke(
			Collection<? extends Callable<T>> tasks, long timeoutNanos,
			int doneCount, int successCount) throws InterruptedException {
		
		Iterator<? extends Callable<T>> taskIter = tasks.iterator();
		ArrayList<Future<T>> runners = new ArrayList<Future<T>>(tasks.size());
		ArrayBlockingQueue<RunnableLockMessageFuture<?>> queue = new ArrayBlockingQueue<RunnableLockMessageFuture<?>>(tasks.size());
		Object[] sorted = null;
		
		try {
			_completions.add(queue);
			
			while(taskIter.hasNext()) {
				RunnableLockMessageFuture<T> task = newTaskFor(taskIter.next());
				_exec.execute(task);
				runners.add(task);
			}
			
			sorted = identityArray(runners);
			
			if(timeoutNanos < 0) {
				while(doneCount != 0 && successCount != 0) {
					RunnableLockMessageFuture<?> future = queue.take();
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
					
					RunnableLockMessageFuture<?> future = queue.poll(waitNanos,
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
	
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		
		if(tasks.isEmpty()) return Collections.emptyList();
		
		return invoke(tasks, -1, tasks.size(), -1);
	}
	
	static <T> Object[] identityArray(Collection<T> source) {
		Object[] result = source.toArray();
		Arrays.sort(result,IDENTITY_SORT);
		return result;
	}
	
	static boolean contains(Object[] identityArray, Object element) {
		return Arrays.binarySearch(identityArray, element, IDENTITY_SORT) >= 0;		
	}
	
	static int indexOf(Object[] identityArray, Object element) {
		return Arrays.binarySearch(identityArray, element);
	}
	
	@Override
	public <T> List<Future<T>> invokeAll(
			Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		if(tasks.isEmpty()) return Collections.emptyList();
		
		long timeoutNanos = System.nanoTime() + unit.toNanos(timeout);
		
		List<Future<T>> runners = invoke(tasks, timeoutNanos, tasks.size(), -1);
		
		// could be more efficient than O(n) but requires more memory
		for(Future<T> future : runners) {
			if(false == future.isDone())
				future.cancel(true);
		}
		
		return runners;
	}
	
	static final Object NONE = new Object();
	
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		List<Future<T>> runners = invoke(tasks, -1, tasks.size(), 1);
		
		// success tracks any successful returns, null is a valid result
		boolean success = false;
		T result = null;
		ExecutionException exception = null;
		
		for(Future<T> future : runners) {
			try {
				if(future.isDone()) {
					result = future.get();
					success = true;
				} else {
					future.cancel(true);
				}
				// TODO test behavior of interrupted exception
			} catch(ExecutionException e) {
				exception = e;
			} catch(RuntimeException e) {
				exception = new ExecutionException(e);
			}
		}
		
		if(success) {
			return result;
		} else if(exception != null) {
			throw exception;
		} else {
			// assert, this is a logical conflict
			throw new ExecutionException(new RuntimeException("no invoked results"));
		}
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		long timeoutNanos = System.nanoTime() + unit.toNanos(timeout);
		
		List<Future<T>> runners = invoke(tasks, timeoutNanos, tasks.size(), 1);
		
		// success tracks any successful returns, null is a valid result
		boolean success = false;
		T result = null;
		ExecutionException exception = null;
		
		for(Future<T> future : runners) {
			try {
				if(future.isDone()) {
					result = future.get();
					success = true;
				} else {
					future.cancel(true);
				}
				// TODO test behavior of interrupted exception
			} catch(ExecutionException e) {
				exception = e;
			} catch(RuntimeException e) {
				exception = new ExecutionException(e);
			}
		}
		
		if(success) {
			return result;
		} else if(exception != null) {
			throw exception;
		} else {
			throw new TimeoutException();
		}
	}

	@Override
	public boolean isShutdown() {
		return _shutdown.get();
	}

	@Override
	public boolean isTerminated() {
		return _shutdown.get() && _running.isEmpty();
	}

	@Override
	public void shutdown() {
		_shutdown.set(true);
		for (RunnableLockMessageFuture<?> k : _running.keySet()) {
			k.cancel(false);
		}
		
		_running.keySet().removeAll(_starting.keySet());
	}

	@Override
	public List<Runnable> shutdownNow() {
		_shutdown.set(true);
		
		for (RunnableLockMessageFuture<?> k : _running.keySet()) {
			k.cancel(true);
		}
		
		_running.keySet().removeAll(_starting.keySet());
		
		// returns a list of tasks "which never commenced execution"
		return new ArrayList<Runnable>(_starting.keySet());
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		if(_shutdown.get())
			throw new RejectedExecutionException();
		
		RunnableLockMessageFuture<T> runTask = newTaskFor(task);
		_exec.execute(runTask);
		return runTask;
	}
	
	@Override
	public Future<?> submit(Runnable task) {
		RunnableLockMessageFuture<?> runTask = newTaskFor(task, null);
		_exec.execute(runTask);
		return runTask;
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		RunnableLockMessageFuture<T> runTask = newTaskFor(task, result);
		_exec.execute(runTask);
		return runTask;
	}

	@Override
	public void execute(Runnable command) {
		RunnableLockMessageFuture<?> runTask = newTaskFor(command, null);
		
		_exec.execute(runTask);
	}
	
	protected abstract class RunnableLockMessageFuture<V> extends
			LockMessageFuture<V> implements RunnableFuture<V> {
		protected abstract V innerRun() throws Exception;
		
//		public ATTACHMENT attachment() { return null; }
		
		public void onInitialize(Object run) {
			_starting.put(this, run);
			_running.put(this, run);
		}
		
		public void onStart() {
			_starting.remove(this);
		}
		
		@Override
		public void run() {
			// double-start prevention? kinda
			if(isDone()) return;
			
			try {
				onStart();
				offer(innerRun());
//			} catch(InterruptedException e) {
//			} catch(InterruptedIOException e) {
//				// contains very useful data (bytes written before interrupt)
//				// TODO consider overriding or storing exception?
			} catch(Throwable t) {
				// if canceled offer fails, otherwise interrupted shows up as exception
				offerException(t);
			}
		}
		
		@Override
		protected void onDone() {
			_running.remove(this);
			for(BlockingQueue<? super RunnableLockMessageFuture<?>> completion : _completions) {
				completion.offer(this);
			}
		}
	}
}
