# Introduction #

Allow external thread to offer completion results to Future objects. This allows non-blocking libraries to interface with the Future API without wasting a thread (as in Jersey's first HTTP client). This also gives programmers comfortable with procedural code a moderately efficient API with which to access asynchronous resources.

# Details #

**goals / use-cases**:

  1. integration with message-passing APIs
  1. provide an API to return control to the user at the earliest completion during execution of arbitrary set of message responses (i.e. CompletionService? / completion Queue API)
  1. allow mixing in callbacks / interceptors which safely modify the tasks being executed based on user preference (allow any thread to modify the user's set of tasks to wait for, for example user is waiting for all messages of type "client disconnect" and user may provide a callback class which filters the results efficiently, allowing separation of concerns between the filtering and the completion handling logic)
  1. allow fine-grained task cancelation by outside callers without a Future handle (i.e. by abstracting requests/responses into request id's)
  1. provide building-blocks to aggregate futures into a single-user-thread record updating mechanism
  1. provide an interface to progress callbacks similar to or directly compatible with SwingWorker

# Related and Alternative Works #

  * FutureTask has set() and setException() methods which are suitable for a simple implementation. If you need to allow multiple handlers to produce a result (distributed computing with duplicate jobs) then MessageFuture.offer() will determine which one was submitted without extra encapsulation.
  * [Atlassian labs SettableFuture](http://labs.atlassian.com/source/browse/CONCURRENT/trunk/src/main/java/com/atlassian/util/concurrent/SettableFuture.java) - very simple implementation using AttomicReference and CountdownLatch.
  * [JDK7 NIO.2](https://jdk7.dev.java.net/) - has changed IoFuture into the baseline Future API, but uses sun.nio.ch.AbstractFuture as the implementation which has attachment capability and connects to an AsynchronousChannel.
  * [jboss.org xnio.IoFuture](http://docs.jboss.org/xnio/latest/api/index.html?org/jboss/xnio/IoFuture.html) - provides an io-focused alternative to Future
  * [Limewire.org ListeningFuture](https://www.limewire.org/fisheye/browse/limecvs/components/common/src/main/java/org/limewire/concurrent) - provides completion callbacks for Futures
  * [DiRMI Completion](https://dirmi.dev.java.net/nonav/javadoc/org/cojen/dirmi/Completion.html) - allows completion queue registration on individual Futures

# Discussions #
  * concurrency-interest threads on the subject of Executor & Future design: [1](http://cs.oswego.edu/pipermail/concurrency-interest/2009-April/005971.html) [2](http://cs.oswego.edu/pipermail/concurrency-interest/2007-December/004693.html) [3](http://cs.oswego.edu/pipermail/concurrency-interest/2009-May/006117.html)