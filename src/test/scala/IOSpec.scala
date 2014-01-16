package vindinium

object IOSpec extends org.specs2.mutable.Specification with IOFixtures {
  "I/O" title

  "String content" should {
    "be expected one from input" in {
      IO.fromUrl(inputUrl, "UTF-8", readerToStr) mustEqual inputStr
    }
  }
}

sealed trait IOFixtures {
  import java.io.BufferedReader

  lazy val inputUrl = getClass.getResource("/input1.json")

  lazy val inputStr = scala.io.Source.
    fromURL(inputUrl).getLines.mkString

  lazy val readerToStr = new UnaryFunction[BufferedReader, String] {
    def apply(r: BufferedReader): String = {
      val buf = new StringBuffer()

      var l: String = null
      do {
        l = r.readLine
        if (l != null) buf.append(l)
      } while (l != null)

      buf.toString
    }
  }
}
