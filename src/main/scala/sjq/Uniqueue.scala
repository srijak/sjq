package sjq

import scala.collection.mutable.{ Queue, HashMap, HashSet }

class Uniqueue[A <: Job] {
  val queue = new Queue[Int]()
  val jobs = new HashMap[Int, A]()
  val reserved_jobs = new HashSet[Int]()

  def put(item: A): Int = {
    if (!jobs.contains(item.id)) {
      queue.enqueue(item.id)
      jobs.put(item.id, item)
    }
    item.id
  }

  def reserve: Option[A] = if (queue.size == 0) {
    None
  } else {
    val itemId = queue.dequeue
    reserved_jobs.add(itemId)
    jobs.get(itemId)
  }

  def done(id: Int): Unit = {
    reserved_jobs.removeEntry(id)
    jobs.remove(id)
  }

  def isEmpty: Boolean = {
    queue.size == 0
  }

  def contains(item: A): Boolean = {
    containsItemWithId(item.id)
  }

  def containsItemWithId(id: Int): Boolean = {
    jobs.contains(id)
  }
  def size: Int = {
    queue.size
  }
}
