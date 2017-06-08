package longevity.unit.manual.repo.poly

import longevity.unit.manual.repo.poly.ex1._
import longevity.unit.manual.repo.poly.ex2._

object ex4 {

import longevity.model.query.Query
import Member.queryDsl._

val query: Query[Member] =
  User.props.username eqs Username("u7") and
  Member.props.numCats gt 2
val queryResults = repo.queryToIterator(query)

}
