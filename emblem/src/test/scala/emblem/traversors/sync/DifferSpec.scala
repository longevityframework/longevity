package emblem.traversors.sync

import Differ._
import emblem.testData.blogs._
import org.scalatest._

/** specs for [[Differ]] */
class DifferSpec extends FlatSpec with GivenWhenThen with Matchers {

  lazy val differ = new Differ(emblemPool, extractorPool)

  // TODO pt-89942150 specs for CouldNotTraverseException cases

  behavior of "Differ.diff for emblems"

  lazy val user = CrmUser(
    "funnyUri",
    "strangeFirstName",
    "Smith",
    CrmAddress("someStreet", "", "Big Frost", "QW", 21211))

  it should "produce an empty Diffs when the values match" in {
    differ.diff(user, user) should equal (Diffs())
    val blog = CrmBlog("blogUri")
    differ.diff(blog, blog) should equal (Diffs())
  }

  it should "find diffs in basic values found directly in the emblem" in {
    val user1 = user
    val user2 = user.copy(firstName = "strangerFirstName", lastName = "Smithie")
    differ.diff(user1, user2) should equal (Diffs(
      Diff(".firstName", "strangeFirstName", "strangerFirstName"),
      Diff(".lastName", "Smith", "Smithie")))
  }

  it should "find diffs in extractors found directly in the emblem" in {
    val user1 = user
    val user2 = user.copy(uri = "sillyUri")
    differ.diff(user1, user2) should equal (Diffs(
      Diff(".uri.inverse", "funnyUri", "sillyUri")))
  }

  it should "find diffs in basic values found in a nested emblem" in {
    val user1 = user
    val user2 = user.copy(address = user.address.copy(street2 = "Hollow St"))
    differ.diff(user1, user2) should equal (Diffs(
      Diff(".address.street2", "", "Hollow St")))
  }

  it should "find diffs in extractors found in a nested emblem" in {
    val user1 = user
    val user2 = user.copy(address = user.address.copy(zipcode = 98765))
    differ.diff(user1, user2) should equal (Diffs(
      Diff(".address.zipcode.inverse", user1.address.zipcode.zipcode, user2.address.zipcode.zipcode)))
  }

  behavior of "Differ.diff for extractors"

  it should "produce an empty Diffs when the values match" in {
    differ.diff(Uri("x"), Uri("x")) should equal (Diffs())
    differ.diff(Zipcode(/*0*/1210), Zipcode(/*0*/1210)) should equal (Diffs())
  }

  it should "produce a single Diff with path .inverse when the values have different sizes" in {
    differ.diff(Uri("x"), Uri("y")) should equal {
      Diffs(Diff(".inverse", "x", "y"))
    }
    differ.diff(Zipcode(/*0*/1210), Zipcode(/*0*/1211)) should equal {
      Diffs(Diff(".inverse", /*0*/1210, /*0*/1211))
    }
  }

  behavior of "Differ.diff for options"

  it should "produce an empty Diffs when the values match" in {

    // TODO pt-88572226: fix differ to handle Nil and None types. theres a similar case with Nil further down
    //differ.diff(None, None) should equal (Diffs())

    differ.diff(None: Option[Nothing], None: Option[Nothing]) should equal (Diffs())
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

    // TODO pt-88572226: fix differ to handle Nil and None types. there's a similar case with None further up
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

  behavior of "Differ.diff for emblems with options, sets and lists of basic values"

  it should "produce an empty Diffs when the values match" in {
    CrmBlogPost() should equal (CrmBlogPost())
  }

  it should "find diffs in the basic value collections of the emblem" in {
    val post1 = CrmBlogPost()
    val post2 = post1.copy(tags = Set("tag2", "tag3"))
    val post3 = post1.copy(tags = post1.tags + "new tag")
    val post4 = post1.copy(longOpt = Some(-7))
    val post5 = post1.copy(longOpt = None)
    val post6 = post1.copy(intList = List(1, 4, 5))
    val post7 = post1.copy(intList = List())

    differ.diff(post1, post2) should equal (Diffs(Diff(".tags", post1.tags, post2.tags)))
    differ.diff(post1, post3) should equal (Diffs(Diff(".tags.size", post1.tags.size, post3.tags.size)))

    differ.diff(post1, post4) should equal (Diffs(Diff(".longOpt.value", post1.longOpt.get, post4.longOpt.get)))
    differ.diff(post1, post5) should equal (Diffs(Diff(".longOpt.size", post1.longOpt.size, post5.longOpt.size)))

    differ.diff(post1, post6) should equal (Diffs(
      Diff(".intList(1)", post1.intList(1), post6.intList(1)),
      Diff(".intList(2)", post1.intList(2), post6.intList(2))))
    differ.diff(post1, post7) should equal (Diffs(Diff(".intList.size", post1.intList.size, post7.intList.size)))
  }

  behavior of "Differ.diff for emblems with options, sets and lists of emblems"

  it should "produce an empty Diffs when the values match" in {
    CrmBlogPost() should equal (CrmBlogPost())
  }

  it should "find diffs in the emblem collections of the emblem" in {
    val post1 = CrmBlogPost()
    val post2 = post1.copy(authors = Set(CrmUser("some other user")))
    val post3 = post1.copy(authors = Set())
    val post4 = post1.copy(comments = List(CrmComment("c1"), CrmComment("c77")))
    val post5 = post1.copy(comments = List(CrmComment("c1")))
    val post6 = post1.copy(blog = Some(CrmBlog("new blog")))
    val post7 = post1.copy(blog = None)

    differ.diff(post1, post2) should equal (Diffs(Diff(".authors", post1.authors, post2.authors)))
    differ.diff(post1, post3) should equal (Diffs(Diff(".authors.size", post1.authors.size, post3.authors.size)))

    differ.diff(post1, post4) should equal (
      Diffs(Diff(".comments(1).uri.inverse", post1.comments(1).uri.uri, post4.comments(1).uri.uri)))
    differ.diff(post1, post5) should equal (
      Diffs(Diff(".comments.size", post1.comments.size, post5.comments.size)))

    differ.diff(post1, post6) should equal (
      Diffs(Diff(".blog.value.uri.inverse", post1.blog.get.uri.uri, post6.blog.get.uri.uri)))
    differ.diff(post1, post7) should equal (Diffs(Diff(".blog.size", post1.blog.size, post7.blog.size)))

  }

}
