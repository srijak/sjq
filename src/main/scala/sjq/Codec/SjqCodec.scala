package sjq.Codec

import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.{ IdleStatus, IoSession }
import org.apache.mina.filter.codec._
import net.lag.naggati._
import net.lag.naggati.Steps._
import java.net.{ URL, MalformedURLException }

object SjqCodec {
  def EOM = "\r\n.\r\n"
  def EOM_Bytes = EOM.getBytes("UTF-8")

  val encoder = new ProtocolEncoder {
    def encode(session: IoSession, message: AnyRef, out: ProtocolEncoderOutput) = {
      val buffer = message.asInstanceOf[Response].data
      out.write(buffer)
    }

    def dispose(session: IoSession): Unit = {}
  }

  val decoder = new Decoder(readDelimiterBuffer(EOM_Bytes) { buffer =>
    def protocolError = throw new ProtocolError("Malformed Request")

    val msg = new String(buffer, 0, buffer.length - EOM_Bytes.length)
      .split("\n", 2)
    val cmd = msg(0).split(' ').toList.map(e => e.trim)
    cmd match {
      case Nil => protocolError
      case _ :: Nil => protocolError
      case "PUT" :: rest =>
        state.out.write(PUT(new PutOptions(rest), msg(1))); End
      case "GET" :: rest =>
        state.out.write(GET(new GetOptions(rest))); End
      case "DONE" :: rest =>
        state.out.write(DONE(new DoneOptions(rest))); End
      case "TOUCH" :: rest =>
        state.out.write(TOUCH(new TouchOptions(rest))); End
      case _ => protocolError
    }
  })
}

