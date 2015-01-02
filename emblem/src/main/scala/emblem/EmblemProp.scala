package emblem

import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.TypeTag
import stringUtil._

class EmblemProp[T <: HasEmblem : TypeTag, U : TypeTag](
  val name: String,
  val get: (T) => U,
  val set: (T, U) => T
) {

  lazy val typeTag: TypeTag[U] = scala.reflect.runtime.universe.typeTag[U]

  override def toString: String = s"$name: ${typeTag.tpe.toString}"

}
