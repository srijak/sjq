package sjq

import org.scalatest.junit.JUnitSuite
import junit.framework.Assert._
import org.junit.Test

class QueuesMapTest extends JUnitSuite {

  @Test
  def test_get_nonexistant_queue_returns_new_queue() { 
    assertNotNull(QueuesMap.get("qn"))
  }
  @Test
  def test_get_queue_by_name_returns_same_queue(){
    val q = QueuesMap.get("qn")
    assertEquals(q, QueuesMap.get("qn"))
  }
  @Test
  def test_get_different_queues_by_name_returns_different_queues(){
    val q0 = QueuesMap.get("q0")
    val q1 = QueuesMap.get("q1")
    assertNotSame(q0, q1)
  }
  @Test
  def test_can_get_list_of_current_queues(){
    QueuesMap.clear
    assertEquals(0, QueuesMap.getQueuesList.size)
    QueuesMap.get("q0")
    assertEquals(1, QueuesMap.getQueuesList.size)
    QueuesMap.remove("q0")
    assertEquals(0, QueuesMap.getQueuesList.size)
  }
}
