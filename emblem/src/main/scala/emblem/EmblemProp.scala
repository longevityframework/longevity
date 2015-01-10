package emblem

import scala.reflect.runtime.universe.TypeRef
import scala.reflect.runtime.universe.TypeTag
import stringUtil._

case class EmblemProp[T <: HasEmblem : TypeKey, U : TypeKey](
  val name: String,
  val get: (T) => U,
  val set: (T, U) => T
) {

  lazy val typeKey: TypeKey[U] = implicitly[TypeKey[U]]

  override def toString: String = s"$name: ${typeKey.tpe}"

}
