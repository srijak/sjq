package sjq.Codec

import java.net.{ URL, MalformedURLException }
import net.lag.naggati._
import net.lag.naggati.Steps._

import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.filter.codec._

import scala.collection.mutable.HashSet
abstract class Request
case class PUT(opts: PutOptions, data: String) extends Request
case class GET(opts: GetOptions) extends Request
case class DONE(opts: DoneOptions) extends Request
case class TOUCH(opts: TouchOptions) extends Request
case class STATUS(opts: StatusOptions) extends Request
case class QLIST() extends Request

case class Response(data: IoBuffer)

trait QueueRequired {
  val opts: List[String]
  val q: String = { assertValidQueueName(opts.head); opts.head }
  require(opts.length > 0)

  def assertValidQueueName(qName: String) = {
    if (!qName.matches("[A-Za-z0-9_]{2,30}")) {
      throw new ProtocolError("Malformed queue name. Must match: [A-Za-z0-9_]{2,30}")
    }
  }
}
trait HasCallbackUrls {
  val opts: List[String]
  val callback_urls = HashSet() ++ opts.map(s => try { new URL(s) } catch { case _ => None }) //better way to do this? may be slow.
    .filter(_ != None)
    .asInstanceOf[List[URL]]
}
trait HasTTR {
  val opts: List[String]
  val ttr_in_seconds: Option[Int] = try {
    opts match {
      case a :: x :: _ => Some(x.toInt)
      case _ => None
    }
  } catch {
    case _ => None
  }
}

abstract class CommandOptions(val opts: List[String]) {}

class PutOptions(opts: List[String]) extends CommandOptions(opts) with QueueRequired with HasTTR with HasCallbackUrls {}

class GetOptions(opts: List[String]) extends CommandOptions(opts) with QueueRequired {}

class DoneOptions(opts: List[String]) extends CommandOptions(opts) with QueueRequired {
  val id: Int = try {
    opts.tail.head.toInt
  } catch {
    case _ => throw new ProtocolError("Need id of job to mark as done")
  }
}
class TouchOptions(opts: List[String]) extends DoneOptions(opts) {
  val additional_time_in_seconds: Int = try {
    List(opts.tail.tail.head.toInt, 2).max
  } catch {
    case _ => throw new ProtocolError("Needs additional time requested(in seconds)")
  }
}
class StatusOptions(opts: List[String]) extends CommandOptions(opts) with QueueRequired{}
