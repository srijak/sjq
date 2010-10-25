package sjq

import scala.collection.mutable.{ Queue, HashSet }

class Uniqueue[A] {
  val queue = new Queue[A]()
  val set = new HashSet[A]()

  def enqueue(item: A): Unit = {
    if (!set.contains(item)) {
      queue.enqueue(item)
      set.add(item)
    }
  }

  def dequeue: Option[A] = {
    if (set.isEmpty || queue.size == 0){
      None
    }else{
      val item = queue.dequeue()
      set.remove(item)
      Some[A](item)
    }
  }

  def isEmpty: Boolean = {
    return set.isEmpty
 }

  def contains(item: A): Boolean = {
    set.contains(item)
  }

  def size: Int = {
    set.size
  }
}
