package longevity.unit.subdomain.root

import org.scalatest._
import longevity.exceptions.subdomain.root.PropNotOrderedException
import longevity.subdomain._
import longevity.subdomain.root._

/** sample domain for the QueryDslSpec */
object QueryDslSpec {

  private case class DslRoot(path1: Int, path2: Double, path3: String, path4: Assoc[Associated])
  extends Root

  private object DslRoot extends RootType[DslRoot] {
    val path1 = prop[Int]("path1")
    val path2 = prop[Double]("path2")
    val path3 = prop[String]("path3")
    val path4 = prop[Assoc[Associated]]("path4")
  }

  private case class Associated() extends Root

  private object Associated extends RootType[Associated]

}

/** unit tests for the proper construction of [[Query Queries]] using the [[QueryDsl]] */
class QueryDslSpec extends FlatSpec with GivenWhenThen with Matchers {
  import QueryDslSpec._
  private val dsl = new QueryDsl[DslRoot]
  import dsl._

  behavior of "QueryDsl"

  it should "refuse to build a static ordering query on a non-ordered prop" in {
    import DslRoot._
    val assoc = Assoc(new Associated())

    (path4 eqs assoc): Query[DslRoot]
    (path4 neq assoc): Query[DslRoot]

    intercept[PropNotOrderedException] {
      (path4 lt assoc): Query[DslRoot]
    }
    intercept[PropNotOrderedException] {
      (path4 lte assoc): Query[DslRoot]
    }
    intercept[PropNotOrderedException] {
      (path4 gt assoc): Query[DslRoot]
    }
    intercept[PropNotOrderedException] {
      (path4 gte assoc): Query[DslRoot]
    }
  }

  it should "build static relational queries that match the results of Query object methods" in {
    import DslRoot._
    val value = 7

    var expected: Query[DslRoot] = Query.eqs(path1, value)
    var actual: Query[DslRoot] = path1 eqs value
    actual should equal (expected)

    expected = Query.neq(path1, value)
    actual = path1 neq value
    actual should equal (expected)

    expected = Query.lt(path1, value)
    actual = path1 lt value
    actual should equal (expected)

    expected = Query.lte(path1, value)
    actual = path1 lte value
    actual should equal (expected)

    expected = Query.gt(path1, value)
    actual = path1 gt value
    actual should equal (expected)

    expected = Query.gte(path1, value)
    actual = path1 gte value
    actual should equal (expected)

  }

  it should "combine two static relational queries with logical operators" in {
    import DslRoot._
    val value1 = 7
    val value2 = 1.2

    var expected: Query[DslRoot] = Query.and(Query.eqs(path1, value1), Query.eqs(path2, value2))
    var actual: Query[DslRoot] = path1 eqs value1 and path2 eqs value2
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

  it should "combine three or more static relational queries with logical operators" in {
    import DslRoot._
    val value1 = 7
    val value2 = 1.2
    val value3 = "string"

    var expected: Query[DslRoot] =
      Query.and(
        Query.and(
          Query.eqs(path1, value1),
          Query.eqs(path2, value2)),
        Query.eqs(path3, value3))
    var actual: Query[DslRoot] = path1 eqs value1 and path2 eqs value2 and path3 eqs value3
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

    expected =
      Query.or(
        Query.or(
          Query.eqs(path1, value1),
          Query.eqs(path2, value2)),
        Query.eqs(path3, value3))
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

}
