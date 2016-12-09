package longevity.exceptions.model

import longevity.model.ptype.Prop

/** thrown on attempt to create a property with a type that longevity does not
 * now support, such as:
 *
 * - properties with collection types
 * - properties with types that (recursively) contain members with collection or polymorphic types
 * - properties with paths that contain collections
 * - properties with paths that terminate with a [[longevity.model.PolyCType polymorphic type]].
 */
class UnsupportedPropTypeException(val prop: Prop[_, _])
extends SubdomainException(
  s"longevity doesn't currently support properties such as `$prop` in `${prop.pTypeKey.name}`. " +
  s"for details see http://longevityframework.github.io/longevity/manual/ptype/properties.html")

