package longevity.subdomain

import emblem.imports._
import emblem.WideningTypeBoundFunction

/** a specification of a subdomain of a project's domain. contains a pool of all the [[EntityType entity types]]
 * in the subdomain, as well as all the shorthands used by the entities.
 *
 * @param name the name of the subdomain
 * @param entityTypePool a complete set of the entity types within the subdomain
 * @param shorthandPool a complete set of the shorthands used by the bounded context
 */
class Subdomain(
  val name: String,
  val entityTypePool: EntityTypePool)(
  implicit val shorthandPool: ShorthandPool = ShorthandPool()) {

  val rootTypePool = RootTypePool(entityTypePool)

  // prohibit further creation of keys and indexs
  rootTypePool.values.foreach(_.register)

  /** a pool of emblems for the entities within the subdomain */
  private[longevity] val entityEmblemPool: TypeKeyMap[HasEmblem, Emblem] =
    entityTypePool.mapValuesWiden[HasEmblem, Emblem] {
      new WideningTypeBoundFunction[Entity, HasEmblem, EntityType, Emblem] {
        def apply[TypeParam <: Entity](value1: EntityType[TypeParam]): Emblem[TypeParam] =
          value1.emblem
      }
    }

  // TODO pt-87441928: some way to express domain constraints, particularly those that span multiple entities
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

object Subdomain {

  /** constructs a new subdomain.
   * 
   * @param name the name of the subdomain
   * @param entityTypePool a complete set of the entity types within the subdomain
   * @param shorthandPool a complete set of the shorthands used by the bounded context. defaults to empty
   */
  def apply(
    name: String,
    entityTypePool: EntityTypePool)(
    implicit shorthandPool: ShorthandPool = ShorthandPool()): Subdomain =
    new Subdomain(name, entityTypePool)(shorthandPool)

}
