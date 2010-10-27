package sjq

import java.net.URL

class Job(val data: String, val ttr_in_seconds: Int, var callback_urls: Option[List[URL]] = None){
  
  val id = MurmurHash2.hash32(this.data)

  override def hashCode(): Int ={
    return id
  }
  override def equals(o: Any): Boolean ={
    o.isInstanceOf[Job] && (this.data eq o.asInstanceOf[Job].data);
  }
  override def toString : String ={
    return "<id:" + id + "> <callbacks: " + this.callback_urls.getOrElse(List[URL]()) + ">" +  data + "\n"
  }
}

object Job {
  implicit def string_ttr_2job(x:(String,Int)): Job ={
    new Job(x._1, x._2)
  }
  implicit def string2job(s: String): Job ={
    new Job(s, -1)  //negative ttr means no ttr, mostly for testing.
  }
}

