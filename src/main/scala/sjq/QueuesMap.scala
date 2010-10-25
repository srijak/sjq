package sjq

import scala.collection.mutable.HashMap

object QueuesMap {

  val queues = new HashMap[String, InMemoryQueue[String]]()  

  def get(qName: String): InMemoryQueue[String] = {
    queues.synchronized {
      if (!queues.contains(qName)) {
        val q = new InMemoryQueue[String]()
        queues.put(qName, q)
      }
    }
    queues.get(qName).get
  }
  def remove(qName: String): Unit ={
    queues.synchronized {
      queues.remove(qName)
    }
  }
}
