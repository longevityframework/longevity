package longevity.model.ptype

import scala.reflect.runtime.universe.TypeTag
import typekey.typeKey

/** a property for this persistent type. properties map to underlying members
 * within the persistent object, at any depth.
 *
 * properties can be used to define [[Key keys]] and [[Index indexes]], as well
 * as for building [[longevity.model.query.Query queries]]. a property can
 * descend from the root into child entities at any depth.
 *
 * at present, a property cannot pass through any collections or terminate with
 * a [[longevity.model.PolyCType polymorphic embeddable]].
 * violations will cause an exception to be thrown on
 * [[longevity.model.ModelType ModelType construction]].
 * 
 * @param path a dot-separated path of the persistent object member descending
 * from the root
 */
class Prop[P : TypeTag, A : TypeTag](val path: String) {

  private[longevity] val pTypeKey = typeKey[P]

  private[longevity] val propTypeKey = typeKey[A]

  override def toString: String = path

  private[longevity] def debug = s"Prop[${pTypeKey.name},${propTypeKey.name}]($path)"

  override def hashCode = pTypeKey.hashCode + path.hashCode

  override def equals(that: Any) = {
    if (that.isInstanceOf[Prop[_, _]]) {
      val thatProp = that.asInstanceOf[Prop[_, _]]
      path == thatProp.path && pTypeKey == thatProp.pTypeKey && propTypeKey == thatProp.propTypeKey
    } else {
      false
    }
  }

}
