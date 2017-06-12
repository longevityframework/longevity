package longevity.emblem.emblematic.traversors.async

import com.github.nscala_time.time.Imports._
import longevity.emblem.exceptions.CouldNotTransformException
import longevity.emblem.testData.exhaustive
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** specs for [[Transformer]] */
class TransformerSpec extends FlatSpec with GivenWhenThen with Matchers with ScalaFutures {

  private val transformer = new Transformer {
    val executionContext = global
    override protected val emblematic = exhaustive.emblematic
    override protected def transformBoolean(input: Future[Boolean]): Future[Boolean] = input.map(!_)
    override protected def transformChar(input: Future[Char]): Future[Char] = input.map(c => (c + 1).toChar)
    override protected def transformDateTime(input: Future[DateTime]): Future[DateTime] = input.map(_ + 1.days)
    override protected def transformDouble(input: Future[Double]): Future[Double] = input.map(_ + 1.0)
    override protected def transformFloat(input: Future[Float]): Future[Float] = input.map(_ + 1)
    override protected def transformInt(input: Future[Int]): Future[Int] = input.map(_ + 1)
    override protected def transformLong(input: Future[Long]): Future[Long] = input.map(_ + 1)
    override protected def transformString(input: Future[String]): Future[String] = input.map(_ + "1")
  }

  behavior of "Transformer.transform for types not covered by the emblematic"

  case class NoEmblem()

  it should "produce a CouldNotTransformException" in {
    transformer.transform(Future.successful(NoEmblem()))
      .failed.futureValue shouldBe a [CouldNotTransformException]
  }

  behavior of "Transformer.transform for basic values"

  it should "produce the transformed basic value" in {
    val date = DateTime.now
    transformer.transform(Future.successful(true)).futureValue should equal (false)
    transformer.transform(Future.successful('c')).futureValue should equal ('d')
    transformer.transform(Future.successful(date)).futureValue should equal (date + 1.day)
    transformer.transform(Future.successful(0.7d)).futureValue should equal (1.7d)
    transformer.transform(Future.successful(0.7f)).futureValue should equal (1.7f)
    transformer.transform(Future.successful(316)).futureValue should equal (317)
    transformer.transform(Future.successful(24l)).futureValue should equal (25l)
    transformer.transform(Future.successful("foo")).futureValue should equal ("foo1")
  }

  behavior of "Transformer.transform for options"

  it should "produce the transformed option" in {
    transformer.transform(Future.successful(None)).futureValue should equal (None)
    transformer.transform(Future.successful(Some(6))).futureValue should equal (Some(7))
  }

  behavior of "Transformer.transform for sets"

  it should "produce a copy of the set with elements transformed" in {
    transformer.transform(Future.successful(Set())).futureValue should equal (Set())
    transformer.transform(Future.successful(Set(6, 7))).futureValue should equal (Set(7, 8))
  }

  behavior of "Transformer.transform for lists"

  it should "produce a copy of the list with elements transformed" in {
    transformer.transform(Future.successful(Nil)).futureValue should equal (Nil)
    transformer.transform(Future.successful(List())).futureValue should equal (List())

    transformer.transform(Future.successful(6 :: 7 :: Nil)).futureValue should equal (7 :: 8 :: Nil)
    transformer.transform(Future.successful(List(6, 7))).futureValue should equal (List(7, 8))
  }

  behavior of "Transformer.transform for single-prop emblems"

  it should "produce a copy of the emblem with the prop value transformed" in {
    val s = "foo"
    transformer.transform(Future.successful(exhaustive.Email(s))).futureValue should equal {
      exhaustive.Email(s + "1")
    }
  }

  behavior of "Transformer.transform for emblems with basics"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val date = DateTime.now
    val withBasics1 = exhaustive.WithBasics(true, 'f', date, 3245.24543d, 43.4f, 245, 45345l, "ggg")
    val withBasics2 = exhaustive.WithBasics(false, 'g', date + 1.day, 3246.24543d, 44.4f, 246, 45346l, "ggg1")
    transformer.transform(Future.successful(withBasics1)).futureValue should equal (withBasics2)
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
    transformer.transform(Future.successful(emblem1)).futureValue should equal (emblem2)
  }

  behavior of "Transformer.transform for emblems with collections"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val emblem1 = exhaustive.WithCollections(Some("A"), Set("B", "C"), List("D", "E", "F"))
    val emblem2 = exhaustive.WithCollections(Some("A1"), Set("B1", "C1"), List("D1", "E1", "F1"))
    transformer.transform(Future.successful(emblem1)).futureValue should equal (emblem2)
  }

  behavior of "Transformer.transform for optional emblems"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val date = DateTime.now
    val withBasics1 = exhaustive.WithBasics(true, 'f', date, 3245.24543d, 43.4f, 245, 45345l, "ggg")
    val withBasics2 = exhaustive.WithBasics(false, 'g', date + 1.day, 3246.24543d, 44.4f, 246, 45346l, "ggg1")
    transformer.transform(Future.successful(Some(withBasics1))).futureValue should equal (Some(withBasics2))
  }

  behavior of "Transformer.transform for sets of emblems"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val date = DateTime.now
    val withBasics1 = exhaustive.WithBasics(true, 'f', date, 3245.24543d, 43.4f, 245, 45345l, "ggg")
    val withBasics2 = exhaustive.WithBasics(false, 'g', date + 1.day, 3246.24543d, 44.4f, 246, 45346l, "ggg1")
    transformer.transform(Future.successful(Set(withBasics1))).futureValue should equal (Set(withBasics2))
  }

  behavior of "Transformer.transform for lists of emblems"

  it should "produce a copy of the emblem with the prop values transformed" in {
    val date = DateTime.now
    val withBasics1 = exhaustive.WithBasics(true, 'f', date, 3245.24543d, 43.4f, 245, 45345l, "ggg")
    val withBasics2 = exhaustive.WithBasics(false, 'g', date + 1.day, 3246.24543d, 44.4f, 246, 45346l, "ggg1")
    transformer.transform(Future.successful(List(withBasics1))).futureValue should equal (List(withBasics2))
  }

  behavior of "Transformer.transform for unions"

  it should "produce a copy of the union with the prop values transformed" in {
    val s11 = exhaustive.Specialization1("common", "special1")
    val s12 = exhaustive.Specialization1("common1", "special11")

    transformer.transform(Future.successful(s11)).futureValue should equal (s12)

    val s21 = exhaustive.Specialization2("common", "special2")
    val s22 = exhaustive.Specialization2("common1", "special21")

    transformer.transform(Future.successful(s21)).futureValue should equal (s22)
  }

}
