package sjq

import org.specs._

object InMemoryQueueSpec extends Specification {

  var queue = new InMemoryQueue[String]()

  "In memory queue " should {

    doBefore {
      queue = new InMemoryQueue[String]()
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
      queue.get() must beSome("Ho")
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

      queue.get() must beSome("1")
      queue.get() must beSome("2")
      queue.get() must beSome("3")
    }
  }
}
