package test;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.util.concurrent.SettableFuture;

import x.util.concurrent.LockMessageFuture;
import x.util.concurrent.MessageTask;
import x.util.concurrent.UnsafeFuture;


public class TestFutureMemUse extends EmpiricalMemoryTest {
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	private static Callable<Object> NOP = new Callable<Object>() {
		@Override
		public Object call() throws Exception {
			return null;
		}
	};

	private static Exception EX = new Exception();

	@Test
	public void testFutureTaskMem() throws Exception {
		super.testMean(new Callable<FutureTask<?>>() {
			@Override
			public FutureTask<?> call() throws Exception {
				return new FutureTask2<Object>(NOP);
			}
			
			public String toString() {
				return "FutureTask(Callable)";
			}
		}, 10);
	}
	
	@Test
	public void testLockFutureMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				return new LockMessageFuture<Object>();
			}
			
			public String toString() {
				return "LockMessageFuture";
			}
		}, 10);
	}

	@Test
	public void testSettableFutureMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				return new SettableFuture<Object>();
			}
			
			public String toString() {
				return "SettableFuture";
			}
		}, 10);
	}

	@Test
	public void testMessageTaskMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				return new MessageTask<Object>();
			}
			
			public String toString() {
				return "MessageTask";
			}
		}, 10);
	}

	@Test
	public void testSyncFutureMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				return new UnsafeFuture<Object>();
			}
			
			public String toString() {
				return "UnsafeFuture";
			}
		}, 10);
	}

	@Test
	public void testSetFutureTaskMem() throws Exception {
		super.testMean(new Callable<FutureTask<?>>() {
			@Override
			public FutureTask<?> call() throws Exception {
				FutureTask2<Object> t = new FutureTask2<Object>(NOP);
				t.set(NOP);
				return t;
			}
			
			public String toString() {
				return "set FutureTask(Callable)";
			}
		}, 10);
	}

	@Test
	public void testSetLockFutureMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				LockMessageFuture<Object> t = new LockMessageFuture<Object>();
				t.offer(NOP);
				return t;
			}
			
			public String toString() {
				return "set LockMessageFuture";
			}
		}, 10);
	}

	@Test
	public void testSetSettableFutureMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				SettableFuture<Object> t = new SettableFuture<Object>();
				t.set(NOP);
				return t;
			}
			
			public String toString() {
				return "set SettableFuture";
			}
		}, 10);
	}

	@Test
	public void testSetMessageTaskMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				MessageTask<Object> t = new MessageTask<Object>();
				t.offer(NOP);
				return t;
			}
			
			public String toString() {
				return "set MessageTask";
			}
		}, 10);
	}

	@Test
	public void testSetSyncFutureMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				UnsafeFuture<Object> t = new UnsafeFuture<Object>();
				t.offer(NOP);
				return t;
			}
			
			public String toString() {
				return "set UnsafeFuture";
			}
		}, 10);
	}

	@Test
	public void testFailFutureTaskMem() throws Exception {
		super.testMean(new Callable<FutureTask<?>>() {
			@Override
			public FutureTask<?> call() throws Exception {
				FutureTask2<Object> t = new FutureTask2<Object>(NOP);
				t.setException(EX);
				return t;
			}
			
			public String toString() {
				return "failed FutureTask(Callable)";
			}
		}, 10);
	}
	
	@Test
	public void testFailedLockFutureMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				LockMessageFuture<Object> t = new LockMessageFuture<Object>();
				t.offerException(EX);
				return t;
			}
			
			public String toString() {
				return "failed LockMessageFuture";
			}
		}, 10);
	}
	
	@Test
	public void testFailedSettableFutureMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				SettableFuture<Object> t = new SettableFuture<Object>();
				t.setException(EX);
				return t;
			}
			
			public String toString() {
				return "failed SettableFuture";
			}
		}, 10);
	}
	
	@Test
	public void testFailedMessageTaskMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				MessageTask<Object> t = new MessageTask<Object>();
				t.offerException(EX);
				return t;
			}
			
			public String toString() {
				return "failed MessageTask";
			}
		}, 10);
	}
	
	@Test
	public void testFailedSyncFutureMem() throws Exception {
		super.testMean(new Callable<Future<?>>() {
			@Override
			public Future<?> call() throws Exception {
				UnsafeFuture<Object> t = new UnsafeFuture<Object>();
				t.offerException(EX);
				return t;
			}
			
			public String toString() {
				return "failed UnsafeFuture";
			}
		}, 10);
	}
}
