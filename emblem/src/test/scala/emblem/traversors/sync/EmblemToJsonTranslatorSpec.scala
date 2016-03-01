package emblem.traversors.sync

import com.github.nscala_time.time.Imports._
import emblem.imports._
import emblem.jsonUtil.dateTimeFormatter
import emblem.testData.geometry
import org.json4s.JsonAST._
import org.scalatest._

/** specs for [[EmblemToJsonTranslator]] */
class EmblemToJsonTranslatorSpec extends FlatSpec with GivenWhenThen with Matchers {

  private val translator = new EmblemToJsonTranslator {
    override protected val emblemPool: EmblemPool = geometry.emblemPool
  }

  behavior of "EmblemToJsonTranslator.generate[A] for basic types"

  it should "produce the appropriate json4s values" in {
    translator.traverse(true) should equal (JBool(true))
    translator.traverse(false) should equal (JBool(false))
    translator.traverse('q') should equal (JString("q"))
    val dt = DateTime.now
    translator.traverse(dt) should equal (JString(dateTimeFormatter.print(dt)))
    translator.traverse(0.7d) should equal (JDouble(0.7d))
    translator.traverse(0.7f) should equal (JDouble(0.7f))
    translator.traverse(9) should equal (JInt(9))
    translator.traverse(9L) should equal (JLong(9L))
    translator.traverse("string") should equal (JString("string"))
  }

  behavior of "EmblemToJsonTranslator.generate[Point]"

  it should "produce the appropriate json4s values" in {
    translator.traverse(geometry.Point(0.4d, -0.3d)) should equal {
      JObject(List("x" -> JDouble(0.4d), "y" -> JDouble(-0.3d)))
    }
  }

  behavior of "EmblemToJsonTranslator.generate[Polygon]"

  it should "produce the appropriate json4s values" in {
    { translator.traverse(geometry.Polygon(Set(
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
