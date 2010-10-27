package sjq

import scala.actors.Actor
import scala.actors.Actor._
import dispatch._
import Http._

import java.net.URL

object CallbackActor extends Actor {
  def act = {
    loop {
      react {
        case urls: List[URL] => 
          urls.asInstanceOf[List[URL]] map { x => scala.actors.Futures.future { doGetURL(x) } } foreach { _() }
        case _ => throw new IllegalArgumentException("Can only handle list of URLs to hit currently.")
      }
    }
  }
  private def doGetURL(u: URL): Unit = {
    val http = new Http
    http(u.toString >~ { _.getLines})
  }
}
