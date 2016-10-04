package longevity.unit.subdomain.ptype

import longevity.subdomain.KeyVal
import longevity.subdomain.Subdomain
import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.Query
import longevity.subdomain.ptype.RootType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** sample domain for the QueryDslSpec */
object QueryDslSpec {

  private case class DslRoot(path1: Int, path2: Double, path3: String, path4: AssociatedId)
  extends Root

  private object DslRoot extends RootType[DslRoot] {
    object props {
      val path1 = prop[Int]("path1")
      val path2 = prop[Double]("path2")
      val path3 = prop[String]("path3")
      val path4 = prop[AssociatedId]("path4")
    }
    object keys {
    }
  }

  private case class AssociatedId(id: String)
  extends KeyVal[Associated, AssociatedId](Associated.keys.id)

  private case class Associated(id: AssociatedId) extends Root

  private object Associated extends RootType[Associated] {
    object props {
      val id = prop[AssociatedId]("id")
    }
    object keys {
      val id = key(props.id)
    }
  }

}

/** unit tests for the proper construction of [[Query Queries]] using the [[QueryDsl]] */
class QueryDslSpec extends FlatSpec with GivenWhenThen with Matchers {
  import QueryDslSpec._
  private val subdomain = Subdomain("QueryDslSpec", PTypePool(DslRoot, Associated))
  private val dsl = DslRoot.queryDsl
  import dsl._

  behavior of "QueryDsl"

  it should "build relational queries that match the results of Query object methods" in {
    import DslRoot._
    val value = 7

    var expected: Query[DslRoot] = Query.eqs(props.path1, value)
    var actual: Query[DslRoot] = props.path1 eqs value
    actual should equal (expected)

    expected = Query.neq(props.path1, value)
    actual = props.path1 neq value
    actual should equal (expected)

    expected = Query.lt(props.path1, value)
    actual = props.path1 lt value
    actual should equal (expected)

    expected = Query.lte(props.path1, value)
    actual = props.path1 lte value
    actual should equal (expected)

    expected = Query.gt(props.path1, value)
    actual = props.path1 gt value
    actual should equal (expected)

    expected = Query.gte(props.path1, value)
    actual = props.path1 gte value
    actual should equal (expected)

  }

  it should "combine two relational queries with logical operators" in {
    import DslRoot._
    val value1 = 7
    val value2 = 1.2

    var expected: Query[DslRoot] = Query.and(Query.eqs(props.path1, value1), Query.eqs(props.path2, value2))
    var actual: Query[DslRoot] = props.path1 eqs value1 and props.path2 eqs value2
    actual should equal (expected)

    actual = (props.path1 eqs value1) and props.path2 eqs value2
    actual should equal (expected)

    actual = props.path1 eqs value1 and (props.path2 eqs value2)
    actual should equal (expected)

    actual = (props.path1 eqs value1) and (props.path2 eqs value2)
    actual should equal (expected)

    expected = Query.or(Query.eqs(props.path1, value1), Query.eqs(props.path2, value2))
    actual = props.path1 eqs value1 or props.path2 eqs value2
    actual should equal (expected)

    actual = (props.path1 eqs value1) or props.path2 eqs value2
    actual should equal (expected)

    actual = props.path1 eqs value1 or (props.path2 eqs value2)
    actual should equal (expected)

    actual = (props.path1 eqs value1) or (props.path2 eqs value2)
    actual should equal (expected)

  }

  it should "combine three or more relational queries with logical operators" in {
    import DslRoot._
    val value1 = 7
    val value2 = 1.2
    val value3 = "string"

    var expected: Query[DslRoot] =
      Query.and(
        Query.and(
          Query.eqs(props.path1, value1),
          Query.eqs(props.path2, value2)),
        Query.eqs(props.path3, value3))
    var actual: Query[DslRoot] = props.path1 eqs value1 and props.path2 eqs value2 and props.path3 eqs value3
    actual should equal (expected)

    actual = (props.path1 eqs value1) and props.path2 eqs value2 and props.path3 eqs value3
    actual should equal (expected)

    actual = (props.path1 eqs value1) and (props.path2 eqs value2) and props.path3 eqs value3
    actual should equal (expected)

    actual = (props.path1 eqs value1) and (props.path2 eqs value2) and (props.path3 eqs value3)
    actual should equal (expected)

    actual = (props.path1 eqs value1) and props.path2 eqs value2 and (props.path3 eqs value3)
    actual should equal (expected)

    actual = props.path1 eqs value1 and (props.path2 eqs value2) and props.path3 eqs value3
    actual should equal (expected)

    actual = props.path1 eqs value1 and (props.path2 eqs value2) and (props.path3 eqs value3)
    actual should equal (expected)

    actual = props.path1 eqs value1 and props.path2 eqs value2 and (props.path3 eqs value3)
    actual should equal (expected)

    expected =
      Query.or(
        Query.or(
          Query.eqs(props.path1, value1),
          Query.eqs(props.path2, value2)),
        Query.eqs(props.path3, value3))
    actual = props.path1 eqs value1 or props.path2 eqs value2 or props.path3 eqs value3
    actual should equal (expected)

    actual = (props.path1 eqs value1) or props.path2 eqs value2 or props.path3 eqs value3
    actual should equal (expected)

    actual = (props.path1 eqs value1) or (props.path2 eqs value2) or props.path3 eqs value3
    actual should equal (expected)

    actual = (props.path1 eqs value1) or (props.path2 eqs value2) or (props.path3 eqs value3)
    actual should equal (expected)

    actual = (props.path1 eqs value1) or props.path2 eqs value2 or (props.path3 eqs value3)
    actual should equal (expected)

    actual = props.path1 eqs value1 or (props.path2 eqs value2) or props.path3 eqs value3
    actual should equal (expected)

    actual = props.path1 eqs value1 or (props.path2 eqs value2) or (props.path3 eqs value3)
    actual should equal (expected)

    actual = props.path1 eqs value1 or props.path2 eqs value2 or (props.path3 eqs value3)
    actual should equal (expected)

    expected =
      Query.and(
        Query.eqs(props.path1, value1),
        Query.or(
          Query.eqs(props.path2, value2),
          Query.eqs(props.path3, value3)))
    actual = props.path1 eqs value1 and (props.path2 eqs value2 or props.path3 eqs value3)
    actual should equal (expected)

  }

}
