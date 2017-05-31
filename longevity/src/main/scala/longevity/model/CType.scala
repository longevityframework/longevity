package longevity.model

import emblem.TypeKey
import emblem.typeKey
import scala.reflect.runtime.universe.TypeTag

/** a type class for a persistent component */
abstract class CType[C : TypeTag] {

  private[longevity] val cTypeKey: TypeKey[C] = typeKey[C]

  override def toString = s"CType[${cTypeKey.name}]"

}

/** contains a factory method for creating `CTypes` */
object CType {

  /** create and return a `CType` for type `C` */
  def apply[C : TypeTag] = new CType[C] {}

}
