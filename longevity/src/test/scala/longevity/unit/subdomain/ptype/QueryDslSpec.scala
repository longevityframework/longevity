package longevity.unit.subdomain.ptype

import longevity.subdomain.KeyVal
import longevity.subdomain.Subdomain
import longevity.subdomain.Persistent
import longevity.subdomain.PTypePool
import longevity.subdomain.query.Query
import longevity.subdomain.query.QueryFilter
import longevity.subdomain.PType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** sample domain for the QueryDslSpec */
object QueryDslSpec {

  private case class DslPersistent(path1: Int, path2: Double, path3: String, path4: AssociatedId)
  extends Persistent

  private object DslPersistent extends PType[DslPersistent] {
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
  extends KeyVal[Associated, AssociatedId]

  private case class Associated(id: AssociatedId) extends Persistent

  private object Associated extends PType[Associated] {
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
  private val subdomain = Subdomain("QueryDslSpec", PTypePool(DslPersistent, Associated))
  private val dsl = DslPersistent.queryDsl
  import dsl._

  behavior of "QueryDsl"

  it should "build relational queries that match the results of Query object methods" in {
    import DslPersistent._
    val value = 7

    var expected: Query[DslPersistent] = Query(QueryFilter.eqs(props.path1, value))
    var actual: Query[DslPersistent] = props.path1 eqs value
    actual should equal (expected)

    expected = Query(QueryFilter.neq(props.path1, value))
    actual = props.path1 neq value
    actual should equal (expected)

    expected = Query(QueryFilter.lt(props.path1, value))
    actual = props.path1 lt value
    actual should equal (expected)

    expected = Query(QueryFilter.lte(props.path1, value))
    actual = props.path1 lte value
    actual should equal (expected)

    expected = Query(QueryFilter.gt(props.path1, value))
    actual = props.path1 gt value
    actual should equal (expected)

    expected = Query(QueryFilter.gte(props.path1, value))
    actual = props.path1 gte value
    actual should equal (expected)

  }

  it should "combine two relational queries with logical operators" in {
    import DslPersistent._
    val value1 = 7
    val value2 = 1.2

    var expected: Query[DslPersistent] =
      Query(
        QueryFilter.and(
          QueryFilter.eqs(props.path1, value1),
          QueryFilter.eqs(props.path2, value2)))
    var actual: Query[DslPersistent] = props.path1 eqs value1 and props.path2 eqs value2
    actual should equal (expected)

    actual = (props.path1 eqs value1) and props.path2 eqs value2
    actual should equal (expected)

    actual = props.path1 eqs value1 and (props.path2 eqs value2)
    actual should equal (expected)

    actual = (props.path1 eqs value1) and (props.path2 eqs value2)
    actual should equal (expected)

    expected =
      Query(
        QueryFilter.or(
          QueryFilter.eqs(props.path1, value1),
          QueryFilter.eqs(props.path2, value2)))
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
    import DslPersistent._
    val value1 = 7
    val value2 = 1.2
    val value3 = "string"

    var expected: Query[DslPersistent] =
      Query(
        QueryFilter.and(
          QueryFilter.and(
            QueryFilter.eqs(props.path1, value1),
            QueryFilter.eqs(props.path2, value2)),
          QueryFilter.eqs(props.path3, value3)))
    var actual: Query[DslPersistent] =
        props.path1 eqs value1 and props.path2 eqs value2 and props.path3 eqs value3
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
      Query(
        QueryFilter.or(
          QueryFilter.or(
            QueryFilter.eqs(props.path1, value1),
            QueryFilter.eqs(props.path2, value2)),
          QueryFilter.eqs(props.path3, value3)))
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
      Query(
        QueryFilter.and(
          QueryFilter.eqs(props.path1, value1),
          QueryFilter.or(
            QueryFilter.eqs(props.path2, value2),
            QueryFilter.eqs(props.path3, value3))))
    actual = props.path1 eqs value1 and (props.path2 eqs value2 or props.path3 eqs value3)
    actual should equal (expected)

  }

}
