package sjq

import scala.collection.mutable.{ Queue, HashMap }

class Uniqueue[A] {
  val queue = new Queue[Int]()
  val jobs = new HashMap[Int, A]()

  def enqueue(item: A): Int= {
    val id = hash(item) //TODO: hmm this may need to be rethought
    if (!jobs.contains(id)) {
      queue.enqueue(id)
      jobs.put(id, item)
    }
    id
  }

  def dequeue: Option[A] = {
    if (jobs.isEmpty || queue.size == 0) {
      None
    } else {
      jobs.remove(queue.dequeue)
    }
  }

  def isEmpty: Boolean = {
    jobs.isEmpty
  }

  def contains(item: A): Boolean = {
    containsItemWithId(hash(item))
  }

  def containsItemWithId(key: Int): Boolean ={
    jobs.contains(key)
  }
  def size: Int = {
    jobs.size
  }
  
  private def hash(s: A): Int={   //hmm geneicness may need to be rethought since we depend on toString
                                  //maybe hash generator shoudl be injected?
    MurmurHash2.hash32(s.toString)
  }
}
