package emblem.emblematic.traversors.sync

import Differ._
import com.github.nscala_time.time.Imports._
import emblem.exceptions.CouldNotTraverseException
import emblem.testData.exhaustive._
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specs for [[Differ]] */
class DifferSpec extends FlatSpec with GivenWhenThen with Matchers {

  lazy val differ = new Differ(emblematic)

  behavior of "Differ.diff for types not covered by the emblematic"

  case class NoEmblem()

  it should "produce a CouldNotTraverseException" in {
    intercept[CouldNotTraverseException] {
      differ.diff(NoEmblem(), NoEmblem())
    }
  }

  behavior of "Differ.diff for basic values"

  it should "produce an empty Diffs when the values match" in {
    val date = DateTime.now
    differ.diff(true, true) should equal (Diffs())
    differ.diff('c', 'c') should equal (Diffs())
    differ.diff(date, date) should equal (Diffs())
    differ.diff(0.7d, 0.7d) should equal (Diffs())
    differ.diff(0.7f, 0.7f) should equal (Diffs())
    differ.diff(316, 316) should equal (Diffs())
    differ.diff(24l, 24l) should equal (Diffs())
    differ.diff("foo", "foo") should equal (Diffs())
  }

  it should "produce a single Diff with empty path when the values don't match" in {
    val date1 = DateTime.now
    val date2 = date1 + 1.hour
    differ.diff(true, false) should equal (Diffs(Diff("", true, false)))
    differ.diff('c', 'd') should equal (Diffs(Diff("", 'c', 'd')))
    differ.diff(date1, date2) should equal (Diffs(Diff("", date1, date2)))
    differ.diff(0.7d, 0.8d) should equal (Diffs(Diff("", 0.7d, 0.8d)))
    differ.diff(0.7f, 0.8f) should equal (Diffs(Diff("", 0.7f, 0.8f)))
    differ.diff(316, 317) should equal (Diffs(Diff("", 316, 317)))
    differ.diff(24L, 25L) should equal (Diffs(Diff("", 24L, 25L)))
    differ.diff("foo", "bar") should equal (Diffs(Diff("", "foo", "bar")))
  }

  behavior of "Differ.diff for options"

  it should "produce an empty Diffs when the values match" in {
    differ.diff(None, None) should equal (Diffs())
    differ.diff(Some(6), Some(6)) should equal (Diffs())
  }

  it should "produce a single Diff with path .size when the values have different sizes" in {
    differ.diff(None, Some(34)) should equal (Diffs(Diff(".size", 0, 1)))
    differ.diff(Some(34), None) should equal (Diffs(Diff(".size", 1, 0)))
  }

  it should "produce a single diff with path value when values have the same size but don't match" in {
    differ.diff(Some(33), Some(34)) should equal (Diffs(Diff(".value", 33, 34)))
  }

  behavior of "Differ.diff for sets"

  it should "produce an empty Diffs when the values match" in {
    differ.diff(Set(), Set()) should equal (Diffs())
    differ.diff(Set(6, 7), Set(6, 7)) should equal (Diffs())
  }

  it should "produce a single Diff with path .size when the values have different sizes" in {
    differ.diff(Set(), Set(34)) should equal (Diffs(Diff(".size", 0, 1)))
    differ.diff(Set(6, 7), Set(6, 7, 8)) should equal (Diffs(Diff(".size", 2, 3)))
  }

  it should "produce a single diff with empty path when values have the same size but don't match" in {
    differ.diff(Set(33), Set(34)) should equal (Diffs(Diff("", Set(33), Set(34))))
    differ.diff(Set(12, 7, 9), Set(6, 7, 8)) should equal (Diffs(Diff("", Set(12, 7, 9), Set(6, 7, 8))))
  }

  behavior of "Differ.diff for lists"

