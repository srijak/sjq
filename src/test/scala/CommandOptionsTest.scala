package sjq.Codec

import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.junit.JUnitSuite
import junit.framework.Assert._
import org.junit.Test
import net.lag.naggati.ProtocolError
import java.net.URL

class CommandOptionsTest extends JUnitSuite {

  @Test
  def parse_Put_Options_only_qname() {
    val opts = new PutOptions(List("qname"))
    assertSame(opts.q, "qname")
  }

  @Test
  def parse_Put_Options_qname_callbackUrl() {
    val opts = new PutOptions(List("qname", "http://google.com"))
    assertEquals(opts.q, "qname")
    assertTrue(opts.callback_urls.contains(new URL("http://google.com")))
  }
  @Test
  def parse_Put_Options_qname_ttr_callbackUrl() {
    val opts = new PutOptions(List("qname", "25", "http://google.com"))
    assertEquals(opts.q, "qname")
    assertEquals(opts.ttr_in_seconds.get, 25)
    assertTrue(opts.callback_urls.contains(new URL("http://google.com")))
  }

  @Test
  def parse_Put_Options_qname_multiple_callbackUrls() {
    val opts = new PutOptions(List("qname", "http://google.com", "http://gar.com"))
    assertEquals(opts.q, "qname")

    assertTrue(opts.callback_urls.contains(new URL("http://google.com")))
    assertTrue(opts.callback_urls.contains(new URL("http://gar.com")))
  }

  @Test
  def parse_Put_Options_qname_ttr_multiple_callbackUrls() {
    val opts = new PutOptions(List("qname", "1500", "http://google.com", "http://gar.com"))
    assertEquals(opts.q, "qname")
    assertEquals(opts.ttr_in_seconds.get, 1500)
    assertTrue(opts.callback_urls.contains(new URL("http://google.com")))
    assertTrue(opts.callback_urls.contains(new URL("http://gar.com")))
  }

  @Test
  def parse_Get_Options_only_qname() {
    val opts = new GetOptions(List("qname"))
    assertSame(opts.q, "qname")
  }
  
  @Test
  def parse_Done_Options_only_id() {
    val opts = new DoneOptions(List("-123"))
    assertSame(opts.id, -123)
  }
}
