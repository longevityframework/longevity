package longevity.subdomain

import emblem.Emblem
import emblem.Emblematic
import emblem.ExtractorFor
import emblem.ExtractorPool
import emblem.TypeBoundFunction
import emblem.TypeKeyMap
import emblem.WideningTypeBoundFunction
import longevity.subdomain.ptype.PTypePool

/** a specification of a subdomain of a project's domain. contains a pool of
 * all the [[EntityType entity types]] in the subdomain, as well as all the
 * [[Shorthand shorthands]] used by the entities.
 *
 * @param name the name of the subdomain
 * @param entityTypePool a complete set of the entity types within the subdomain
 * @param shorthandPool a complete set of the shorthands used by the bounded context
 */
class Subdomain(
  val name: String,
  val entityTypePool: EntityTypePool)(
  implicit val shorthandPool: ShorthandPool = ShorthandPool()) {

  /** a pool of the persistent types in the subdomain */
  val pTypePool = PTypePool(entityTypePool)

  private[longevity] val emblematic = Emblematic(extractorPool, emblemPool)

  private def extractorPool: ExtractorPool = {
    val shorthandToExtractor = new TypeBoundFunction[Any, ShorthandFor, ExtractorFor] {
      def apply[TypeParam](shorthand: ShorthandFor[TypeParam]): ExtractorFor[TypeParam] =
        shorthand.extractor
    }
    shorthandPool.mapValues(shorthandToExtractor)
  }

  private def emblemPool: TypeKeyMap[Any, Emblem] =
    entityTypePool.mapValuesWiden[Any, Emblem] {
      new WideningTypeBoundFunction[Entity, Any, EntityType, Emblem] {
        def apply[TypeParam <: Entity](value1: EntityType[TypeParam]): Emblem[TypeParam] =
          value1.emblem
      }
    }

  // TODO pt-#115456079: some way to express domain constraints that span multiple entities
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
