package longevity.integration.queries

import longevity.model.ModelEv
import longevity.model.ModelType

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

}
