package sjq

import net.lag.naggati.{ IoHandlerActorAdapter, MinaMessage, ProtocolError }
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.{ IdleStatus, IoSession }
import java.io.IOException
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.{ immutable, mutable }
import Codec.{ Request, PUT, GET, DONE, Response, SjqCodec, PutOptions, GetOptions, DoneOptions }
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
        item match {
          case Some(j) => sendJob(j)
          case _ => sendReply("") //block?
        }
      case DONE(opts) =>
        done(opts)
        sendOk
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
    sendReply("OK")
  }

  private def put(s: String, opts: PutOptions) ={
    val callback_urls = Some(opts.callback_urls.toList)    
    
    getQueue(opts.q).put(opts.ttr_in_seconds match {
      case Some(x) => createJob(s, x, callback_urls)
      case _ => createJob(s, default_ttr_in_seconds, callback_urls)
    })
  }

  private def createJob(data:String, ttr:Int, callback_urls: Option[List[URL]]): Job ={
    new Job(data, ttr, callback_urls)
  }
  private def get(opts: GetOptions): Option[Job] = {
    getQueue(opts.q).get
  }

  private def done(opts: DoneOptions): Unit ={
    CallbackActor ! getQueue(opts.q).done(opts.id)
  }
  private def getQueue(qName: String): InMemoryQueue[Job] = {
    return QueuesMap.get(qName)
  }

}

