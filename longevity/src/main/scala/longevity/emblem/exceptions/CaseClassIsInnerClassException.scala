package longevity.emblem.exceptions

import typekey.TypeKey

/** this exception is thrown when a user tries to generate an
 * [[emblem.emblematic.Emblem Emblem]] for an inner case class. an inner type is
 * a type that belongs to an instance variable, such as `B` in the following
 * example:
 *
 * {{{
 * class A { case class B(i: Int) }
 * val a1 = new A
 * val a2 = new A
 * import scala.reflect.runtime.universe.typeOf
 * typeOf[a1.B] =:= typeOf[a2.B] // evaluates to false
 * }}}
 *
 * Emblem generation for inner classes may be supported in the future.
 */
private[longevity] class CaseClassIsInnerClassException(key: TypeKey[_])
extends GeneratorException(key, s"generation for inner case classes currently not supported: $key")
