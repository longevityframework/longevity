package emblem

/** describes a supertype that can be resolved down in to other types found in
 * an [[Emblematic]]
 *
 * TODO params
 */
case class Union[A](
  typeKey: TypeKey[A],
  constituents: Set[TypeKey[_ <: A]]) {

  // TODO scaladoc
  def typeKeyForInstance(a: A): Option[TypeKey[_ <: A]] = {
    // TODO: think about when this might fail
    typeKeyForName(a.getClass.getSimpleName)
  }
 
  // TODO scaladoc
  def typeKeyForName(name: String): Option[TypeKey[_ <: A]] =
    constituentKeysByName.get(name)

  private val constituentKeysByName: Map[String, TypeKey[_ <: A]] =
    constituents.map(c => (c.name, c)).toMap
    
}

object Union {

  /** TODO */
  def apply[A : TypeKey](constituents: TypeKey[_ <: A]*): Union[A] =
    Union[A](typeKey[A], constituents.toSet)

}
