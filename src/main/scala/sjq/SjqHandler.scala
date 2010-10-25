package sjq

import net.lag.naggati.{ IoHandlerActorAdapter, MinaMessage, ProtocolError }
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.{ IdleStatus, IoSession }
import java.io.IOException
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.{ immutable, mutable }
import Codec.{ Request, PUT, CPUT, GET, Response, SjqCodec }
import java.net.URL

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
            case e: ProtocolError => sendReply("ProtocolError: " + e.getMessage)
            case i: IOException => sendReply("IOException: " + i.getMessage)
            case _ => sendReply("Error: " + cause.toString)
          }
          session.close(true)
        }
      }
    }
  }
  private def handle(request: Request) = {
    request match {
      case PUT(q, data) =>
        sendReply(put(q, data).toString)
      case GET(q) =>
        sendReply(get(q).getOrElse(""))
      case CPUT(q, callback_url, data) =>
        cput(q, callback_url, data)
        sendReply("NOT_IMPLEMENTED_YET")
      case _ =>
        sendReply("Unknown request")
    }
  }
  private def sendReply(s: String)={
    session.write(new Response(IoBuffer.wrap((s + SjqCodec.EOM).getBytes)))
  }
  private def sendOk = {
    reply("OK" + SjqCodec.EOM)
  }
  private def put(q: String, s: String) ={
    getQueue(q).put(s)
  }
  private def cput(q: String, callback_url: URL, data: String) ={
    //TODO: Implement CPUT
  }
  private def get(q: String): Option[String] = {
    getQueue(q).get
  }
  private def getQueue(qName:String):InMemoryQueue[String] ={ 
    return QueuesMap.get(qName)
  }
}

