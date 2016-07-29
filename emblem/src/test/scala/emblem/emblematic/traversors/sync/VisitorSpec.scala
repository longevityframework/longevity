package emblem.emblematic.traversors.sync

import com.github.nscala_time.time.Imports._
import emblem.exceptions.CouldNotVisitException
import emblem.testData.exhaustive
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specs for [[Visitor]] */
class VisitorSpec extends FlatSpec with GivenWhenThen with Matchers {

  class CountingVisitor extends Visitor {
    var counts = Map[Any, Int]().withDefaultValue(0)

    override protected val emblematic = exhaustive.emblematic
    override protected def visitBoolean(input: Boolean): Unit = synchronized {
      counts += input -> (counts(input) + 1)
    }
    override protected def visitChar(input: Char): Unit = synchronized {
      counts += input -> (counts(input) + 1)
    }
    override protected def visitDateTime(input: DateTime): Unit = synchronized {
      counts += input -> (counts(input) + 1)
    }
    override protected def visitDouble(input: Double): Unit = synchronized {
      counts += input -> (counts(input) + 1)
    }
    override protected def visitFloat(input: Float): Unit = synchronized {
      counts += input -> (counts(input) + 1)
    }
    override protected def visitInt(input: Int): Unit = synchronized {
      counts += input -> (counts(input) + 1)
    }
    override protected def visitLong(input: Long): Unit = synchronized {
      counts += input -> (counts(input) + 1)
    }
    override protected def visitString(input: String): Unit = synchronized {
      counts += input -> (counts(input) + 1)
    }
  }

  private def newVisitor = new CountingVisitor

  behavior of "Visitor.visit for types not covered by the emblematic"

  case class NoEmblem()

  it should "produce a CouldNotVisitException" in {
    intercept[CouldNotVisitException] {
      newVisitor.visit(NoEmblem())
    }
  }

  behavior of "Visitor.visit for basic values"

  it should "produce the visited basic value" in {
    val visitor = newVisitor
    val date = DateTime.now
    visitor.visit(true)
    visitor.visit('c')
    visitor.visit(date)
    visitor.visit(0.7d)
    visitor.visit(0.7f)
    visitor.visit(316)
    visitor.visit(24l)
    visitor.visit("foo")

    visitor.counts(true) should equal (1)
    visitor.counts('c') should equal (1)
    visitor.counts(date) should equal (1)
    visitor.counts(0.7d) should equal (1)
    visitor.counts(0.7f) should equal (1)
    visitor.counts(316) should equal (1)
    visitor.counts(24l) should equal (1)
    visitor.counts("foo") should equal (1)
  }

  behavior of "Visitor.visit for options"

  it should "produce the visited option" in {
    val visitor = newVisitor
    visitor.visit(None)
    visitor.visit(Some(6))
    visitor.counts(6) should equal (1)
  }

  behavior of "Visitor.visit for sets"

  it should "produce a copy of the set with elements visited" in {
    val visitor = newVisitor
    visitor.visit(Set())
    visitor.visit(Set(6, 7))
    visitor.counts(6) should equal (1)
    visitor.counts(7) should equal (1)
  }

  behavior of "Visitor.visit for lists"

  it should "produce a copy of the list with elements visited" in {
    val visitor = newVisitor
    visitor.visit(Nil)
    visitor.visit(List())

    visitor.visit(6 :: 7 :: Nil)
    visitor.visit(List(6, 7))
    visitor.counts(6) should equal (2)
    visitor.counts(7) should equal (2)
  }

  behavior of "Visitor.visit for single-prop emblems"

  it should "produce a copy of the emblem with the prop value visited" in {
    val visitor = newVisitor
    val s = "foo"
    visitor.visit(exhaustive.Email(s))
    visitor.counts(s) should equal (1)
  }

  behavior of "Visitor.visit for emblems with basics"

  it should "produce a copy of the emblem with the prop values visited" in {
    val visitor = newVisitor
    val date = DateTime.now
    val withBasics = exhaustive.WithBasics(true, 'f', date, 3245.24543d, 43.4f, 245, 45345l, "ggg")
    visitor.visit(withBasics)
    visitor.counts(true) should equal (1)
    visitor.counts('f') should equal (1)
    visitor.counts(date) should equal (1)
    visitor.counts(3245.24543d) should equal (1)
    visitor.counts(43.4f) should equal (1)
    visitor.counts(245) should equal (1)
    visitor.counts(45345l) should equal (1)
    visitor.counts("ggg") should equal (1)
  }

  behavior of "Visitor.visit for emblems with single-prop emblems"

  it should "produce a copy of the emblem with the prop values visited" in {
    val visitor = newVisitor
    val emblem1 = exhaustive.WithSinglePropEmblems(
      exhaustive.Email("1234"),
      exhaustive.Markdown("5678"),
      exhaustive.Uri("90"))
    visitor.visit(emblem1)

    visitor.counts.size should equal (3)
    visitor.counts("1234") should equal (1)
    visitor.counts("5678") should equal (1)
    visitor.counts("90") should equal (1)
  }

  behavior of "Visitor.visit for emblems with collections"

  it should "produce a copy of the emblem with the prop values visited" in {
    val visitor = newVisitor
    val emblem1 = exhaustive.WithCollections(Some("A"), Set("B", "C"), List("D", "E", "F"))
    visitor.visit(emblem1)

    visitor.counts.size should equal (6)
    visitor.counts("A") should equal (1)
    visitor.counts("B") should equal (1)
    visitor.counts("C") should equal (1)
    visitor.counts("D") should equal (1)
    visitor.counts("E") should equal (1)
    visitor.counts("F") should equal (1)
  }

  behavior of "Visitor.visit for unions"

  it should "produce a copy of the union with the prop values visited" in {
    val visitor = newVisitor
    val s11 = exhaustive.Specialization1("common", "special1")
    visitor.visit(s11)

    val s21 = exhaustive.Specialization2("common", "special2")
    visitor.visit(s21)

    visitor.counts.size should equal (3)
    visitor.counts("common") should equal (2)
    visitor.counts("special1") should equal (1)
    visitor.counts("special2") should equal (1)
  }

}
