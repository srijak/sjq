package sjq

import net.lag.naggati.{ IoHandlerActorAdapter, MinaMessage, ProtocolError }
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.{ IdleStatus, IoSession }
import java.io.IOException
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.{ immutable, mutable }
import Codec.{ Request, PUT, GET, Response, SjqCodec }

class SjqHandler(val session: IoSession) extends Actor {
  start

  def act = {
    loop {
      react {
        case MinaMessage.MessageReceived(msg) =>
          handle(msg.asInstanceOf[Request])
        case MinaMessage.SessionClosed => exit()
        case MinaMessage.SessionIdle(status) => session.close(true)
        case MinaMessage.ExceptionCaught(cause) => {
          cause.getCause match {
            case e: ProtocolError => reply("ProtocolError: " + e.getMessage + SjqCodec.EOM)
            case i: IOException => reply("IOException: " + i.getMessage + SjqCodec.EOM)
            case _ => reply("Error: " + cause.toString + SjqCodec.EOM)
          }
          session.close(true)
        }
      }
    }
  }
  private def handle(request: Request) = {
    request match {
      case PUT(q, data) =>
        put(q, data)
        sendOk
      case GET(q) =>
        reply(get(q).getOrElse("") + SjqCodec.EOM)
      case _ =>
        reply("Unknown request" + SjqCodec.EOM)
    }
  }
  private def sendOk = {
    reply("OK" + SjqCodec.EOM)
  }
  private def put(q: String, s: String) = {
    getQueue(q).put(s)
  }
  private def get(q: String): Option[String] = {
    getQueue(q).get
  }
  private def reply(s: String) = {
    session.write(new Response(IoBuffer.wrap(s.getBytes)))
  }
  private def getQueue(qName:String):InMemoryQueue[String] ={ 
    return QueuesMap.get(qName)
  }
}



