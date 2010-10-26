SJQ: Simple job queue or if you think it isn't simple, Scala job queue

#### Why?
 To learn scala and work through the issues in writing a message queue.
#### Features:
  - Supports unique jobs. So, if you add a job and another one like it already exists in the queue, the duplicate wont get added.  [DONE]
  - Supports doing a get request to a callback url when a job is complete. [TODO]
