package emblem

// TODO rename extractorFor to Extractor.apply[D,R]
// TODO clean up docs
/** describes a relation (one-to-one mapping) between two types, `Range` and `Domain`. The "range" type is
 * typically a richer type, such as a case class with a single parameter, and an domain value for the
 * type, such as a string. Provides functions for mapping between the range and domain types, as well as
 * [[TypeKey type keys]] for the two types.
 *
 * @tparam Range the range type
 * @tparam Domain the domain type
 * @param apply a function to convert from domain to range
 * @param unapply a function to convert from range to domain @throws
 */
case class Extractor[Domain : TypeKey, Range : TypeKey] private[emblem] (
  val apply: (Domain) => Range,
  val unapply: (Range) => Domain
) {

  /** a [[TypeKey]] for the range type */
  lazy val rangeTypeKey: TypeKey[Range] = implicitly[TypeKey[Range]]

  /** a [[TypeKey]] for the domain type */
  lazy val domainTypeKey: TypeKey[Domain] = implicitly[TypeKey[Domain]]

  override def toString = s"Extractor[${rangeTypeKey.tpe}, ${domainTypeKey.tpe}]"

}
