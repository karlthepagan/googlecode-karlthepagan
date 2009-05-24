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

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import x.util.concurrent.LocalExecutor;
import x.util.concurrent.MessageFuture;

@SuppressWarnings("unchecked")
public class LocalExecutorTest {
	static final Object LOCK = new Object();
	ExecutorService parentExec = null;
	ExecutorService exec = null;
	ExecutorService exec2 = null;

	@Before
	public void setUp() throws Exception {
		parentExec = Executors.newFixedThreadPool(10);
		exec = new LocalExecutor(parentExec);
		exec2 = new LocalExecutor(parentExec);
	}

	@After
	public void tearDown() throws Exception {
		exec2.shutdownNow();
		exec2 = null;
		exec.shutdownNow();
		exec = null;
		parentExec.shutdownNow();
		parentExec = null;
	}

	@Test
	public void testInvokeAll() throws Exception {
		boolean[] wasRun = new boolean[]{false,false,false};
		List<Callable<Object>> calls = Arrays.asList(
				makeCallable(wasRun, 0, 0),
				makeDelayCallable(50, wasRun, 1, 1),
				makeDelayCallable(75, wasRun, 2, 2),
				makeDelayExcepting(15, 3 + " exception"),
				makeDelayExcepting(80, 4 + " exception"));
		List<Future<Object>> invoked = exec.invokeAll(calls);
		
		assertNotNull("1= invokeAll returns non-null",invoked);
		assertEquals("2= returned all items", calls.size(), invoked.size());
		for(Future<?> f : invoked) {
			assertTrue("3= each element of returned list isDone",f.isDone());
		}
		for(int i = 0; i < 3; i++) {
			assertEquals("4= items 0-2 return given data",
					Integer.valueOf(i), invoked.get(i).get());
		}
		for(int i = 3; i < 5; i++) {
			try {
				invoked.get(i).get();
				fail("5= items 3-4 throw expected exception");
			} catch(ExecutionException e) {
				assertEquals("6= items 3-4 throw correct exception", i + " exception",e.getCause().getMessage());
			}
		}
		for(boolean ran : wasRun) {
			assertTrue("9= each callable was run",ran);
		}
	}

	@Test
	public void testInvokeAllWithTimeout() throws Exception {
		boolean[] wasRun = new boolean[]{false,false,false};
		List<Callable<Object>> calls = Arrays.asList(
				makeCallable(wasRun, 0, 0),
				makeDelayCallable(50, wasRun, 1, 1),
				makeDelayCallable(1000, wasRun, 2, 2),
				makeDelayExcepting(75, 3 + " exception"),
				makeDelayCallable(2000, null, 0, 4));
		List<Future<Object>> invoked = exec.invokeAll(calls,100,TimeUnit.MILLISECONDS);
		
		assertNotNull("1= invokeAll returns non-null",invoked);
		assertEquals("2= returned all items", calls.size(), invoked.size());
		for(Future<Object> f : invoked) {
			assertTrue("3= each element of returned list isDone",f.isDone());
		}
		assertTrue("4= item 0 was run",wasRun[0]);
		invoked.get(0).get(); // first doesn't throw
		assertTrue("5= item 1 was run",wasRun[1]);
		invoked.get(1).get(); // second doesn't throw
		assertFalse("6= item 2 wasn't run",wasRun[2]);
		try {
			assertTrue("7= item 2 looks canceled",invoked.get(2).isCancelled());
			invoked.get(2).get();
			fail("8= item 2 was canceled");
		} catch( CancellationException e) {
		}
		try {
			invoked.get(3).get();
			fail("9= item 3 throws exception");
		} catch(ExecutionException e) {
			assertEquals("10= item 3 threw correct exception", 3 + " exception",e.getCause().getMessage());
		}
	}

	@Test
	public void testInvokeAny() throws Exception {
		// TODO ThreadPoolExecutor interleaves completion checks before
		// submitting more tasks, is that necessary?
		boolean[] wasRun = new boolean[]{false,false,false};
		List<Callable<Object>> calls = Arrays.asList(
				makeDelayExcepting(0, 0 + " exception"),
				makeDelayCallable(50, wasRun, 0, 1),
				makeDelayCallable(100, wasRun, 1, 2),
				makeDelayCallable(1000, wasRun, 2, 3));
		// this excepts because the inner invoke does not wait for a successful completion
		Object value = exec.invokeAny(calls);
		
		assertEquals("1= returned value of item 1", Integer.valueOf(1), value);
		assertTrue("2= item 1 was run",wasRun[0]);
		assertFalse("3= item 2 was not run",wasRun[1]);
		assertFalse("4= item 3 was not run",wasRun[2]);
		
		calls = Arrays.asList(
				makeDelayExcepting(0, 0 + " exception"),
				makeDelayExcepting(10, 1 + " exception"),
				makeDelayExcepting(15, 2 + " exception"),
				makeDelayExcepting(20, 3 + " exception"));
		try {
			exec.invokeAny(calls);
			fail("5= all excepted");
		} catch( ExecutionException e) {
		}
		
		try {
			exec.invokeAny(Collections.EMPTY_LIST);
			fail("6= cannot execute empty list");
		} catch( IllegalArgumentException e) {
		}
		
		Arrays.fill(wasRun, false);
		try {
			exec.invokeAny(Arrays.asList(
					makeCallable(wasRun, 0),
					(Callable<Object>)null));
			fail("7= cannot execute with null in list");
		} catch( NullPointerException e) {
		}
		assertFalse("8= item 0 wasn't run",wasRun[0]);
		
		try {
			exec.invokeAny(null);
			fail("9= cannot execute a null list");
		} catch(NullPointerException e) {
		}
	}

