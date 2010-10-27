package sjq

import java.net.URL
import scala.collection.mutable.HashSet

class InMemoryQueue[A <: Job] {
  var availableJobs = new Uniqueue[A]()

  def put(implicit item: A): Int = {
    availableJobs.synchronized {
      availableJobs.put(item)
    }
  }

  def get: Option[A] = {
    availableJobs.synchronized {
      if (availableJobs.isEmpty) {
        None // block instead?
      } else {
        availableJobs.reserve
      }
    }
  }

  def contains(implicit item: A): Boolean = {
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
