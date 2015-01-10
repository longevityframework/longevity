package emblem

import scala.reflect.runtime.universe._

case class Shorthand[Long : TypeKey, Short : TypeKey](
  val shorten: (Long) => Short,
  val unshorten: (Short) => Long
) {
  lazy val longTypeKey: TypeKey[Long] = implicitly[TypeKey[Long]]
  lazy val shortTypeKey: TypeKey[Short] = implicitly[TypeKey[Short]]
  override def toString = s"Shorthand[${longTypeKey.tpe}, ${shortTypeKey.tpe}]"
}
