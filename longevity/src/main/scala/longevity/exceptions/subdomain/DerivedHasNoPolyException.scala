package longevity.exceptions.subdomain

/** thrown on attempt to construct a
 * [[longevity.subdomain.Subdomain subdomain]] with a
 * [[longevity.subdomain.DerivedCType derived type]] that does not
 * have a corresponding [[longevity.subdomain.PolyCType poly type]],
 * or a [[longevity.subdomain.DerivedPType derived persistent type]] that
 * does not have a corresponding [[longevity.subdomain.PolyPType
 * poly persistent type]]
 */
class DerivedHasNoPolyException(typeName: String, isPType: Boolean)
extends SubdomainException(
  if (isPType) "DerivedPType $typeName does not have a corresponding PolyPType"
  else "DerivedCType $typeName does not have a corresponding PolyPype")
