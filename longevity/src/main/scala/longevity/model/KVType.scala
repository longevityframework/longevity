package longevity.model

import emblem.typeKey
import scala.reflect.runtime.universe.TypeTag

/** a type class for a key value
 *
 * @tparam M the domain model
 * @tparam P the persistent class
 * @tparam V the key value class
 */
abstract class KVType[M : ModelEv, P, V : TypeTag] {

  /** the evidence for the key value */
  implicit val kvEv = new KVEv[M, P, V](typeKey[V])

}