	@Test
	public void testInvokeAnyWithTimeout() throws Exception {
		boolean[] wasRun = new boolean[]{false,false,false};
		List<Callable<Object>> calls = Arrays.asList(
				makeDelayCallable(100, wasRun, 0),
				makeDelayCallable(150, wasRun, 1),
				makeDelayCallable(175, wasRun, 2));
		try {
			exec.invokeAny(calls,50,TimeUnit.MILLISECONDS);
			fail("1= invoke times out");
		} catch(TimeoutException e) {
		}
		
		assertFalse("2= first was not run",wasRun[0]);
		assertFalse("3= second was not run",wasRun[1]);
		assertFalse("4= second was not run",wasRun[2]);
	}

	@Test
	public void testShutdown() throws InterruptedException {
		exec.shutdown();
		final boolean[] wasRun = new boolean[]{false};
		try {
			exec.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					wasRun[0] = true;
					return null;
				}
			});
			fail("1= submit after shutdown did not throw");
		} catch(RejectedExecutionException e) {
		}
		
		assertFalse("2= task was not run after shutdown",wasRun[0]);
		assertTrue("3= looks shut down",exec.isShutdown());
		assertTrue("4= finished terminating",exec.awaitTermination(10,TimeUnit.MILLISECONDS));
		assertTrue("5= looks terminated",exec.isTerminated());
		assertFalse("6= peer is not shut down",exec2.isShutdown());
		assertFalse("7= peer is not terminated",exec2.isTerminated());
	}
	
	@Test
	public void testShutdownNow() throws InterruptedException {
		exec.submit(makeDelayCallable(100));
		List<Runnable> canceled = null;
		final boolean[] wasRun = new boolean[]{false};
		try {
			exec.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					wasRun[0] = true;
					return null;
				}
			});
			canceled = exec.shutdownNow();
			exec.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					wasRun[0] = true;
					return null;
				}
			});
			fail("1= submit after shutdown did not throw");
		} catch(RejectedExecutionException e) {
		}
		
		assertFalse("2= task was not run after shutdown", wasRun[0]);
		assertNotNull("3= shutdown list is not null", canceled);
		assertTrue("4= shutdown list has an element", 1 <= canceled.size());
		assertTrue("5= looks shut down",exec.isShutdown());
		assertTrue("6= finished terminating",exec.awaitTermination(10,TimeUnit.MILLISECONDS));
		assertTrue("7= looks terminated",exec.isTerminated());
		assertFalse("8= peer is not shut down",exec2.isShutdown());
		assertFalse("9= peer is not terminated",exec2.isTerminated());
	}
	
	protected Callable<Object> makeDelayExcepting(final long millis, final String message) {
		return new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
				}
				throw new Exception(message);
			}
		};
	}

	protected Callable<Void> makeDelayCallable(final long millis) {
		return new Callable<Void>() {
			@Override
			public Void call() {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
				}
				return null;
			}
		};
	}
	
	protected Callable<Object> makeDelayCallable(final long millis,
			final boolean[] wasRun, final int index) {
		return makeDelayCallable(millis,wasRun,index,null);
	}
	
	protected Callable<Object> makeDelayCallable(final long millis,
			final boolean[] wasRun, final int index, final Object data) {
		return new Callable<Object>() {
			@Override
			public Object call() {
				try {
					Thread.sleep(millis);
					if(wasRun != null)
						wasRun[index] = true;
				} catch (InterruptedException e) {
				}
				return data;
			}
		};
	}
	
	@Test
	public void testSubmitCallableOfT() throws Exception {
		final Object dataToken = new Object();
		final boolean[] wasRun = new boolean[]{false};
		Future<Object> future = exec.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				wasRun[0] = true;
				return dataToken;
			}
		});
		
		assertNotNull("1= future is not null",future);
		assertEquals("2= future returns token (and doesn't throw)", dataToken, future.get());
		assertTrue("3= runnable was run",wasRun[0]);
		assertTrue("4= future  is instanceof MessageFuture",future instanceof MessageFuture);
	}

	@Test
	public void testSubmitRunnable() throws Exception {
		final boolean[] wasRun = new boolean[]{false};
		Future<?> future = exec.submit(makeRunnable(wasRun));
		
		assertNotNull("1= future is not null",future);
		assertNull("2= future returns null (and doesn't throw)", future.get());
		assertTrue("3= runnable was run",wasRun[0]);
		assertTrue("4= future  is instanceof MessageFuture",future instanceof MessageFuture);
	}

	protected Callable<Object> makeCallable(final boolean[] wasRun,
			final int index) {
		return makeCallable(wasRun, index, null);
	}
	
	protected Callable<Object> makeCallable(final boolean[] wasRun,
			final int index, final Object data) {
		return new Callable<Object>() {
			@Override
			public Object call() {
				wasRun[index] = true;
				return data;
			}
		};
	}
	
	protected Runnable makeRunnable(final boolean[] wasRun) {
		return new Runnable() {
			@Override
			public void run() {
				wasRun[0] = true;
			}
		};
	}
	
	@Test
	public void testSubmitRunnableWithValue() throws Exception {
		final Object dataToken = new Object();
		final boolean[] wasRun = new boolean[]{false};
		Future<Object> future = exec.submit(makeRunnable(wasRun),dataToken);
		
		assertNotNull("1= future is not null",future);
		assertEquals("2= future returns token (and doesn't throw)", dataToken, future.get());
		assertTrue("3= runnable was run",wasRun[0]);
		assertTrue("4= future  is instanceof MessageFuture",future instanceof MessageFuture);
	}
	
	protected <T> T syncInvoke(Object object, String methodName,
			final Runnable run, Object ... args) throws Exception {
		Method method = method(object,methodName,run,args);
		
		Runnable lockRun = new Runnable() {
			@Override
			public void run() {
				synchronized(LOCK) {
					run.run();
					LOCK.notify();
				}
			}
		};
		
		synchronized(LOCK) {
			Object[] methodargs = new Object[args.length + 1];
			System.arraycopy(args, 0, methodargs, 1, args.length);
			methodargs[0] = lockRun;
			Object o = method.invoke(object, methodargs);
			LOCK.wait();
			return (T)o;
		}
	}
	
	// this is terrible! why doesn't Java give us a more flexible version of getMethod?
	protected Method method(Object object, String methodName, Object arg0,
			Object[] args) throws SecurityException, NoSuchMethodException {
		Class<?>[] argClasses = new Class<?>[args.length + 1];
		argClasses[0] = arg0.getClass();
		for(int i = 0; i < args.length; i++) {
			argClasses[i+1] = args[i].getClass();
		}
		
		List<Method> candidateMethods = new ArrayList<Method>();
		nextMethod:
		for(Method method : object.getClass().getMethods()) {
			if(!method.getName().equals(methodName))
				continue nextMethod;
			
			Class<?>[] methodTypes = method.getParameterTypes();
			if(methodTypes.length != argClasses.length)
				continue nextMethod;
			
			for(int i = 0; i < methodTypes.length; i++) {
				if(!methodTypes[i].isAssignableFrom(argClasses[i]))
					continue nextMethod;
			}
			
			candidateMethods.add(method);
		}
		
		if(candidateMethods.size() == 1)
			return candidateMethods.get(0);
		
		if(candidateMethods.size() > 1)
			throw new UnsupportedOperationException("oops, not yet implemented");
		// TODO overloaded method
		
		throw new NoSuchMethodException(methodName + Arrays.asList(argClasses));
	}
	
	protected <T> T syncInvoke(Object object, String methodName,
			final Callable<T> run, Object ... args) throws Exception {
		Method method = method(object,methodName,run,args);
		
		Callable<T> lockRun = new Callable<T>() {
			@Override
			public T call() throws Exception {
				synchronized(LOCK) {
					T ret = run.call();
					LOCK.notify();
					return ret;
				}
			}
		};
		
		synchronized(LOCK) {
			Object[] methodargs = new Object[args.length + 1];
			System.arraycopy(args, 0, methodargs, 1, args.length);
			methodargs[0] = lockRun;
			Object o = method.invoke(object, methodargs);
			LOCK.wait();
			return (T)o;
		}
	}
	
	@Test
	public void testExecute() throws InterruptedException {
		// poor man's mutable closure
		final boolean[] wasRun = new boolean[]{false};
		synchronized(LOCK) {
			exec.execute(new Runnable() {
				@Override
				public void run() {
					synchronized(LOCK) {
						wasRun[0] = true;
						LOCK.notify();
					}
				}
			});
			LOCK.wait();
		}
		
		assertTrue("1= runnable was run",wasRun[0]);
	}
	
	@Test
	public void testExecuteByUtility() throws Exception {
		// testing the framework - should operate same as testExecute
		final boolean[] wasRun = new boolean[]{false};
		syncInvoke(exec, "execute", makeRunnable(wasRun));
		
		assertTrue("1= runnable was run",wasRun[0]);
	}
}
