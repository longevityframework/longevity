package longevity.emblem.exceptions

import typekey.TypeKey

/** an exception indicating you broke the contract of one of the
 * [[emblem.emblematic.Emblem]] or [[emblem.emblematic.Union]] factory methods
 */
private[longevity] class ConstituentPropTypeMismatchException(
  val unionType: TypeKey[_],
  val propName: String,
  val propType: TypeKey[_],
  val constituentType: TypeKey[_],
  val constituentPropType: TypeKey[_])
extends GeneratorException(
  unionType,
  s"a union constituent must have properties that exactly match the corresponding property " +
  s"in the union. your union property $propName has type ${propType.name}, but constituent " +
  s"${constituentType.name} has property type ${constituentPropType.name}")
