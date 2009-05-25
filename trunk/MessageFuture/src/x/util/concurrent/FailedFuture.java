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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FailedFuture<V> implements Future<V> {
	private final Throwable _t;
	
	public FailedFuture(Throwable t) {
		_t = t;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// failed, not excepted
		return false;
	}
	
	@Override
	public V get() throws InterruptedException, ExecutionException {
		throw new ExecutionException(_t);
	}
	
	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		throw new ExecutionException(_t);
	}
	
	@Override
	public boolean isCancelled() {
		return false;
	}
	
	@Override
	public boolean isDone() {
		// failed is done
		return true;
	}
}
