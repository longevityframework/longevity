package emblem

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import jsonUtil.dateTimeFormatter

/** [[jsonUtil]] specifications */
class JsonUtilSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "emblem.jsonUtil.dateTimeFormatter"

  it should "produce the original DateTime in a round trip" in {
    val expected = DateTime.now
    val stringRep = dateTimeFormatter.print(expected)
    val actual = dateTimeFormatter.parseDateTime(stringRep)
    actual should equal (expected)
  }

  it should "produce the original DateTime in a round trip when time zone is set to UTC" in {
    val expected = DateTime.now.withZone(DateTimeZone.UTC)
    val stringRep = dateTimeFormatter.print(expected)
    val actual = dateTimeFormatter.parseDateTime(stringRep)
    actual should equal (expected)
  }

  it should "produce the original DateTime in a round trip when time zone is set to Etc/UTC" in {
    val expected = DateTime.now.withZone(DateTimeZone.forID("Etc/UTC"))
    val stringRep = dateTimeFormatter.print(expected)
    val actual = dateTimeFormatter.parseDateTime(stringRep)
    actual should equal (expected)
  }

}
