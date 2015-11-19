package longevity.unit.subdomain

import org.scalatest._
import longevity.subdomain._

/** unit tests for the proper construction of [[RootEntityType#Prop nat key props]] */
class QueryDslSpec extends FlatSpec with GivenWhenThen with Matchers {

  private case class Root(path1: Int, path2: Double, path3: String) extends RootEntity
  private object Root extends RootEntityType[Root] {
    val path1 = prop[Int]("path1")
    val path2 = prop[Double]("path2")
    val path3 = prop[String]("path3")
  }
  private val dsl = new QueryDsl[Root]
  import dsl._

  behavior of "QueryDsl"

  it should "build dynamic relational queries that match the results of Query object methods" in {
    val path = "foo"
    val value = 7

    var expected = Query.eqs(path, value)
    var actual: Query[Root] = path eqs value // the type help will come naturally when calling repo method
    actual should equal (expected)

    expected = Query.neq(path, value)
    actual = path neq value
    actual should equal (expected)

    expected = Query.lt(path, value)
    actual = path lt value
    actual should equal (expected)

    expected = Query.lte(path, value)
    actual = path lte value
    actual should equal (expected)

    expected = Query.gt(path, value)
    actual = path gt value
    actual should equal (expected)

    expected = Query.gte(path, value)
    actual = path gte value
    actual should equal (expected)
  }

  it should "combine two dynamic relational queries with logical operators" in {
    val path1 = "foo"
    val value1 = 7
    val path2 = "bar"
    val value2 = 1.2

    var expected: Query[Root] = Query.and(Query.eqs(path1, value1), Query.eqs(path2, value2))
    var actual: Query[Root] = path1 eqs value1 and path2 eqs value2
    actual should equal (expected)

    actual = (path1 eqs value1) and path2 eqs value2
    actual should equal (expected)

    actual = path1 eqs value1 and (path2 eqs value2)
    actual should equal (expected)

    actual = (path1 eqs value1) and (path2 eqs value2)
    actual should equal (expected)

    expected = Query.or(Query.eqs(path1, value1), Query.eqs(path2, value2))
    actual = path1 eqs value1 or path2 eqs value2
    actual should equal (expected)

    actual = (path1 eqs value1) or path2 eqs value2
    actual should equal (expected)

    actual = path1 eqs value1 or (path2 eqs value2)
    actual should equal (expected)

    actual = (path1 eqs value1) or (path2 eqs value2)
    actual should equal (expected)

  }

  it should "combine three or more dynamic relational queries with logical operators" in {
    val path1 = "foo"
    val value1 = 7
    val path2 = "bar"
    val value2 = 1.2
    val path3 = "baz"
    val value3 = "string"

    var expected: Query[Root] =
      Query.and(Query.eqs(path1, value1), Query.eqs(path2, value2), Query.eqs(path3, value3))
    var actual: Query[Root] = path1 eqs value1 and path2 eqs value2 and path3 eqs value3
    actual should equal (expected)

    actual = (path1 eqs value1) and path2 eqs value2 and path3 eqs value3
    actual should equal (expected)

    actual = (path1 eqs value1) and (path2 eqs value2) and path3 eqs value3
    actual should equal (expected)

    actual = (path1 eqs value1) and (path2 eqs value2) and (path3 eqs value3)
    actual should equal (expected)

    actual = (path1 eqs value1) and path2 eqs value2 and (path3 eqs value3)
    actual should equal (expected)

    actual = path1 eqs value1 and (path2 eqs value2) and path3 eqs value3
    actual should equal (expected)

    actual = path1 eqs value1 and (path2 eqs value2) and (path3 eqs value3)
    actual should equal (expected)

    actual = path1 eqs value1 and path2 eqs value2 and (path3 eqs value3)
    actual should equal (expected)

    expected = Query.or(Query.eqs(path1, value1), Query.eqs(path2, value2), Query.eqs(path3, value3))
    actual = path1 eqs value1 or path2 eqs value2 or path3 eqs value3
    actual should equal (expected)

    actual = (path1 eqs value1) or path2 eqs value2 or path3 eqs value3
    actual should equal (expected)

    actual = (path1 eqs value1) or (path2 eqs value2) or path3 eqs value3
    actual should equal (expected)

    actual = (path1 eqs value1) or (path2 eqs value2) or (path3 eqs value3)
    actual should equal (expected)

    actual = (path1 eqs value1) or path2 eqs value2 or (path3 eqs value3)
    actual should equal (expected)

    actual = path1 eqs value1 or (path2 eqs value2) or path3 eqs value3
    actual should equal (expected)

    actual = path1 eqs value1 or (path2 eqs value2) or (path3 eqs value3)
    actual should equal (expected)

    actual = path1 eqs value1 or path2 eqs value2 or (path3 eqs value3)
    actual should equal (expected)

    expected =
      Query.and(
        Query.eqs(path1, value1),
        Query.or(
          Query.eqs(path2, value2),
          Query.eqs(path3, value3)))
    actual = path1 eqs value1 and (path2 eqs value2 or path3 eqs value3)
    actual should equal (expected)

  }

  it should "build static relational queries that match the results of Query object methods" in {
    // import Root._
    // val value = 7

    // var expected = Query.eqs(prop1, value)
    // var actual: Query[Root] = prop1 eqs value // the type help will come naturally when calling repo method
    // actual should equal (expected)

    // expected = Query.neq(path, value)
    // actual = path neq value
    // actual should equal (expected)

    // expected = Query.lt(path, value)
    // actual = path lt value
    // actual should equal (expected)

    // expected = Query.lte(path, value)
    // actual = path lte value
    // actual should equal (expected)

    // expected = Query.gt(path, value)
    // actual = path gt value
    // actual should equal (expected)

    // expected = Query.gte(path, value)
    // actual = path gte value
    // actual should equal (expected)

  }

}
