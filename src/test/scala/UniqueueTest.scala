package sjq

import org.scalatest.junit.JUnitSuite
import junit.framework.Assert._
import org.junit.Test
import org.junit.Before
import java.net.URL

class UniqueueTest extends JUnitSuite {
  var q: Uniqueue[Job] = _  

  @Before
  def setup(){
    q = new Uniqueue[Job]()
  }
  
  @Test
  def enqueue_multiple_of_same_item_adds_only_one() { 
    q.put(getJob("A"))
    q.put(getJob("A"))
    assertEquals(1, q.size)
  }

  @Test
  def enqueue_multiple_of_same_item_returns_same_id() { 
    val id0 = q.put(getJob("A"))
    val id1 =  q.put(getJob("A"))
    assertEquals(id0, id1)
  }
  
  @Test
  def dequeue_empty_list_returns_empty_Option(){
    assertFalse(q.reserve.isDefined)
  }

  @Test
  def enqueue_dequeue_means_queue_is_empty(){
    q.put(getJob("A"))
    assertEquals(1, q.size)
    q.reserve
    assertEquals(0, q.size)
    assertTrue(q.isEmpty)
  }

  @Test
  def enqueue_means_queue_now_contains_item(){
    q.put(getJob("B"))
    assertTrue(q.contains(getJob("B")))
  }
  
  @Test 
  def done_means_queue_doesnt_have_item(){
    q.put(getJob("C"))
    val job = q.reserve.get
    q.done(job.id)
    assertFalse(q.contains(job))
  }

  @Test 
  def done_returns_list_of_callback_urls(){
    val job =getJob("C")
    job.callback_urls = Some(List[URL](new URL("http://google.com"))) //may need to be rethought
    q.put(job)
    assertTrue(q.done(q.reserve.get.id).get.contains(new URL("http://google.com")))
  }
  @Test 
  def done_returns_none_when_no_callbacksset(){
    q.put(getJob("C"))
    assertEquals(q.done(q.reserve.get.id), None)
  }

  @Test
  def size_works(){
    q.put(getJob("A"))
    q.put(getJob("B"))
    q.put(getJob("C"))
    assertEquals(3, q.size)
  }

  private def getJob(s: String): Job ={
    new Job(s, 300)   //TODO
  }
}
