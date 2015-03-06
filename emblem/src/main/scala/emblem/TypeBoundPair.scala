package emblem

/** mimics a pair found in an ordinary map, but preserves the type parameter equality in the two elements
 * of the pair
 *
 * @tparam TypeBound the upper bound on the type parameters passed into the A and B types of the two elements
 * of this pair
 * @tparam A the parameterized type of the first element of this pair
 * @tparam B the parameterized type of the second element of this pair
 * @tparam TypeParam the type param binding both the A and B types of the two elements of this pair
 * @param _1 the first element of this type bound pair
 * @param _2 the second element of this type bound pair
 */
case class TypeBoundPair[
  TypeBound,
  A[_ <: TypeBound],
  B[_ <: TypeBound],
  TypeParam <: TypeBound] (
  _1: A[TypeParam],
  _2: B[TypeParam])
