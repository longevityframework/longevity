package longevity.exceptions.subdomain

/** thrown on attempt to construct a
 * [[longevity.subdomain.Subdomain]] with a
 * [[longevity.subdomain.embeddable.DerivedType]] that does not have a corresponding
 * [[longevity.subdomain.embeddable.PolyType]], or a
 * [[longevity.subdomain.ptype.DerivedPType]] that does not have a corresponding
 * [[longevity.subdomain.ptype.PolyPType]]
 */
class DerivedHasNoPolyException(typeName: String, isPType: Boolean)
extends SubdomainException(
  if (isPType) "DerivedPType $typeName does not have a corresponding PolyPType"
  else "DerivedType $typeName does not have a corresponding PolyPype")
