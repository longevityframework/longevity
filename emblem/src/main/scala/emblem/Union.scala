package emblem

/** describes a supertype that can be resolved down in to other types found in
 * an [[Emblematic]].
 *
 * currently, discrimination between the constituent types depends on the simple
 * name of the type. this may lead to problems if two constituent types have the
 * same simple name, if the types involved are companion objects, or if the
 * types are otherwise complex types, such as `Foo with Bar`. for now, it's best
 * to declare your constituent types in discrete classes. if this ever becomes a
 * problematic limitation, we will look into making this more robust.
 *
 * @tparam A the supertype
 * @param typeKey the type key for the supertype
 * @param constituents the type keys for the constituent types
 */
case class Union[A](
  typeKey: TypeKey[A],
  constituents: Set[TypeKey[_ <: A]]) {

  /** returns the type key for the constituent that the instance matches,
   * wrapped in a `Some`, if the instance matches one of the constituent types.
   * otherwise returns `None`.
   *
   * @param a the instance to find the consituent type for
   * @return the type key for the constituent type
   */
  def typeKeyForInstance(a: A): Option[TypeKey[_ <: A]] = {
    typeKeyForName(a.getClass.getSimpleName)
  }
 
  /** returns the type key for the constituent with the given name,
   * wrapped in a `Some`, if the name matches one of the constituent types.
   * otherwise returns `None`.
   *
   * @param a the instance to find the consituent type for
   * @return the type key for the constituent type
   */
  def typeKeyForName(name: String): Option[TypeKey[_ <: A]] =
    constituentKeysByName.get(name)

  private val constituentKeysByName: Map[String, TypeKey[_ <: A]] =
    constituents.map(c => (c.name, c)).toMap
    
}

object Union {

  /** constructs a `Union` from the constituent types */
  def apply[A : TypeKey](constituents: TypeKey[_ <: A]*): Union[A] =
    Union[A](typeKey[A], constituents.toSet)

}
