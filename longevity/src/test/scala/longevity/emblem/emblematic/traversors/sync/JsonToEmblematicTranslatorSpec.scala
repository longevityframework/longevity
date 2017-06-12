package longevity.emblem.emblematic.traversors.sync

import com.github.nscala_time.time.Imports._
import longevity.emblem.jsonUtil.dateTimeFormatter
import longevity.emblem.testData.geometry
import org.json4s.JsonAST._
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** some simple specs for [[JsonToEmblematicTranslator]]. these only test a limited
 * number of features. see [[JsonTranslationSpec]] for a more comprehensive
 * suite.
 */
class JsonToEmblematicTranslatorSpec extends FlatSpec with GivenWhenThen with Matchers {

  private val translator = new JsonToEmblematicTranslator {
    override protected val emblematic = geometry.emblematic
  }

  behavior of "JsonToEmblematicTranslator.generate[A] for basic types"

  it should "produce the appropriate json4s values" in {
    translator.translate[Boolean](JBool(true)) should equal (true)
    translator.translate[Boolean](JBool(false)) should equal (false)
    translator.translate[Char](JString("q")) should equal ('q')
    val dt = DateTime.now
    translator.translate[DateTime](JString(dateTimeFormatter.print(dt))) should equal (dt)
    translator.translate[Double](JDouble(0.7d)) should equal (0.7d)
    translator.translate[Float](JDouble(0.7f)) should equal (0.7f)
    translator.translate[Int](JInt(9)) should equal (9)
    translator.translate[Long](JLong(9L)) should equal (9L)
    translator.translate[String](JString("string")) should equal ("string")
  }

  behavior of "JsonToEmblematicTranslator.generate[Point]"

  it should "produce a Point from json4s" in {
    { translator.translate[geometry.Point](
      JObject(List("x" -> JDouble(0.4d), "y" -> JDouble(-0.3d))))
    } should equal {
      geometry.Point(0.4d, -0.3d)
    }
  }

  behavior of "JsonToEmblematicTranslator.generate[Polygon]"

  it should "produce a Polygon from json4s" in {
    { translator.translate[geometry.Polygon](
      JObject(List("corners" -> JArray(List(
        JObject(List("x" -> JDouble(0.44d), "y" -> JDouble(-0.34d))),
        JObject(List("x" -> JDouble(0.45d), "y" -> JDouble(-0.35d))),
        JObject(List("x" -> JDouble(0.46d), "y" -> JDouble(-0.36d))))))))
    } should equal {
      geometry.Polygon(Set(
        geometry.Point(0.44d, -0.34d),
        geometry.Point(0.45d, -0.35d),
        geometry.Point(0.46d, -0.36d)))
    }
  }

}
