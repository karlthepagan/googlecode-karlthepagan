Code snippets for several projects in various stages of release readiness.

MessageFuture: Allow external thread to offer completion results to Future objects. This allows non-blocking libraries to interface with the Future API without wasting a thread (as in Jersey's first HTTP client). This also gives programmers comfortable with procedural code a moderately efficient API with which to access asynchronous resources.

The MessageFuture library's goals / use-cases are:
  1. integration with message-passing APIs
  1. provide an API to return control to the user at the earliest completion during execution of arbitrary set of message responses (i.e. CompletionService / completion Queue API)
  1. allow mixing in callbacks / interceptors which safely modify the tasks being executed based on user preference (allow any thread to modify the user's set of tasks to wait for, for example user is waiting for all messages of type "client disconnect" and user may provide a callback class which filters the results efficiently, allowing separation of concerns between the filtering and the completion handling logic)
  1. allow fine-grained task cancelation by outside callers without a Future handle (i.e. by abstracting requests/responses into request id's)
  1. provide building-blocks to aggregate futures into a single-user-thread record updating mechanism



Abandoned projects:

RewardOptimizer: World of Warcraft quest reward optimizer. Depends on wowhead.com for selecting objectives, wowarmory.com for item details.