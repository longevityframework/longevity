package emblem

import scala.reflect.runtime.universe._

// TODO unit tests
// TODO scaladoc

case class ShorthandPool(val shorthands: Seq[Shorthand[_, _]]) {

  private lazy val longTypeKeyMap: Map[TypeKey[_], Shorthand[_, _]] =
    shorthands.map(s => (s.longTypeKey -> s)).toMap

  // TODO: this is unused should i keep it?
  private lazy val shortTypeKeyMap: Map[TypeKey[_], Shorthand[_, _]] =
    shorthands.map(s => (s.shortTypeKey -> s)).toMap

  def longTypeKeyToShorthand[Long](key: TypeKey[Long]): Option[Shorthand[Long, _]] =
    longTypeKeyMap.get(key).asInstanceOf[Option[Shorthand[Long, _]]]

  def shortTypeKeyToShorthand[Short](key: TypeKey[Short]): Option[Shorthand[Short, _]] =
    shortTypeKeyMap.get(key).asInstanceOf[Option[Shorthand[Short, _]]]

}

object ShorthandPool {

  def apply(): ShorthandPool = ShorthandPool(Seq())

}
