package longevity.integration.queries

import longevity.model.ModelType
import longevity.model.PType
import longevity.model.PTypePool

/** we use a special model type for limit/offset tests to prevent interference
 * from other tests.
 *
 * please note that if for some reason afterAll fails to run, then future runs
 * of this test will fail until someone manually cleans out (or deletes) the
 * limit_offset table.
 */
package object offsetLimit {

  trait DomainModel

  object DomainModel {
    implicit object modelType extends ModelType[DomainModel](PTypePool(OffsetLimit))
  }

  case class OffsetLimit(i: Int, j: Int)

  object OffsetLimit extends PType[OffsetLimit] {
    object props {
      val i = prop[Int]("i")
      val j = prop[Int]("j")
    }
    val keySet = emptyKeySet
    override val indexSet = Set(index(props.i), index(props.j))
  }

}
