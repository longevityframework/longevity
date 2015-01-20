package emblem

/** describes a relation (one-to-one mapping) between two types, `Long` and `Short`. The "long" type is
 * typically a richer type, such as a case class with a single parameter, and a shorthand value for the
 * type, such as a string. Provides functions for mapping between the long and short types, as well as
 * [[TypeKey type keys]] for the two types.
 *
 * @tparam Long the longhand type
 * @tparam Short the shorthand type
 * @param shorten a function to convert from longhand to shorthand
 * @param unshorten a function to convert from shorthand to longhand
 */
case class Shorthand[Long : TypeKey, Short : TypeKey] private[emblem] (
  val shorten: (Long) => Short,
  val unshorten: (Short) => Long
) {

  /** a [[TypeKey]] for the longhand type */
  lazy val longTypeKey: TypeKey[Long] = implicitly[TypeKey[Long]]

  /** a [[TypeKey]] for the shorthand type */
  lazy val shortTypeKey: TypeKey[Short] = implicitly[TypeKey[Short]]

  override def toString = s"Shorthand[${longTypeKey.tpe}, ${shortTypeKey.tpe}]"

}
