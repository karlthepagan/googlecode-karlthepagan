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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LockMessageFutureTest extends MessageFutureTests {
	LockMessageFuture<Object> future; // ? conflicts with offer (correctly!)
	
	@Before
	public void setUp() throws Exception {
		future = new LockMessageFuture<Object>();
	}

	@After
	public void tearDown() throws Exception {
		future = null;
	}

	@Test
	public void testCancel() throws Exception {
		boolean returnn = future.cancel(false);
		assertTrue("1= cancel returns true on success", returnn);
		
		testCanceled(future);
	}
	
	@Test
	public void testOfferException() throws Exception {
		final String EXCEPTION_TEXT = "TEST EXCEPTION TEXT!";
		Exception EXCEPTION = new Exception(EXCEPTION_TEXT);
		boolean returnn = future.offerException(EXCEPTION);
		
		assertTrue("1= offerException returns true on success", returnn);
		
		testFailedMessage(future, EXCEPTION, EXCEPTION_TEXT);
	}
	
	@Test
	public void testOffer() throws Exception {
		final String DATA_STRING = "TEST DATA STRING!";
		boolean returnn = future.offer(DATA_STRING);
		
		assertTrue("1= offer returns true on success", returnn);
		
		testCompletedMessage(future,DATA_STRING);
	}
	
	@Test
	public void testNoOffer() throws Exception {
		testBefore(future);
	}
}
