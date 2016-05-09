package emblem.typeBound

import scala.language.higherKinds

/** like a [[TypeBoundFunction]], except that the type bound for the return value is wider than the type bound
 * for the argument. This is useful for `mapWiden` and `mapValuesWiden` methods in [[TypeKeyMap]] and
 * [[TypeBoundMap]] that return a map with a wider type bound than the original.
 * 
 * @tparam TypeBound the type bound to use for the argument type
 * @tparam WiderTypeBound the type bound to use for the return value type
 * @tparam Arg the argument type
 * @tparam ReturnVal the return value type
 * @see TypeBoundFunction
 */
trait WideningTypeBoundFunction[
  TypeBound,
  WiderTypeBound >: TypeBound,
  Arg[_ <: TypeBound],
  ReturnVal[_ <: WiderTypeBound]] {

  def apply[
    TypeParam <: TypeBound](
    value: Arg[TypeParam])
  : ReturnVal[TypeParam]

}