  it should "produce an empty Diffs when the values match" in {
    differ.diff(Nil, Nil) should equal (Diffs())
    differ.diff(List(), List()) should equal (Diffs())

    differ.diff(6 :: 7 :: Nil, 6 :: 7 :: Nil) should equal (Diffs())
    differ.diff(List(6, 7), List(6, 7)) should equal (Diffs())
  }

  it should "produce a single Diff with path .size when the values have different sizes" in {
    differ.diff(Nil, 34 :: Nil) should equal (Diffs(Diff(".size", 0, 1)))
    differ.diff(List(), List(34)) should equal (Diffs(Diff(".size", 0, 1)))

    differ.diff(List(6, 7), List(6, 7, 8)) should equal (Diffs(Diff(".size", 2, 3)))
    differ.diff(6 :: 7 :: Nil, 6 :: 7 :: 8 :: Nil) should equal (Diffs(Diff(".size", 2, 3)))
  }

  it should "produce diffs on a per-element basis when lists have same size but elements don't match" in {
    differ.diff(List(33), List(34)) should equal (Diffs(Diff("(0)", 33, 34)))
    differ.diff(33 :: Nil, 34 :: Nil) should equal (Diffs(Diff("(0)", 33, 34)))
    differ.diff(List(12, 7, 9), List(6, 7, 8)) should equal (Diffs(
      Diff("(0)", 12, 6),
      Diff("(2)", 9, 8)))
  }

  behavior of "Differ.diff for single-prop emblems"

  it should "produce an empty Diffs when the values match" in {
    val email = singlePropEmblems.email
    differ.diff(email, email) should equal (Diffs())
  }

  it should "produce a single Diff when the values differ" in {
    val email1 = singlePropEmblems.email
    val email2 = singlePropEmblems.email
    differ.diff(email1, email2) should equal (Diffs(Diff("", email1, email2)))
  }

  behavior of "Differ.diff for emblems with basics"

  it should "produce an empty Diffs when the values match" in {
    val withBasics = emblems.withBasics
    differ.diff(withBasics, withBasics) should equal (Diffs())
  }

  it should "produce a single Diff when the values differ" in {
    val withBasics1 = emblems.withBasics
    val withBasics2 = withBasics1.copy(int = basics.int)
    differ.diff(withBasics1, withBasics2) should equal {
      Diffs(Diff(".int", withBasics1.int, withBasics2.int))
    }
  }

  it should "find diffs in the right order" in {
    val withBasics1 = emblems.withBasics
    val withBasics2 = withBasics1.copy(int = basics.int, string = basics.string)
    differ.diff(withBasics1, withBasics2) should equal {
      Diffs(
        Diff(".int", withBasics1.int, withBasics2.int),
        Diff(".string", withBasics1.string, withBasics2.string))
    }
  }

  behavior of "Differ.diff for emblems with single-prop emblems"

  it should "produce an empty Diffs when the values match" in {
    val withSinglePropEmblems = emblems.withSinglePropEmblems
    differ.diff(withSinglePropEmblems, withSinglePropEmblems) should equal (Diffs())
  }

  it should "produce a single Diff when the values differ" in {
    val withSinglePropEmblems1 = emblems.withSinglePropEmblems
    val withSinglePropEmblems2 = withSinglePropEmblems1.copy(email = singlePropEmblems.email)
    differ.diff(withSinglePropEmblems1, withSinglePropEmblems2) should equal {
      Diffs(Diff(".email", withSinglePropEmblems1.email, withSinglePropEmblems2.email))
    }
  }

  behavior of "Differ.diff for emblems with collections"

  it should "produce an empty Diffs when the values match" in {
    val withCollections = emblems.withCollections
    differ.diff(withCollections, withCollections) should equal (Diffs())
  }

  it should "produce a single Diff when the values differ in size" in {
    val withCollections1 = emblems.withCollections.copy(option = None)
    val withCollections2 = withCollections1.copy(
      option = Some("foo"),
      set = withCollections1.set + "foo452435",
      list = "foo" :: withCollections1.list)
    differ.diff(withCollections1, withCollections2) should equal {
      Diffs(
        Diff(".option.size", withCollections1.option.size, withCollections2.option.size),
        Diff(".set.size", withCollections1.set.size, withCollections2.set.size),
        Diff(".list.size", withCollections1.list.size, withCollections2.list.size))
    }
  }

