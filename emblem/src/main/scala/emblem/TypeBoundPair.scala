package emblem

case class TypeBoundPair[TypeBound, A[_ <: TypeBound], B[_ <:TypeBound]] (
  _1: A[TypeBound],
  _2: B[TypeBound]) {

  
}
