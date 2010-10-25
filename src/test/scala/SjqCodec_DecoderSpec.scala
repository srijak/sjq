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
    SjqCodec.decoder.decode(fakeSession, IoBuffer.wrap(s.getBytes("UTF-8")), fakeDecoderOutput)
  }

  "SjqCodec Decoder" should {
    doBefore {
      written.clear()
    }

    "parse PUT" in {
      quickDecode("PUT qname\n{\"key\": \"value\"}" + SjqCodec.EOM)
      written(0) must haveClass[PUT]
      written(0).asInstanceOf[PUT].data mustEqual "{\"key\": \"value\"}"
      written(0).asInstanceOf[PUT].q mustEqual "qname"
    }

    "parse PUT, no queue name throws ProtocolError" in {
      quickDecode("PUT \n{\"key\": \"value\"}" + SjqCodec.EOM) must throwA[ProtocolError]
    }

    "parse PUT, queue name length less than 2 throws ProtocolError" in {
      quickDecode("PUT q\n{\"key\": \"value\"}" + SjqCodec.EOM) must throwA[ProtocolError]
    }

    "parse PUT, queue name length greater than 30 throws ProtocolError" in {
      quickDecode("PUT queue_name_that_is_longer_than_30_characters\n{\"key\": \"value\"}" + SjqCodec.EOM) must throwA[ProtocolError]
    }

    "parse GET" in {
      quickDecode("GET qname" + SjqCodec.EOM)
      written.size mustEqual 1
      written(0) must haveClass[GET]
      written(0).asInstanceOf[GET].q mustEqual "qname"
    }
    "parse GET, no queue name throws ProtocolError" in {
      quickDecode("GET" + SjqCodec.EOM) must throwA[ProtocolError]
    }

    "parse GET, queue name length less than 2 throws ProtocolError" in {
      quickDecode("GET q" + SjqCodec.EOM) must throwA[ProtocolError]
    }

    "parse GET, queue name length greater than 30 throws ProtocolError" in {
      quickDecode("GET queue_name_that_is_longer_than_30_characters" + SjqCodec.EOM) must throwA[ProtocolError]
    }

    "parse invalid command" in {
      quickDecode("KAPUT\n" + SjqCodec.EOM) must throwA[ProtocolError]
    }
  }
}
