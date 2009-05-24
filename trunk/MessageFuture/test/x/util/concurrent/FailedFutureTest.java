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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FailedFutureTest extends MessageFutureTests {
	FailedFuture<Object> future = null;
	Throwable exception = null;
	static final String message = "TEST MESSAGE!";
	
	@Before
	public void setUp() throws Exception {
		exception = new Exception(message);
		future = new FailedFuture<Object>(exception);
	}
	
	@After
	public void tearDown() throws Exception {
		exception = null;
		future = null;
	}
	
	@Test
	public void testFailedFuture() throws Exception {
		testFailed(future, exception, message);
	}
}
