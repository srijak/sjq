package sjq

import java.util.concurrent.atomic.AtomicLong

class QueryStats{
  val totalMessages = new AtomicLong(0)
  var availableMessages = new AtomicLong(0)
  var reservedMessages = new AtomicLong(0)

  override def toString : String ={
    "Status" + "\n" + 
    "Total Messages Processed: " + totalMessages.get() + "\n" +
    "Available Messages: " + availableMessages.get() + "\n" +
    "Reserved Messages: " + reservedMessages.get() + "\n"
  }
}
