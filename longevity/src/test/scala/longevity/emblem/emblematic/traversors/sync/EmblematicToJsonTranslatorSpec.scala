package longevity.emblem.emblematic.traversors.sync

import longevity.emblem.jsonUtil.dateTimeFormatter
import longevity.emblem.testData.geometry
import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC
import org.json4s.JsonAST._
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** some simple specs for [[EmblematicToJsonTranslator]]. these only test a limited
 * number of features. see [[JsonTranslationSpec]] for a more comprehensive
 * suite.
 */
class EmblematicToJsonTranslatorSpec extends FlatSpec with GivenWhenThen with Matchers {

  private val translator = new EmblematicToJsonTranslator {
    override protected val emblematic = geometry.emblematic
  }

  behavior of "EmblematicToJsonTranslator.generate[A] for basic types"

  it should "produce the appropriate json4s values" in {
    translator.translate(true) should equal (JBool(true))
    translator.translate(false) should equal (JBool(false))
    translator.translate('q') should equal (JString("q"))
    val dt = DateTime.now.withZoneRetainFields(UTC)
    translator.translate(dt) should equal (JString(dateTimeFormatter.print(dt)))
    translator.translate(0.7d) should equal (JDouble(0.7d))
    translator.translate(0.7f) should equal (JDouble(0.7f))
    translator.translate(9) should equal (JInt(9))
    translator.translate(9L) should equal (JLong(9L))
    translator.translate("string") should equal (JString("string"))
  }

  behavior of "EmblematicToJsonTranslator.generate[Point]"

  it should "produce the appropriate json4s values" in {
    translator.translate(geometry.Point(0.4d, -0.3d)) should equal {
      JObject(List("x" -> JDouble(0.4d), "y" -> JDouble(-0.3d)))
    }
  }

  behavior of "EmblematicToJsonTranslator.generate[Polygon]"

  it should "produce the appropriate json4s values" in {
    { translator.translate(geometry.Polygon(Set(
        geometry.Point(0.44d, -0.34d),
        geometry.Point(0.45d, -0.35d),
        geometry.Point(0.46d, -0.36d))))
    } should equal {
      JObject(List("corners" -> JArray(List(
        JObject(List("x" -> JDouble(0.44d), "y" -> JDouble(-0.34d))),
        JObject(List("x" -> JDouble(0.45d), "y" -> JDouble(-0.35d))),
        JObject(List("x" -> JDouble(0.46d), "y" -> JDouble(-0.36d)))))))
    }
  }

}
