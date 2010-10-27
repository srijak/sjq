package sjq

import org.specs._

object InMemoryQueueSpec extends Specification {

  var queue = new InMemoryQueue[Job]()
  
  "In memory queue " should {

    doBefore {
      queue = new InMemoryQueue[Job]()
    }

    "get from empty queue returns  None" in {
      queue.get() must beNone
    }

    "can put item onto queue" in {
      queue.put("Hi")
      queue.contains("Hi") must beTrue
    }

    "can remove item from queue" in {
      queue.put("Ho")
      queue.get().get.data must be("Ho")
    }

    "adding multiple times only contains 1" in {
      queue.put("Hi")
      queue.put("Hi")
      queue.put("Hi")
      queue.size must be_==(1)
    }

    "items are gotten in the same order they were put" in {
      queue.put("1")
      queue.put("2")
      queue.put("3")

      queue.get().get.data must be("1")
      queue.get().get.data must be("2")
      queue.get().get.data must be("3")
    }
  }
}
