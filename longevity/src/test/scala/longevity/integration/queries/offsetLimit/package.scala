package longevity.integration.queries

import longevity.model.ModelEv
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.ptype.Prop

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
    implicit object modelType extends ModelType[DomainModel](Seq(OffsetLimit))
    private[offsetLimit] implicit object modelEv extends ModelEv[DomainModel]
  }

  case class OffsetLimit(i: Int, j: Int)

  object OffsetLimit extends PType[DomainModel, OffsetLimit] {
    object props {
      object i extends Prop[OffsetLimit, Int]("i")
      object j extends Prop[OffsetLimit, Int]("j")
    }
    override val indexSet = Set(index(props.i), index(props.j))
  }

}
