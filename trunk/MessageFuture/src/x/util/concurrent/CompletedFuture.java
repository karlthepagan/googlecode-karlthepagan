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

/**
 *
 */
public class CompletedFuture<T> implements Future<T> {
	private final T _val;

	public CompletedFuture(T val) {
		_val = val;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		// already completed - fail
		return false;
	}

	public boolean isCancelled() {
		// completed - not canceled
		return false;
	}

	public boolean isDone() {
		// done!
		return true;
	}

	public T get() throws InterruptedException, ExecutionException {
		return _val;
	}

	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return _val;
	}
}