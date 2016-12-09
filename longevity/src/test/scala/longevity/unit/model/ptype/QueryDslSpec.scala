package longevity.unit.model.ptype

import longevity.model.KeyVal
import longevity.model.PType
import longevity.model.PTypePool
import longevity.model.DomainModel
import longevity.model.ptype.Key
import longevity.model.query.Query
import longevity.model.query.Ascending
import longevity.model.query.Descending
import longevity.model.query.QueryFilter
import longevity.model.query.QueryOrderBy
import longevity.model.query.QuerySortExpr
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** sample domain for the QueryDslSpec */
object QueryDslSpec {

  private case class DslPersistent(path1: Int, path2: Double, path3: String, path4: AssociatedId) 

  private object DslPersistent extends PType[DslPersistent] {
    object props {
      val path1 = prop[Int]("path1")
      val path2 = prop[Double]("path2")
      val path3 = prop[String]("path3")
      val path4 = prop[AssociatedId]("path4")
    }
    val keySet = Set.empty[Key[DslPersistent]]
  }

  private case class AssociatedId(id: String) extends KeyVal[Associated]

  private case class Associated(id: AssociatedId)

  private object Associated extends PType[Associated] {
    object props {
      val id = prop[AssociatedId]("id")
    }
    val keySet = Set(key(props.id))
  }

}

/** unit tests for the proper construction of [[Query Queries]] using the [[QueryDsl]] */
class QueryDslSpec extends FlatSpec with GivenWhenThen with Matchers {
  import QueryDslSpec._
  private val domainModel = DomainModel(PTypePool(DslPersistent, Associated))
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

  it should "support order-by clauses" in {
    import DslPersistent._
    val value = 7
    var actual: Query[DslPersistent] = props.path1 eqs value orderBy props.path2

    val singleAscendingOrder = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy(Seq(
        QuerySortExpr(props.path2, Ascending))))

    actual = props.path1 eqs value orderBy props.path2
    actual should equal (singleAscendingOrder)

    actual = props.path1 eqs value orderBy (props.path2)
    actual should equal (singleAscendingOrder)

    actual = props.path1 eqs value orderBy props.path2.asc
    actual should equal (singleAscendingOrder)

    actual = props.path1 eqs value orderBy (props.path2.asc)
    actual should equal (singleAscendingOrder)

    val singleDescendingOrder = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy(Seq(
        QuerySortExpr(props.path2, Descending))))

    actual = props.path1 eqs value orderBy props.path2.desc
    actual should equal (singleDescendingOrder)

    actual = props.path1 eqs value orderBy (props.path2.desc)
    actual should equal (singleDescendingOrder)

    val doubleAscendingOrder = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy(Seq(
        QuerySortExpr(props.path2, Ascending),
        QuerySortExpr(props.path3, Ascending))))

    actual = props.path1 eqs value orderBy (props.path2, props.path3)
    actual should equal (doubleAscendingOrder)

    actual = props.path1 eqs value orderBy (props.path2.asc, props.path3.asc)
    actual should equal (doubleAscendingOrder)

    actual = props.path1 eqs value orderBy (props.path2.asc, props.path3)
    actual should equal (doubleAscendingOrder)

    import scala.language.postfixOps
    actual = props.path1 eqs value orderBy (props.path2 asc, props.path3 asc)
    actual should equal (doubleAscendingOrder)

  }

  it should "support offset clauses" in {
    import DslPersistent._
    val value = 7
    var actual: Query[DslPersistent] = null

    var expected = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy.empty,
      Some(10))

    actual = props.path1 eqs value offset 10
    actual should equal (expected)

    expected = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy(Seq(
        QuerySortExpr(props.path2, Ascending))),
      Some(10))

    actual = props.path1 eqs value orderBy props.path2 offset 10
    actual should equal (expected)

    expected = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy(Seq(
        QuerySortExpr(props.path2, Descending))),
      Some(10))

    actual = props.path1 eqs value orderBy props.path2.desc offset 10
    actual should equal (expected)

  }

  it should "support limit clauses" in {
    import DslPersistent._
    val value = 7
    var actual: Query[DslPersistent] = null

    var expected = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy.empty,
      None,
      Some(10))

    actual = props.path1 eqs value limit 10
    actual should equal (expected)

    expected = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy.empty,
      Some(10),
      Some(12))

    actual = props.path1 eqs value offset 10 limit 12
    actual should equal (expected)

    expected = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy(Seq(
        QuerySortExpr(props.path2, Ascending))),
      None,
      Some(10))

    actual = props.path1 eqs value orderBy props.path2 limit 10
    actual should equal (expected)

    expected = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy(Seq(
        QuerySortExpr(props.path2, Descending))),
      None,
      Some(10))

    actual = props.path1 eqs value orderBy props.path2.desc limit 10
    actual should equal (expected)

    expected = Query(
      QueryFilter.eqs(props.path1, value),
      QueryOrderBy(Seq(
        QuerySortExpr(props.path2, Descending))),
      Some(10),
      Some(12))

    actual = props.path1 eqs value orderBy props.path2.desc offset 10 limit 12
    actual should equal (expected)

  }

}
