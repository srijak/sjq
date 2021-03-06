package sjq

import net.lag.naggati.IoHandlerActorAdapter
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.transport.socket.SocketAcceptor
import org.apache.mina.transport.socket.nio.{ NioProcessor, NioSocketAcceptor }
import java.net.InetSocketAddress
import java.util.concurrent.{ Executors, ExecutorService }
import scala.actors.Actor._
import Codec.SjqCodec
import scala.collection.mutable.HashMap

object sjq {
  val host = "0.0.0.0"
  val port = 5537
  val statusPort = 5538

  def main(args: Array[String]): Unit = {
    setMaxThreads
    initializeQueueAcceptor
    CallbackActor.start()
    println("sjq up and listening on " + host + ":" + port)
  }

  def setMaxThreads = {
    val maxThreads = List(Runtime.getRuntime.availableProcessors * 2, 2).max
    System.setProperty("actors.maxPoolSize", maxThreads.toString)
  }
  
  def initializeQueueAcceptor = {
    var acceptorExecutor = Executors.newCachedThreadPool()
    var acceptor = new NioSocketAcceptor(acceptorExecutor, new NioProcessor(acceptorExecutor))

    acceptor.setBacklog(1000)
    acceptor.setReuseAddress(true)
    acceptor.getSessionConfig.setTcpNoDelay(true)

    acceptor.getFilterChain.addLast("codec",
      new ProtocolCodecFilter(SjqCodec.encoder, SjqCodec.decoder))
    acceptor.setHandler(
      new IoHandlerActorAdapter(session => new SjqHandler(session)))

    acceptor.bind(new InetSocketAddress(host, port))
  }
}
