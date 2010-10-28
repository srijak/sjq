package sjq

import scala.collection.mutable.HashMap

object QueuesMap {

  val queues = new HashMap[String, InMemoryQueue[Job]]()

  def get(qName: String): InMemoryQueue[Job] = {
    queues.synchronized {
      if (!queues.contains(qName)) {
        queues.put(qName, new InMemoryQueue[Job]())
      }
    }
    queues.get(qName).get
  }
  def remove(qName: String): Unit = {
    queues.synchronized {
      queues.remove(qName)
    }
  }
  def clear: Unit ={
    queues.synchronized{
      queues.clear()
    }
  }
  def getQueuesList: List[String] = {
    queues.keys.toList
  }
}
