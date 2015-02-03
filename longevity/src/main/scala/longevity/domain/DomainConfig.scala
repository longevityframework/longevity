package longevity.domain

import emblem._

/** a specification of a project's domain. contains a pool of all the [[EntityType entity types]] in the
 * domain, as well as all the [[Shorthand shorthands]] used by the entities. */
case class DomainConfig(
  entityTypePool: EntityTypePool,
  shorthandPool: ShorthandPool) {

  val entityEmblemPool: TypeKeyMap[HasEmblem, Emblem] = entityTypePool.mapValuesWiden[HasEmblem, Emblem] {
    new WideningTypeBoundFunction[Entity, HasEmblem, EntityType, Emblem] {
      def apply[TypeParam <: Entity](value1: EntityType[TypeParam]): Emblem[TypeParam] =
        value1.emblem
    }
  }

  // TODO: some way to express domain constraints, particularly those that span multiple entities
  // - figure a way for TestDataGenerator/RepoSpec to respect these
  // - figure a way to check constraints in entityMatchers/RepoSpec
  // - user-callable checkConstraint{,s} somewhere
  // - musette constraints to implement:
  //   - uri well-formedness
  //   - email well-formedness
  //   - markdown well-formedness?
  //   - the site of the blog authors and the site of the blog should be the same
  //   - the site of the blog post authors and the site of the blog should be the same
  //   - the site of the comment author and the site of the blog should be the same
  //   - the site of the wiki authors and the site of the wiki should be the same
  //   - the site of the wiki page authors and the site of the wiki should be the same

}
