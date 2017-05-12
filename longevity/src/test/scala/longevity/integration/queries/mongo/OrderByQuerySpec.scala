package longevity.integration.queries.mongo

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.test.QuerySpec
import longevity.integration.model.basics._
import longevity.integration.queries.queryTestsExecutionContext

class OrderByQuerySpec extends QuerySpec[DomainModel, Basics](
  new LongevityContext[DomainModel](TestLongevityConfigs.mongoConfig)) {

  lazy val sample = randomP

  import Basics.queryDsl._
  import Basics.props

  behavior of "MongoRepo.retrieveByQuery"

  // ive commented out some of these because test really takes a long time

  it should "produce expected ordered results for filterAll queries" in {
    // exerciseQuery(filterAll orderBy (props.boolean.asc  ), true)
    // exerciseQuery(filterAll orderBy (props.boolean.desc ), true)
    exerciseQuery(filterAll orderBy (props.char.asc     ), true)
    exerciseQuery(filterAll orderBy (props.char.desc    ), true)
    // exerciseQuery(filterAll orderBy (props.dateTime.asc ), true)
    // exerciseQuery(filterAll orderBy (props.dateTime.desc), true)
    // exerciseQuery(filterAll orderBy (props.double.asc   ), true)
    // exerciseQuery(filterAll orderBy (props.double.desc  ), true)
    // exerciseQuery(filterAll orderBy (props.float.asc    ), true)
    // exerciseQuery(filterAll orderBy (props.float.desc   ), true)
    // exerciseQuery(filterAll orderBy (props.int.asc      ), true)
    // exerciseQuery(filterAll orderBy (props.int.desc     ), true)
    // exerciseQuery(filterAll orderBy (props.long.asc     ), true)
    // exerciseQuery(filterAll orderBy (props.long.desc    ), true)
    // exerciseQuery(filterAll orderBy (props.string.asc   ), true)
    // exerciseQuery(filterAll orderBy (props.string.desc  ), true)

    exerciseQuery(filterAll orderBy (props.int.asc,  props.float.asc), true)
    // exerciseQuery(filterAll orderBy (props.int.asc,  props.float.desc), true)
    // exerciseQuery(filterAll orderBy (props.int.desc, props.float.asc), true)
    // exerciseQuery(filterAll orderBy (props.int.desc, props.float.desc), true)
  }

  it should "produce expected ordered results for simple equality queries" in {
    // exerciseQuery(props.boolean eqs sample.boolean orderBy (props.int.asc), true)
    // exerciseQuery(props.boolean eqs sample.boolean orderBy (props.int.desc), true)
    exerciseQuery(props.boolean neq sample.boolean orderBy (props.int.asc), true)
    // exerciseQuery(props.boolean neq sample.boolean orderBy (props.int.desc), true)
  }

  it should "produce expected ordered results for simple ordering queries" in {
    exerciseQuery(props.int lt sample.int orderBy (props.int.asc   ), true)
    // exerciseQuery(props.int lt sample.int orderBy (props.int.desc  ), true)
    // exerciseQuery(props.int lt sample.int orderBy (props.float.asc ), true)
    // exerciseQuery(props.int lt sample.int orderBy (props.float.desc), true)
  }

}
