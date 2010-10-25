package sjq

import org.scalatest.junit.JUnitSuite
import junit.framework.Assert._
import org.junit.Test
import org.junit.Before

class UniqueueTest extends JUnitSuite {
  var q: Uniqueue[String] = _  

  @Before
  def setup(){
    q = new Uniqueue[String]()
  }

  @Test
  def enqueue_multiple_of_same_item_adds_only_one() { 
    q.enqueue("A")
    q.enqueue("A")
    assertEquals(1, q.size)
  }

  @Test
  def enqueue_multiple_of_same_item_returns_same_id() { 
    val id0 = q.enqueue("A")
    val id1 =  q.enqueue("A")
    assertEquals(id0, id1)
  }
  
  @Test
  def dequeue_empty_list_returns_empty_Option(){
    assertFalse(q.dequeue.isDefined)
  }

  @Test
  def enqueue_dequeue_means_queue_is_empty(){
    q.enqueue("A")
    assertEquals(1, q.size)
    q.dequeue
    assertEquals(0, q.size)
    assertTrue(q.isEmpty)
  }

  @Test
  def enqueue_means_queue_now_contains_item(){
    q.enqueue("B")
    assertTrue(q.contains("B"))
  }

  @Test
  def size_works(){
    q.enqueue("A")
    q.enqueue("B")
    q.enqueue("C")
    assertEquals(3, q.size)
  }
}
