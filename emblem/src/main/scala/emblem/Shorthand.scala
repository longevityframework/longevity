package emblem

/** describes a relation (one-to-one mapping) between two types, `Actual` and `Abbreviated`. The "actual" type is
 * typically a richer type, such as a case class with a single parameter, and an abbreviated value for the
 * type, such as a string. Provides functions for mapping between the actual and abbreviated types, as well as
 * [[TypeKey type keys]] for the two types.
 *
 * @tparam Actual the actual type
 * @tparam Abbreviated the abbreviated type
 * @param abbreviate a function to convert from actual to abbreviated
 * @param unabbreviate a function to convert from short to actual
 */
case class Shorthand[Actual : TypeKey, Abbreviated : TypeKey] private[emblem] (
  val abbreviate: (Actual) => Abbreviated,
  val unabbreviate: (Abbreviated) => Actual
) {

  /** a [[TypeKey]] for the actualhand type */
  lazy val actualTypeKey: TypeKey[Actual] = implicitly[TypeKey[Actual]]

  /** a [[TypeKey]] for the abbreviatedhand type */
  lazy val abbreviatedTypeKey: TypeKey[Abbreviated] = implicitly[TypeKey[Abbreviated]]

  override def toString = s"Shorthand[${actualTypeKey.tpe}, ${abbreviatedTypeKey.tpe}]"

}
