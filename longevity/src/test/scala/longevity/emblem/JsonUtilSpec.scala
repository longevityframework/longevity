package longevity.emblem

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
    val expected = DateTime.now.withZoneRetainFields(DateTimeZone.UTC)
    val stringRep = dateTimeFormatter.print(expected)
    val actual = dateTimeFormatter.parseDateTime(stringRep)
    actual should equal (expected)
  }

}
