package longevity.subdomain.realized

import emblem.TypeKey

// TODO RM

// TODO better name for this class. also, where does it really belong? maybe
// hold off on that second part for now as this will probably continue to
// change as i add support for multi-prop keys

/** resolves a type down to a basic type. the incoming type is either a
 * basic type itself, or an emblem that boils down to a single basic
 * type. (ie, the emblem has a single property, and that property is either a
 * basic type, or could be reolved to a basic type by the same process.)
 */
private[longevity] case class BasicResolver[A, B](
  incomingTypeKey: TypeKey[A],
  basicTypeKey: TypeKey[B],
  resolve: (A) => B,
  ordering: Ordering[A])
