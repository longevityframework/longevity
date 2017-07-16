package longevity.model

import org.scalacheck.Arbitrary
import scala.annotation.implicitNotFound
import scala.reflect.runtime.universe.TypeTag
import shapeless.Generic
import shapeless.LabelledGeneric
import typekey.TypeKey

/** evidence for a persistent class
 *
 * this evidence is provided in the persistent type (`PType`) for the same persistent class. because
 * the `PType` is typically the companion object for your persistent class, this evidence should be
 * available when needed.
 *
 * @tparam M the domain model
 * @tparam P the persistent class
 *
 * @see longevity.model.annotations.persistent
 * @see longevity.model.PType
 */
@implicitNotFound("${P} is not a persistent type in domain model ${M}")
class PEv[M, P] (
  implicit tag: TypeTag[P],
  private[longevity] val generic: Generic[P],
  private[longevity] val labelled: LabelledGeneric[P],
  private[longevity] val arbitrary: Arbitrary[P]) {

  private[longevity] val key = TypeKey[P](tag)

}