  it should "produce a single Diff when the values differ in values" in {
    val withCollections1 = WithCollections(
      option = Some("foo1"),
      set = Set("foo1", "foo2"),
      list = List("foo1", "foo2", "foo3"))
    val withCollections2 = WithCollections(
      option = Some("foo2"),
      set = Set("foo1", "foo3"),
      list = List("foo1", "foo3", "foo4"))
    differ.diff(withCollections1, withCollections2) should equal {
      Diffs(
        Diff(".option.value", withCollections1.option.get, withCollections2.option.get),
        Diff(".set", withCollections1.set, withCollections2.set),
        Diff(".list(1)", withCollections1.list(1), withCollections2.list(1)),
        Diff(".list(2)", withCollections1.list(2), withCollections2.list(2)))
    }
  }

  behavior of "Differ.diff for optional emblems"

  it should "produce an empty Diffs when the values match" in {
    val option = Some(emblems.withBasics)
    differ.diff(option, option) should equal (Diffs())
  }

  it should "produce a single Diff when the values differ in size" in {
    val option1 = None
    val option2 = Some(emblems.withBasics)
    differ.diff(option1, option2) should equal (Diffs(Diff(".size", 0, 1)))
  }

  it should "produce a single Diff when the values differ in values" in {
    val option1 = Some(emblems.withBasics)
    val option2 = Some(emblems.withBasics)
    differ.diff(option1, option2) should equal (Diffs(Diff(".value", option1.get, option2.get)))
  }

  behavior of "Differ.diff for sets of emblems"

  it should "produce an empty Diffs when the values match" in {
    val e1 = emblems.withBasics
    val e2 = emblems.withBasics
    differ.diff(Set[WithBasics](), Set[WithBasics]()) should equal (Diffs())
    differ.diff(Set(e1), Set(e1)) should equal (Diffs())
    differ.diff(Set(e1, e2), Set(e1, e2)) should equal (Diffs())
  }

  it should "produce a single Diff when the values differ in size" in {
    val e1 = emblems.withBasics
    val e2 = emblems.withBasics
    differ.diff(Set[WithBasics](), Set(e1)) should equal (Diffs(Diff(".size", 0, 1)))
    differ.diff(Set(e1), Set(e1, e2)) should equal (Diffs(Diff(".size", 1, 2)))
  }

  it should "produce a single Diff when the values differ in values" in {
    val se1 = Set(emblems.withBasics)
    val se2 = Set(emblems.withBasics)
    differ.diff(se1, se2) should equal (Diffs(Diff("", se1, se2)))
  }

  behavior of "Differ.diff for lists of emblems"

  it should "produce an empty Diffs when the values match" in {
    val e1 = emblems.withBasics
    val e2 = emblems.withBasics
    differ.diff(List[WithBasics](), List[WithBasics]()) should equal (Diffs())
    differ.diff(List(e1), List(e1)) should equal (Diffs())
    differ.diff(List(e1, e2), List(e1, e2)) should equal (Diffs())
  }

  it should "produce a single Diff when the values differ in size" in {
    val e1 = emblems.withBasics
    val e2 = emblems.withBasics
    differ.diff(List[WithBasics](), List(e1)) should equal (Diffs(Diff(".size", 0, 1)))
    differ.diff(List(e1), List(e1, e2)) should equal (Diffs(Diff(".size", 1, 2)))
  }

  it should "produce a single Diff when the values differ in values" in {
    val le1 = List(emblems.withBasics)
    val le2 = List(emblems.withBasics)
    differ.diff(le1, le2) should equal (Diffs(Diff("(0)", le1.head, le2.head)))
  }

  // TODO unions

}
