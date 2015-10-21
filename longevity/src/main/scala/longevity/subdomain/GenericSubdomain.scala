package longevity.subdomain

object GenericSubdomain {

  /** a generic subdomain. really just another name for a [Subdomain].
   *
   * @param name the name of the core domain
   * @param entityTypePool a complete set of the entity types within the core domain
   * @param shorthandPool a complete set of the shorthands used by the bounded context. defaults to empty
   */
  def apply(
    name: String,
    entityTypePool: EntityTypePool,
    shorthandPool: ShorthandPool = ShorthandPool()): GenericSubdomain = 
    Subdomain(name, entityTypePool, shorthandPool)

}
