package sjq.Codec

import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.{ IdleStatus, IoSession }
import org.apache.mina.filter.codec._
import net.lag.naggati._
import net.lag.naggati.Steps._

abstract class Request
case class PUT(q: String, data: String) extends Request
case class GET(q: String) extends Request

case class Response(data: IoBuffer)

object SjqCodec {
  def EOM = "\r\n.\r\n"
  def EOM_Bytes = EOM.getBytes("UTF-8")

  val encoder = new ProtocolEncoder {
    def encode(session: IoSession, message: AnyRef, out: ProtocolEncoderOutput) = {
      val buffer = message.asInstanceOf[Response].data
      out.write(buffer)
      out.write(EOM)
    }

    def dispose(session: IoSession): Unit = {}
  }

  val decoder = new Decoder(readDelimiterBuffer(EOM_Bytes) { buffer =>
    def assertValidQueueName(qName: String) = {
      if (!qName.matches("[A-Za-z0-9_]{2,30}")) {
        throw new ProtocolError("Malformed queue name. Must match: [A-Za-z0-9_]{2,30}")
      }
    }

    val msg = new String(buffer, 0, buffer.length - EOM_Bytes.length)
      .split("\n", 2)
    val cmd = msg(0).split(' ').toList.map(e => e.trim)

    cmd match {
      case "PUT" :: q :: Nil => assertValidQueueName(q); state.out.write(PUT(q, msg(1))); End
      case "GET" :: q :: Nil => assertValidQueueName(q); state.out.write(GET(q)); End
      case _ => throw new ProtocolError("Malformed request:" + cmd.toString)
    }
  })
}
