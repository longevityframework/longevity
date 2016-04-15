package emblem

/** describes a supertype that can be resolved down in to other types found in
 * an [[Emblematic]].
 */
case class Union[A](
  typeKey: TypeKey[A],
  constituents: Set[TypeKey[_ <: A]]) {

}
