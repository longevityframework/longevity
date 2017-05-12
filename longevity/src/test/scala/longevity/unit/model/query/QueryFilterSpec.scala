package longevity.unit.model.query

import longevity.integration.model.basics.Basics
import longevity.integration.model.basics.BasicsId
import longevity.integration.model.basics.DomainModel
import longevity.model.query.QueryFilter
import org.joda.time.DateTime
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for [[QueryFilter.matches]] */
class QueryFilterSpec extends FlatSpec with GivenWhenThen with Matchers {
  private val rpt = DomainModel.modelType.realizedPTypes(Basics)

  val degenerate = Basics(BasicsId("id"), false, '0', 0D, 0F, 0, 0L, "", DateTime.now)
  def withInt(i: Int) = degenerate.copy(int = i)
  def withIntAndDouble(i: Int, d: Double) = degenerate.copy(int = i, double = d)

  behavior of "QueryFilter.matches"

  it should "return correct results for simple relational query filters" in {
    var filter = QueryFilter.eqs(Basics.props.int, 7)
    QueryFilter.matches(filter, withInt(6), rpt) should equal (false)
    QueryFilter.matches(filter, withInt(7), rpt) should equal (true)
    QueryFilter.matches(filter, withInt(8), rpt) should equal (false)

    filter = QueryFilter.neq(Basics.props.int, 7)
    QueryFilter.matches(filter, withInt(6), rpt) should equal (true)
    QueryFilter.matches(filter, withInt(7), rpt) should equal (false)
    QueryFilter.matches(filter, withInt(8), rpt) should equal (true)

    filter = QueryFilter.lt(Basics.props.int, 7)
    QueryFilter.matches(filter, withInt(6), rpt) should equal (true)
    QueryFilter.matches(filter, withInt(7), rpt) should equal (false)
    QueryFilter.matches(filter, withInt(8), rpt) should equal (false)

    filter = QueryFilter.lte(Basics.props.int, 7)
    QueryFilter.matches(filter, withInt(6), rpt) should equal (true)
    QueryFilter.matches(filter, withInt(7), rpt) should equal (true)
    QueryFilter.matches(filter, withInt(8), rpt) should equal (false)

    filter = QueryFilter.gt(Basics.props.int, 7)
    QueryFilter.matches(filter, withInt(6), rpt) should equal (false)
    QueryFilter.matches(filter, withInt(7), rpt) should equal (false)
    QueryFilter.matches(filter, withInt(8), rpt) should equal (true)

    filter = QueryFilter.gte(Basics.props.int, 7)
    QueryFilter.matches(filter, withInt(6), rpt) should equal (false)
    QueryFilter.matches(filter, withInt(7), rpt) should equal (true)
    QueryFilter.matches(filter, withInt(8), rpt) should equal (true)

  }

  it should "return correct results for query filters with logical operators" in {

    var filter = QueryFilter.and(
      QueryFilter.eqs(Basics.props.int, 7),
      QueryFilter.eqs(Basics.props.double, 1.2d))

    QueryFilter.matches(filter, withIntAndDouble(6, 1.1d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(7, 1.1d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(8, 1.1d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(6, 1.2d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(7, 1.2d), rpt) should equal (true)
    QueryFilter.matches(filter, withIntAndDouble(8, 1.2d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(6, 1.3d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(7, 1.3d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(8, 1.3d), rpt) should equal (false)

    filter = QueryFilter.or(
      QueryFilter.eqs(Basics.props.int, 7),
      QueryFilter.eqs(Basics.props.double, 1.2d))

    QueryFilter.matches(filter, withIntAndDouble(6, 1.1d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(7, 1.1d), rpt) should equal (true)
    QueryFilter.matches(filter, withIntAndDouble(8, 1.1d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(6, 1.2d), rpt) should equal (true)
    QueryFilter.matches(filter, withIntAndDouble(7, 1.2d), rpt) should equal (true)
    QueryFilter.matches(filter, withIntAndDouble(8, 1.2d), rpt) should equal (true)
    QueryFilter.matches(filter, withIntAndDouble(6, 1.3d), rpt) should equal (false)
    QueryFilter.matches(filter, withIntAndDouble(7, 1.3d), rpt) should equal (true)
    QueryFilter.matches(filter, withIntAndDouble(8, 1.3d), rpt) should equal (false)

  }

}
