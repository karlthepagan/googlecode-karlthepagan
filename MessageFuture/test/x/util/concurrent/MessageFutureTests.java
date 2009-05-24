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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MessageFutureTests {
	final long ONE_SECOND_NANOS = TimeUnit.NANOSECONDS.convert(1,
			TimeUnit.SECONDS);
	protected void testCanceled(Future<Object> future) throws Exception {
		boolean returnn = false;
		
		assertTrue("2= future looks canceled",future.isCancelled());
		
		try {
			future.get();
			fail("3? get throws CancelationException");
		} catch(CancellationException e) {
		}
		
		long t0 = System.nanoTime();
		try {
			future.get(1,TimeUnit.SECONDS);
			fail("4? get(time...) throws CancelationException");
		} catch(CancellationException e) {
		}
		assertTrue("5= get(1s) does not pause",
				System.nanoTime() - t0 < ONE_SECOND_NANOS);
		
		returnn = future.cancel(true);
		assertTrue("6= repeat cancel(true) returns true on success", returnn);
		returnn = future.cancel(false);
		assertTrue("7= repeat cancel(false) returns true on success", returnn);
		
		assertTrue("8= future looks done", future.isDone());
	}
	
	protected void testCanceledMessage(MessageFuture<Object> future) throws Exception {
		testCanceled(future);
		
		long t0 = System.nanoTime(); // TODO interrupt unless canceled timer
		boolean returnn = future.offer("HELLO!");
		assertFalse("9= offer returns false",returnn);
		assertTrue("10= offer does not pause",
				System.nanoTime() - t0 < ONE_SECOND_NANOS);
		
		t0 = System.nanoTime();
		returnn = future.offerException(new Exception(
				"11? offerException doesn't throw"));
		assertFalse("12= offerException returns false",returnn);
		assertTrue("13= offerException does not pause",
				System.nanoTime() - t0 < ONE_SECOND_NANOS);
	}
	
	protected void testFailed(Future<Object> future, Throwable testException, String testMessage) throws Exception {
		boolean returnn = false;
		assertFalse("2= future doesn't look canceled",future.isCancelled());
		assertTrue("3= future looks done",future.isDone());
		
		try {
			future.get();
			fail("4? get throws ExecutionException");
		} catch(ExecutionException e) {
			if(testException != null)
				assertEquals("5? get threw expected exception",testException,e.getCause());
			if(testMessage != null)
				assertEquals("6? get threw expected message",testMessage, e.getCause().getMessage());
		}
		
		long t0 = System.nanoTime();
		try {
			future.get(1,TimeUnit.SECONDS);
			fail("7? get(1s) throws ExecutionException");
		} catch(ExecutionException e) {
			if(testException != null)
				assertEquals("8? get(1s) threw expected exception",testException,e.getCause());
			if(testMessage != null)
				assertEquals("9? get(1s) threw expected message",testMessage, e.getCause().getMessage());
		}
		final long ONE_SECOND_NANOS = TimeUnit.NANOSECONDS.convert(1,
				TimeUnit.SECONDS);
		assertTrue("10= get(1s) does not pause",
				System.nanoTime() - t0 < ONE_SECOND_NANOS);
		
		returnn = future.cancel(true);
		assertFalse("11= cancel(true) returns false", returnn);
		returnn = future.cancel(false);
		assertFalse("12= cancel(false) returns false", returnn);
	}
	
	protected void testFailedMessage(MessageFuture<Object> future, Throwable testException, String testMessage) throws Exception {
		testFailed(future, testException, testMessage);
		
		long t0 = System.nanoTime(); // TODO interrupt unless canceled timer
		boolean returnn = future.offer("HELLO!");
		assertFalse("13= offer returns false",returnn);
		assertTrue("14= offer does not pause",
				System.nanoTime() - t0 < ONE_SECOND_NANOS);
		
		t0 = System.nanoTime();
		returnn = future.offerException(new Exception(
				"15? offerException doesn't throw"));
		assertFalse("16= offerException returns false",returnn);
		assertTrue("17= offerException does not pause",
				System.nanoTime() - t0 < ONE_SECOND_NANOS);
	}
	
	protected void testCompleted(Future<Object> future,
			Object data) throws Exception {
		boolean returnn = false;
		assertFalse("2= future doesn't look canceled",future.isCancelled());
		assertTrue("3= future looks done",future.isDone());
		
		assertEquals("4= get returns expected data", data, future.get());
		
		long t0 = System.nanoTime();
		assertEquals("5= get(1s) returns expected data", data, future.get(1,TimeUnit.SECONDS));
		assertTrue("6= get(1s) does not pause",
				System.nanoTime() - t0 < ONE_SECOND_NANOS);
		
		returnn = future.cancel(true);
		assertFalse("7= cancel(true) returns false", returnn);
		returnn = future.cancel(false);
		assertFalse("8= cancel(false) returns false", returnn);
		
	}
	
	protected void testCompletedMessage(MessageFuture<Object> future,
			Object data) throws Exception {
		testCompleted(future, data);
		
		long t0 = System.nanoTime(); // TODO interrupt unless canceled timer
		boolean returnn = future.offer("HELLO!");
		assertFalse("9= repeat offer returns false",returnn);
		assertTrue("10= repeat offer does not pause",
				System.nanoTime() - t0 < ONE_SECOND_NANOS);
		
		t0 = System.nanoTime();
		returnn = future.offerException(new Exception(
				"11? offerException doesn't throw"));
		assertFalse("12= offerException returns false",returnn);
		assertTrue("13= offerException does not pause",
				System.nanoTime() - t0 < ONE_SECOND_NANOS);
	}
	
	protected void testBefore(Future<Object> future) throws Exception {
		assertFalse("1= future doesn't look canceled",future.isCancelled());
		assertFalse("2= future looks done",future.isDone());
		
		try {
			future.get(0, TimeUnit.SECONDS);
			fail("3? get throws timeout exception");
		} catch(TimeoutException e) {
		}
	}
}
