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

import java.util.concurrent.Future;

/**
 * A Future which is implemented as a waiter for a specific message response
 * rather than the execution of a code block.
 */
public interface MessageFuture<V> extends Future<V> {
    /**
     * Method for delivering completion signals and data to clients of this
     * Future.
     * 
     * @return true if and only if the data was accepted
     */
    boolean offer(V data);

	/**
	 * Method for delivering failure completion signals and exception data to
	 * clients of this Future.
	 * 
	 * For remotely delivered exceptions it is highly recommended that the
	 * exception is wrapped in RemoteException immediately before it is offered.
	 * This ensures that the complete execution stack is visible.
	 * 
	 * @return true if and only if the exception was accepted
	 */
    boolean offerException(Throwable t);
}