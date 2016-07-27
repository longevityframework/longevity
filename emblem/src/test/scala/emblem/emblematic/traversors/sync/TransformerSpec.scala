package emblem.emblematic.traversors.sync

import com.github.nscala_time.time.Imports._
import emblem.exceptions.CouldNotTransformException
import emblem.testData.exhaustive
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specs for [[Transformer]] */
class TransformerSpec extends FlatSpec with GivenWhenThen with Matchers {

  private val transformer = new Transformer {
    override protected val emblematic = exhaustive.emblematic
    override protected def transformBoolean(input: Boolean): Boolean = !input
    override protected def transformChar(input: Char): Char = (input + 1).toChar
    override protected def transformDateTime(input: DateTime): DateTime = input + 1.days
    override protected def transformDouble(input: Double): Double = input + 1.0
    override protected def transformFloat(input: Float): Float = input + 1
    override protected def transformInt(input: Int): Int = input + 1
    override protected def transformLong(input: Long): Long = input + 1
    override protected def transformString(input: String): String = input + "1"
  }

  behavior of "Transformer.transform for types not covered by the emblematic"

  case class NoEmblem()

  it should "produce a CouldNotTransformException" in {
    intercept[CouldNotTransformException] {
      transformer.transform(NoEmblem())
    }
  }

  behavior of "Transformer.transform for basic values"

  it should "produce the transformed basic value" in {
    val date = DateTime.now
    transformer.transform(true) should equal (false)
    transformer.transform('c') should equal ('d')
    transformer.transform(date) should equal (date + 1.day)
    transformer.transform(0.7d) should equal (1.7d)
    transformer.transform(0.7f) should equal (1.7f)
    transformer.transform(316) should equal (317)
    transformer.transform(24l) should equal (25l)
    transformer.transform("foo") should equal ("foo1")
  }

  behavior of "Transformer.transform for options"

  it should "produce the transformed option" in {
    transformer.transform(None) should equal (None)
    transformer.transform(Some(6)) should equal (Some(7))
  }

  behavior of "Transformer.transform for sets"

  it should "produce a copy of the set with elements transformed" in {
    transformer.transform(Set()) should equal (Set())
    transformer.transform(Set(6, 7)) should equal (Set(7, 8))
  }

  behavior of "Transformer.transform for lists"

  it should "produce a copy of the list with elements transformed" in {
    transformer.transform(Nil) should equal (Nil)
    transformer.transform(List()) should equal (List())

    transformer.transform(6 :: 7 :: Nil) should equal (7 :: 8 :: Nil)
    transformer.transform(List(6, 7)) should equal (List(7, 8))
  }

  behavior of "Transformer.transform for single-prop emblems"

  it should "produce a copy of the emblem with the prop value transformed" in {
    val s = "foo"
    transformer.transform(exhaustive.Email(s)) should equal (exhaustive.Email(s + "1"))
  }

  behavior of "Transformer.transform for emblems with basics"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val date = DateTime.now
    val withBasics1 = exhaustive.WithBasics(true, 'f', date, 3245.24543d, 43.4f, 245, 45345l, "ggg")
    val withBasics2 = exhaustive.WithBasics(false, 'g', date + 1.day, 3246.24543d, 44.4f, 246, 45346l, "ggg1")
    transformer.transform(withBasics1) should equal (withBasics2)
  }

  behavior of "Transformer.transform for emblems with single-prop emblems"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val emblem1 = exhaustive.WithSinglePropEmblems(
      exhaustive.Email("1234"),
      exhaustive.Markdown("5678"),
      exhaustive.Uri("90"))
    val emblem2 = exhaustive.WithSinglePropEmblems(
      exhaustive.Email("12341"),
      exhaustive.Markdown("56781"),
      exhaustive.Uri("901"))
    transformer.transform(emblem1) should equal (emblem2)
  }

  behavior of "Transformer.transform for emblems with collections"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val emblem1 = exhaustive.WithCollections(Some("A"), Set("B", "C"), List("D", "E", "F"))
    val emblem2 = exhaustive.WithCollections(Some("A1"), Set("B1", "C1"), List("D1", "E1", "F1"))
    transformer.transform(emblem1) should equal (emblem2)
  }

  behavior of "Transformer.transform for optional emblems"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val date = DateTime.now
    val withBasics1 = exhaustive.WithBasics(true, 'f', date, 3245.24543d, 43.4f, 245, 45345l, "ggg")
    val withBasics2 = exhaustive.WithBasics(false, 'g', date + 1.day, 3246.24543d, 44.4f, 246, 45346l, "ggg1")
    transformer.transform(Some(withBasics1)) should equal (Some(withBasics2))
  }

  behavior of "Transformer.transform for sets of emblems"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val date = DateTime.now
    val withBasics1 = exhaustive.WithBasics(true, 'f', date, 3245.24543d, 43.4f, 245, 45345l, "ggg")
    val withBasics2 = exhaustive.WithBasics(false, 'g', date + 1.day, 3246.24543d, 44.4f, 246, 45346l, "ggg1")
    transformer.transform(Set(withBasics1)) should equal (Set(withBasics2))
  }

  behavior of "Transformer.transform for lists of emblems"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val date = DateTime.now
    val withBasics1 = exhaustive.WithBasics(true, 'f', date, 3245.24543d, 43.4f, 245, 45345l, "ggg")
    val withBasics2 = exhaustive.WithBasics(false, 'g', date + 1.day, 3246.24543d, 44.4f, 246, 45346l, "ggg1")
    transformer.transform(List(withBasics1)) should equal (List(withBasics2))
  }

  behavior of "Transformer.transform for unions"

  it should "produce a copy of the union with the prop values transformed" in {
    val s11 = exhaustive.Specialization1("common", "special1")
    val s12 = exhaustive.Specialization1("common1", "special11")

    transformer.transform(s11) should equal (s12)

    val s21 = exhaustive.Specialization2("common", "special2")
    val s22 = exhaustive.Specialization2("common1", "special21")

    transformer.transform(s21) should equal (s22)
  }

}
