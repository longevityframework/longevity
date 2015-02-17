package emblem.traversors

//import emblem._
//import emblem.exceptions.CouldNotGenerateException
import emblem.testData.blogs._
//import emblem.traversors.Generator._
//import org.scalatest.OptionValues._
import org.scalatest._
import Differ._

/** specs for [[Differ]] */
class DifferSpec extends FlatSpec with GivenWhenThen with Matchers {

  lazy val differ = new Differ(emblemPool, shorthandPool)

  // TODO:
  // traverseCustomOption(input) orElse
  // traverseEmblemOptionFromAny(input) orElse
  // traverseShorthandOption(input) orElse
  // traverseOptionOption(input) orElse
  // traverseSetOption(input) orElse

  behavior of "Differ.diff for lists"

  it should "produce an empty Diffs when the values match" in {

    // this is known to fail TODO fix
    //differ.diff(Nil, Nil) should equal (Diffs())

    differ.diff(List(), List()) should equal (Diffs())
    differ.diff(List(6, 7), List(6, 7)) should equal (Diffs())
  }

  it should "produce a single Diff with path .size when the values have different sizes" in {
    differ.diff(List(), List(34)) should equal (Diffs(Diff(".size", 0, 1)))
    differ.diff(List(6, 7), List(6, 7, 8)) should equal (Diffs(Diff(".size", 2, 3)))
  }

  it should "produce diffs on a per-element basis when lists have same size but elements don't match" in {
    differ.diff(List(33), List(34)) should equal (Diffs(Diff("(0)", 33, 34)))
    differ.diff(List(12, 7, 9), List(6, 7, 8)) should equal (Diffs(
      Diff("(0)", 12, 6),
      Diff("(2)", 9, 8)))
  }

  behavior of "Differ.diff for basic values"

  it should "produce an empty Diffs when the values match" in {
    differ.diff(true, true) should equal (Diffs())
    differ.diff('c', 'c') should equal (Diffs())
    differ.diff(0.7d, 0.7d) should equal (Diffs())
    differ.diff(0.7f, 0.7f) should equal (Diffs())
    differ.diff(316, 316) should equal (Diffs())
    differ.diff(24l, 24l) should equal (Diffs())
    differ.diff("foo", "foo") should equal (Diffs())
  }

  it should "produce a single Diff with empty path when the values don't match" in {
    differ.diff(true, false) should equal (Diffs(Diff("", true, false)))
    differ.diff('c', 'd') should equal (Diffs(Diff("", 'c', 'd')))
    differ.diff(0.7d, 0.8d) should equal (Diffs(Diff("", 0.7d, 0.8d)))
    differ.diff(0.7f, 0.8f) should equal (Diffs(Diff("", 0.7f, 0.8f)))
    differ.diff(316, 317) should equal (Diffs(Diff("", 316, 317)))
    differ.diff(24L, 25L) should equal (Diffs(Diff("", 24L, 25L)))
    differ.diff("foo", "bar") should equal (Diffs(Diff("", "foo", "bar")))
  }

}
