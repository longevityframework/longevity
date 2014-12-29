package emblem

import scala.reflect.runtime.universe.TypeTag
import stringUtil._

class EmblemProp[T <: HasEmblem, U](
  val name: String,
  val get: (T) => U,
  val set: (T, U) => T
)(
  implicit val typeTag: TypeTag[U]
) {

  override def toString: String = s"$name: ${typeName(typeTag)}"

}
