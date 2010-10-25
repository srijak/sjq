package sjq.Codec

import java.nio.ByteOrder
import net.lag.naggati._
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.filterchain.IoFilter
import org.apache.mina.core.session.{ DummySession, IoSession }
import org.apache.mina.filter.codec._
import org.specs._
import scala.collection.{ immutable, mutable }

object SjqCodecDecoder_Spec extends Specification {

  private var written = new mutable.ListBuffer[Request]
  private var fakeSession: IoSession = new DummySession
  private var fakeDecoderOutput: ProtocolDecoderOutput = new ProtocolDecoderOutput {
    override def flush(nextFilter: IoFilter.NextFilter, s: IoSession) = {}
    override def write(obj: AnyRef) = {
      written += obj.asInstanceOf[Request]
    }
  }

  def quickDecode(s: String): Unit = {
    SjqCodec.decoder.decode(fakeSession, IoBuffer.wrap((s + SjqCodec.EOM).getBytes("UTF-8")), fakeDecoderOutput)
  }

  "SjqCodec Decoder" should {
    doBefore {
      written.clear()
    }

    "parse PUT" in {
      quickDecode("PUT qname\n{\"key\": \"value\"}")
      written(0) must haveClass[PUT]
      written(0).asInstanceOf[PUT].data mustEqual "{\"key\": \"value\"}"
      written(0).asInstanceOf[PUT].q mustEqual "qname"
    }

    "parse PUT, no queue name throws ProtocolError" in {
      quickDecode("PUT \n{\"key\": \"value\"}") must throwA[ProtocolError]
    }

    "parse PUT, queue name length less than 2 throws ProtocolError" in {
      quickDecode("PUT q\n{\"key\": \"value\"}") must throwA[ProtocolError]
    }

    "parse PUT, queue name length greater than 30 throws ProtocolError" in {
      quickDecode("PUT queue_name_that_is_longer_than_30_characters\n{\"key\": \"value\"}") must throwA[ProtocolError]
    }

    "parse GET" in {
      quickDecode("GET qname")
      written.size mustEqual 1
      written(0) must haveClass[GET]
      written(0).asInstanceOf[GET].q mustEqual "qname"
    }

    "parse GET, no queue name throws ProtocolError" in {
      quickDecode("GET") must throwA[ProtocolError]
    }

    "parse GET, queue name length less than 2 throws ProtocolError" in {
      quickDecode("GET q") must throwA[ProtocolError]
    }

    "parse GET, queue name length greater than 30 throws ProtocolError" in {
      quickDecode("GET queue_name_that_is_longer_than_30_characters") must throwA[ProtocolError]
    }

    "parse invalid command" in {
      quickDecode("KAPUT\n") must throwA[ProtocolError]
    }

    "parse CPUT" in {
      quickDecode("CPUT qname http://google.com\nblah blah blah")
      written.size mustEqual 1
      written(0) must haveClass[CPUT]
      written(0).asInstanceOf[CPUT].q mustEqual "qname"
      written(0).asInstanceOf[CPUT].callback_url.getHost mustEqual "google.com"
    }

    "parse CPUT, invalid URL throws ProtocolError" in {
      quickDecode("CPUT qname google.com\n blah blah blah") must throwA[ProtocolError]
    }

    "parse CPUT, empty URL throws ProtocolError" in {
      quickDecode("CPUT qname  \n blah blah blah") must throwA[ProtocolError]
    }

  }
}
