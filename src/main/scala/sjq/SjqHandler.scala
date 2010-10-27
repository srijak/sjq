package sjq

import net.lag.naggati.{ IoHandlerActorAdapter, MinaMessage, ProtocolError }
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.{ IdleStatus, IoSession }
import java.io.IOException
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.{ immutable, mutable }
import Codec.{ Request, PUT, GET, Response, SjqCodec, PutOptions, GetOptions }
import java.net.URL

class SjqHandler(val session: IoSession) extends Actor {
  val default_ttr_in_seconds = 300
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
      case PUT(opts, data) =>
        sendReply(put(data, opts).toString)
      case GET(opts) =>
        val item = get(opts)
        if (item.isDefined) {
          sendJob(item.get)
        } else {
          sendReply("")
        }
      case _ =>
        sendReply("Unknown request")
    }
  }
  private def sendReply(s: String) = {
    session.write(new Response(IoBuffer.wrap((s + SjqCodec.EOM).getBytes)))
  }
  private def sendJob(j: Job) = {
    sendReply(j.id + " " + j.ttr_in_seconds + "\n" + j.data)
  }

  private def sendOk = {
    reply("OK" + SjqCodec.EOM)
  }
  private def put(s: String, opts: PutOptions) = {
    if (opts.ttr_in_seconds.isDefined){
      getQueue(opts.q).put(s, opts.ttr_in_seconds.get)
    }else{
      getQueue(opts.q).put(s, default_ttr_in_seconds)
    }
  }
  private def get(opts: GetOptions): Option[Job] = {
    getQueue(opts.q).get
  }
  private def getQueue(qName: String): InMemoryQueue[Job] = {
    return QueuesMap.get(qName)
  }

}

