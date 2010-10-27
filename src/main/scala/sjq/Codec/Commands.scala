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

case class Response(data: IoBuffer)

abstract class CommandOptions(val opts: List[String]) {

  val q: String = { assertValidQueueName(opts.head); opts.head }
  require(opts.length > 0)

  def assertValidQueueName(qName: String) = {
    if (!qName.matches("[A-Za-z0-9_]{2,30}")) {
      throw new ProtocolError("Malformed queue name. Must match: [A-Za-z0-9_]{2,30}")
    }
  }
}

class PutOptions(opts: List[String]) extends CommandOptions(opts) {
  val callback_urls = HashSet() ++ opts.map(s => try { new URL(s) } catch { case _ => None }) //better way to do this? may be slow.
    .filter(_ != None)
    .asInstanceOf[List[URL]]
  val ttr_in_seconds: Option[Int] = try {
    opts match {
      case a :: x :: _ => Some(x.toInt)
      case _ => None
    }
  } catch {
    case _ => None
  }
}

class GetOptions(opts: List[String]) extends CommandOptions(opts) {
}
