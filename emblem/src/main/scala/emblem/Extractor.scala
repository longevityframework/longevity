package emblem

import factories.ExtractorFactory

/** describes an injective relation between two types, `Domain` and `Range`. every value in the domain maps
 * to a unique value in the range. however, not all values in the range map back onto the domain.
 * the `Range` type is typically a richer type, such as a case class with a single parameter, and the `Domain`
 * type is the type wrapped by the case class.
 *
 * provides functions for mapping between the range and domain types, as well as
 * [[TypeKey type keys]] for the two types. for example:
 *
 * {{{
 * case class Uri(uri: String)
 * val extractor = Extractor[String, Uri]
 * }}}
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

object Extractor {

  /** creates and returns an [[Extractor]] for the specified types `Range` and `Domain`. `Range` must be a
   * stable case class with single a parameter list.
   *
   * @tparam Domain the domain type
   * @tparam Range the range type
   * @throws emblem.exceptions.GeneratorException when `Range` is not a stable case class with a single
   * parameter list
   */
  def apply[Domain : TypeKey, Range : TypeKey]: Extractor[Domain, Range] =
    new ExtractorFactory[Domain, Range].generate

}
