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
 * val extractor = Extractor[Uri, String]
 * extractor.domainTypeKey should equal (typeKey[Uri])
 * extractor.rangeTypeKey should equal (typeKey[String])
 * extractor.apply(Uri("someUri")) should equal ("someUri")
 * extractor.unapply("someUri") should equal (Some(Uri("someUri")))
 * }}}
 *
 * @tparam Domain the domain type
 * @tparam Range the range type
 * @param apply a function to convert from domain to range
 * @param inverse a function to convert from range to domain. throws exception if
 * there is no element in the domain that corresponds to the element in the range
 */
case class Extractor[Domain : TypeKey, Range : TypeKey] private[emblem] (
  val apply: (Domain) => Range,
  val inverse: (Range) => Domain
) {

  /** a function to convert from range to domain. returns `None` if there is no element in the domain that
   * corresponds to the element in the range
   */
  def unapply(range: Range): Option[Domain] = try {
    Some(inverse(range))
  } catch {
    case e: Exception => None
  }

  /** a [[TypeKey]] for the domain type */
  lazy val domainTypeKey: TypeKey[Domain] = implicitly[TypeKey[Domain]]

  /** a [[TypeKey]] for the range type */
  lazy val rangeTypeKey: TypeKey[Range] = implicitly[TypeKey[Range]]

  override def toString = s"Extractor[${rangeTypeKey.tpe}, ${domainTypeKey.tpe}]"

}

object Extractor {

  /** creates and returns an [[Extractor]] for the specified types `Range` and `Domain`. `Range` must be a
   * stable case class with single a parameter list.
   *
   * @tparam Domain the domain type
   * @tparam Range the range type
   * @throws emblem.exceptions.GeneratorException when `Domain` is not a stable case class with a single
   * parameter list, or when `Range` does not match the parameter of the `Domain` constructor
   */
  def apply[Domain : TypeKey, Range : TypeKey]: Extractor[Domain, Range] =
    new ExtractorFactory[Domain, Range].generate

}
