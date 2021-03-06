package sjq

import net.lag.naggati.{ IoHandlerActorAdapter, MinaMessage, ProtocolError }
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.{ IdleStatus, IoSession }
import java.io.IOException
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.{ immutable, mutable }
import Codec._
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
      case TOUCH(opts) =>
        touch(opts)
        sendOk
      case STATUS(opts) =>
        sendReply(status(opts).toString)
      case QLIST() =>
        sendReply(qlist)
      case HELP(opts) =>
        opts match {
          case Nil => sendReply(help(List[String]("PUT", "GET", "DONE", "TOUCH", "QLIST", "STATUS", "HELP") ))
          case _ => sendReply(help(opts))
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
    sendReply("OK")
  }

  private def put(s: String, opts: PutOptions) = {
    val callback_urls = Some(opts.callback_urls.toList)

    getQueue(opts.q).put(opts.ttr_in_seconds match {
      case Some(x) => createJob(s, x, callback_urls)
      case _ => createJob(s, default_ttr_in_seconds, callback_urls)
    })
  }

  private def createJob(data: String, ttr: Int, callback_urls: Option[List[URL]]): Job = {
    new Job(data, ttr, callback_urls)
  }
  private def get(opts: GetOptions): Option[Job] = {
    getQueue(opts.q).get
  }
  private def touch(opts: TouchOptions): Unit = {
    //TODO
  }
  private def help(opts: List[String]):String ={
    opts match {
      case Nil => "\n"
      case a::rest =>
        val current = a match {
          case "QLIST" => "\nQLIST\n\tlist all queues on server"
          case "STATUS" => "\nSTATUS <qname>\n\tShow stats on the given queue"
          case "PUT" => "\nPUT <qname> [ttr] [callback_urls]\n<DATA>\n\t add <DATA> to queue with optional ttr and callback urls"
          case "GET" => "\nGET <qname>\n\t Get item from the given queue"
          case "DONE" => "\nDONE <qname> <id>\n\t Mark item as done"
          case "TOUCH" => "\nTOUCH <qname> <id> <additional_time_seconds>\n\tRequest additional processing time"
          case "HELP" => "\nHELP [commands]\n\t Get info on the specified commands"
        }
      current + "\n"+ help(rest)
        
    }
  }
  private def done(opts: DoneOptions): Unit = {
    CallbackActor ! getQueue(opts.q).done(opts.id)
  }
  private def status(opts: StatusOptions): QueryStats = {
    QueuesMap.get(opts.q).getStats
  }
  private def qlist: String = {
    val qs = QueuesMap.getQueuesList
    if (qs.size > 0) {
      qs reduceLeft (_ + "\n" + _)
    } else {
      ""
    }
  }
  private def getQueue(qName: String): InMemoryQueue[Job] = {
    return QueuesMap.get(qName)
  }
}

