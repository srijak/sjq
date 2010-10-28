package sjq

import java.net.URL
import scala.collection.mutable.HashSet

class InMemoryQueue[A <: Job] {
  private val availableJobs = new Uniqueue[A]()
  private val stats = new QueryStats()

  def put(implicit item: A): Int = {
    stats.totalMessages.incrementAndGet()
    stats.availableMessages.incrementAndGet()
    availableJobs.synchronized {
      availableJobs.put(item)
    }
  }

  def get: Option[A] = {
    availableJobs.synchronized {
      if (availableJobs.isEmpty) {
        None // block instead?
      } else {
        stats.availableMessages.decrementAndGet()
        stats.reservedMessages.incrementAndGet()
        availableJobs.reserve
      }
    }
  }
  def done(id: Int): List[URL] ={
    stats.reservedMessages.decrementAndGet()
    availableJobs.synchronized{
      val urls = availableJobs.done(id)
      urls match {
        case Some(x) => x
        case _ => List[URL]()
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
  def getStats: QueryStats={
    this.stats
  }
}
