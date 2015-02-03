package emblem

import scala.language.higherKinds

/** A function with one type parameter, where both the argument and the return value are types with a single
 * type parameter, bound to the type parameter of the function.
 * 
 * @tparam TypeBound the type bound to use for the argument and return value types
 * @tparam Arg the argument type
 * @tparam ReturnVal the return value type
 */
trait TypeBoundFunction[TypeBound, Arg[_ <: TypeBound], ReturnVal[_ <: TypeBound]]
extends WideningTypeBoundFunction[TypeBound, TypeBound, Arg, ReturnVal]
