/*
 * This file is part of the MessageFuture library,
 * Copyright 2009 karlthepagan@gmail.com
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class LocalExecutor extends AbstractMessageExecutor implements ExecutorService {
	protected final Executor _exec;
	
	public LocalExecutor() {
		this(Executors.newSingleThreadExecutor());
	}
	
	public LocalExecutor(Executor executor) {
		super();
		_exec = executor;
	}

	@Override
	protected void execute(ExecutorFuture<?,?> task) {
		_exec.execute((RunnableExecutorFuture<?>)task);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<Runnable> toShutdownList(
			Collection<?> starting) {
		return new ArrayList<Runnable>((Collection<RunnableExecutorFuture<?>>)starting);
	}
	
	public void execute(Runnable command) {
		ExecutorFuture<?,?> runTask = newTaskFor(command, null);
		
		execute(runTask);
	}

	public Future<?> submit(Runnable task) {
		ExecutorFuture<?,?> runTask = newTaskFor(task, null);
		execute(runTask);
		return runTask;
	}

	public <T> Future<T> submit(Runnable task, T result) {
		ExecutorFuture<T,?> runTask = newTaskFor(task, result);
		execute(runTask);
		return runTask;
	}

	public <T> Future<T> submit(Callable<T> task) {
		ExecutorFuture<T,?> runTask = newTaskFor(task);
		execute(runTask);
		return runTask;
	}

	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		
		if(tasks.isEmpty()) return Collections.emptyList();
		
		return invoke(tasks, -1, tasks.size(), -1);
	}

	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
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

	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		List<Future<T>> runners = invoke(tasks, -1, tasks.size(), 1);

		// success tracks any successful returns, null is a valid result
		boolean success = false;
		T result = null;
		ExecutionException exception = null;

		for (Future<T> future : runners) {
			try {
				if (future.isDone()) {
					result = future.get();
					success = true;
				} else {
					future.cancel(true);
				}
				// TODO test behavior of interrupted exception
			} catch (ExecutionException e) {
				exception = e;
			} catch (RuntimeException e) {
				exception = new ExecutionException(e);
			}
		}

		if (success) {
			return result;
		} else if (exception != null) {
			throw exception;
		} else {
			// assert, this is a logical conflict
			throw new ExecutionException(new RuntimeException(
					"no invoked results"));
		}
	}

	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		long timeoutNanos = System.nanoTime() + unit.toNanos(timeout);

		List<Future<T>> runners = invoke(tasks, timeoutNanos, tasks.size(), 1);

		// success tracks any successful returns, null is a valid result
		boolean success = false;
		T result = null;
		ExecutionException exception = null;

		for (Future<T> future : runners) {
			try {
				if (future.isDone()) {
					result = future.get();
					success = true;
				} else {
					future.cancel(true);
				}
				// TODO test behavior of interrupted exception
			} catch (ExecutionException e) {
				exception = e;
			} catch (RuntimeException e) {
				exception = new ExecutionException(e);
			}
		}

		if (success) {
			return result;
		} else if (exception != null) {
			throw exception;
		} else {
			throw new TimeoutException();
		}
	}

	protected <V> ExecutorFuture<V,?> newTaskFor(final Runnable run, final V data) {
		if(_shutdown.get())
			throw new RejectedExecutionException();
		
		ExecutorFuture<V,?> task = new RunnableExecutorFuture<V>() {
			@Override
			protected V innerRun() throws Exception {
				run.run();
				return data;
			}
			
			@Override
			public Object task() {
				return run;
			}
		};
		task.onInitialize();
		return task;
	}

	protected <V> ExecutorFuture<V,?> newTaskFor(final Callable<V> task) {
		if(_shutdown.get())
			throw new RejectedExecutionException();
		
		ExecutorFuture<V,?> runTask = new RunnableExecutorFuture<V>() {
			@Override
			protected V innerRun() throws Exception {
				return task.call();
			}
			
			@Override
			public Object task() {
				return task;
			}
		};
		runTask.onInitialize();
		return runTask;
	}

	protected static final Thread IDLE = new Thread() {
		@Override
		public void interrupt() {
			// swallow fake interrupt calls
		}

		@Override
		public final void run() {
			// don't allow run
		}

		@Override
		public final void start() {
			// don't allow run
		}
	};
	
	protected abstract class RunnableExecutorFuture<V> extends
			ExecutorFuture<V,Object> implements RunnableFuture<V> {
	    
		private final AtomicReference<Thread> _thread = new AtomicReference<Thread>(IDLE);

		protected abstract V innerRun() throws Exception;

		@Override
		public void run() {
			// real double-start prevention
			if (false == _thread.compareAndSet(IDLE, Thread.currentThread()))
				return;

			if (isDone())
				return;

			try {
				onStart();
				offer(innerRun());
				// } catch(InterruptedException e) {
				// } catch(InterruptedIOException e) {
				// // contains very useful data (bytes written before interrupt)
				// // TODO consider overriding or storing exception?
			} catch (Throwable t) {
				// if canceled offer fails, otherwise interrupted shows up as
				// exception
				offerException(t);
			}
			
			_thread.set(IDLE);
			// cannot lazySet here because we shouldn't interrupt
			// the next future that uses this thread
		}
		
		@Override
		protected void onCancel(boolean mayInterruptIfRunning) {
			// TODO "may interrupt" should be interpreted as: cancel if work has begun
			// i.e. send remote cancelation message rather  than local
			super.onCancel(mayInterruptIfRunning);
			if(mayInterruptIfRunning) _thread.get().interrupt();
		}
	}
}
