package sjq

import java.net.URL
import scala.collection.mutable.HashSet

class InMemoryQueue[A] {
  var availableJobs = new Uniqueue[A]()

  def put(item: A): Unit = {
    availableJobs.synchronized {
      availableJobs.enqueue(item)
    }
  }

  def get: Option[A] = {
    availableJobs.synchronized {
      if (availableJobs.isEmpty) {
        None // block instead?
      } else {
        availableJobs.dequeue
      }
    }
  }

  def contains(item: A): Boolean = {
    availableJobs.synchronized {
      availableJobs.contains(item)
    }
  }
  def size: Int = {
    availableJobs.synchronized {
      availableJobs.size
    }
  }
}
