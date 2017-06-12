package longevity.emblem.exceptions

import typekey.TypeKey

/** an exception thrown when the user attempts to use a
 * [[emblem.emblematic.Union]] on an instance that is properly typed for the
 * `Union`, but whose type was not declared as a constituent when the `Union`
 * was constructed
 *
 * @tparam A the union type
 * @param instance the instance whose type is not a constituent of the union
 * @param unionTypeKey a [[TypeKey]] for the union type
 */
private[longevity] class InstanceNotInUnionException[A](
  val instance: A,
  val unionTypeKey: TypeKey[A])
extends EmblemException(
  s"attempt to use a union on an instance whose type is not a constituent type of the union")
