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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MessageFuture implemented with a backing Lock.
 * 
 * @see MessageFuture
 * @see Lock
 */
public class LockMessageFuture<V> implements MessageFuture<V> {
	private static final Object CANCELED = new Object();
	private static final Object NULL = new Object();
	
	/**
	 * Has a value of literal null when no result has been set yet.
	 */
	private volatile Object _result; // 4-8
	private final Lock _itemLock; // 4-8
	private final Condition _notEmpty; // 4-8
	// total: 20-40 bytes (object: 2 pointers)
	
	/**
	 * Construct a new LockMessageFuture with an unfair ReentantLock.
	 */
	public LockMessageFuture() {
		this(new ReentrantLock());
	}

	/**
	 * Construct a LockMessageFuture with the specified lock.
	 * 
	 * @param lock
	 */
	public LockMessageFuture(Lock lock) {
		_itemLock = lock;
		_notEmpty = lock.newCondition();
		_result = null;
	}
	
	public boolean cancel(boolean mayInterruptIfRunning) {
		Object o = _result;
		if (o != null)
			return o == CANCELED;
		
		_itemLock.lock();
		try {
			// double-check, with volatile for visibility
			o = _result;
			if (o != null)
				return o == CANCELED;
			
			// WARNING - this will throw ClassCastException if ever accessed as
			// V
			_result = CANCELED;
			
			onCancel(mayInterruptIfRunning);
			onDone();
			
			_notEmpty.signalAll();
			
			return true;
		} finally {
			_itemLock.unlock();
		}
	}
	
    public boolean offerException(Throwable t) {
    	if (_result != null)
			return false;
    	
		_itemLock.lock();
		try {
			// double-check, with volatile for visibility
			if (_result != null)
				return false;
			
			_result = new ThrowHolder(t);
			
			onFail(t);
			onDone();
			
			_notEmpty.signalAll();
			
			return true;
		} finally {
			_itemLock.unlock();
		}
    }
	
	public boolean offer(V data) {
		if (_result != null)
			return false;
		
		Object o = data;
		
		// _result == null has special meaning, can't assign literal null, use a
		// constant instead
		if (o == null)
			o = NULL;
		
		_itemLock.lock();
		try {
			// double-check, with volatile for visibility
			if (_result != null)
				return false;
			
			_result = o;
			
			onFinish(data);
			onDone();
			
			_notEmpty.signalAll();
			
			return true;
		} finally {
			_itemLock.unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	private V coerce(Object o) throws ExecutionException, CancellationException {
		//assert o != null;
		if (o == CANCELED)
			throw new CancellationException();
		
		// by convention of FutureTask, ExecutionException is generated in the
		// thread/stack from which access to the result is requested
		if (o instanceof ThrowHolder)
			throw new ExecutionException(((ThrowHolder)o)._t);
		
		return o == NULL ? null : (V) o;
	}
	
	public V get() throws InterruptedException, ExecutionException {
		Object o = _result;
		if (o != null)
			return coerce(o);
		
		_itemLock.lockInterruptibly();
		try {
			// double-check, with volatile for visibility
			for (;;) {
				o = _result;
				if (o != null)
					return coerce(o);
				
				// conditions may spuriously wake up!
				_notEmpty.await();
			}
		} finally {
			_itemLock.unlock();
		}
	}

	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		Object o = _result;
		if (o != null)
			return coerce(o);
		
		_itemLock.lockInterruptibly();
		try {
			long nanos = unit.toNanos(timeout);
			
			// double-check, with volatile for visibility
			for (;;) {
				o = _result;
				if (o != null)
					return coerce(o);
				
				if (nanos > 0) {
					nanos = _notEmpty.awaitNanos(nanos);
				} else {
					throw new TimeoutException();
				}
			}
		} finally {
			_itemLock.unlock();
		}
	}

	@Override
	public boolean isCancelled() {
		return _result == CANCELED;
	}
	
	public boolean isFailed() {
		return _result instanceof ThrowHolder;
	}
	
	public boolean isSuccess() {
		Object o = _result;
		return o != null && o != CANCELED && false == o instanceof ThrowHolder;
	}
	
	@Override
	public boolean isDone() {
		return _result != null;
	}
	
	/**
	 * Override onDone() to notify completion queues of completion and that this
	 * future transitioned to the "isDone" state.
	 * 
	 * All blocking method will be uninterruptably locked during execution of
	 * onDone().
	 * 
	 * finished, canceled, and failed are called previous to done
	 * 
	 * @param value
	 *            result value, implementers MUST ensure the thread-safety of
	 *            this value and its use
	 */
	protected void onDone() {}

	/**
	 * Override onFinish() to notify completion queues of completion with result.
	 * 
	 * All blocking method will be uninterruptably locked during execution of
	 * onFinish().
	 * 
	 * @param value
	 *            result value, implementers MUST ensure the thread-safety of
	 *            this value and its use
	 */
    protected void onFinish(V value) { }
    
	/**
	 * Override onCancel() to notify cancellation handlers of canceled requests
	 * @param mayInterruptIfRunning 
	 */
    protected void onCancel(boolean mayInterruptIfRunning) { }

	/**
	 * Override failed() to be notified of requests that result in exceptions.
	 * 
	 * @param t
	 *            the exception offered to this future, the offerer is
	 *            recommended to wrap the exception in a RemoteException
	 *            immediately before offering
	 */
    protected void onFail(Throwable t) { }
    
    /**
     * MAGIC/POISON class used to signify an exceptional result value.
     */
    protected static class ThrowHolder {
    	public Throwable _t;
    	public ThrowHolder(Throwable t) {
    		_t = t;
    	}
    }
}