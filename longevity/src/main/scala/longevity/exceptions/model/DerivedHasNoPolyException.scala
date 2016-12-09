package longevity.exceptions.model

/** thrown on attempt to construct a
 * [[longevity.model.DomainModel domainModel]] with a
 * [[longevity.model.DerivedCType derived type]] that does not
 * have a corresponding [[longevity.model.PolyCType poly type]],
 * or a [[longevity.model.DerivedPType derived persistent type]] that
 * does not have a corresponding [[longevity.model.PolyPType
 * poly persistent type]]
 */
class DerivedHasNoPolyException(typeName: String, isPType: Boolean)
extends DomainModelException(
  if (isPType) "DerivedPType $typeName does not have a corresponding PolyPType"
  else "DerivedCType $typeName does not have a corresponding PolyPype")
