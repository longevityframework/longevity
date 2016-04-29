package longevity.subdomain

import emblem.Emblem
import emblem.EmblemPool
import emblem.Emblematic
import emblem.ExtractorFor
import emblem.ExtractorPool
import emblem.TypeBoundFunction
import emblem.TypeBoundPair
import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.Union
import emblem.UnionPool
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

  private val extractorPool: ExtractorPool = {
    val shorthandToExtractor = new TypeBoundFunction[Any, ShorthandFor, ExtractorFor] {
      def apply[TypeParam](shorthand: ShorthandFor[TypeParam]): ExtractorFor[TypeParam] =
        shorthand.extractor
    }
    shorthandPool.mapValues(shorthandToExtractor)
  }

  private val emblemPool: EmblemPool = {
    val entityTypesWithEmblems = entityTypePool.filterNot(isValPolyType)
    entityTypesWithEmblems.mapValuesWiden[Any, Emblem] {
      new WideningTypeBoundFunction[Entity, Any, EntityType, Emblem] {
        def apply[TypeParam <: Entity](entityType: EntityType[TypeParam]): Emblem[TypeParam] =
          Emblem(entityType.entityTypeKey)
      }
    }
  }

  private val unionPool: UnionPool =  {
    val polyTypes = entityTypePool.filter(isValPolyType)

    type DerivedT[D <: Entity] = DerivedType[B, D] forSome { type B >: D <: Entity }

    val derivedTypes: TypeKeyMap[Entity, DerivedT] =
      entityTypePool.filter(isValDerivedType).asInstanceOf[TypeKeyMap[Entity, DerivedT]]

    type DerivedList[E <: Entity] = List[Emblem[_ <: E]]
    val baseToDerivedsMap: TypeKeyMap[Entity, DerivedList] =
      derivedTypes.values.foldLeft(TypeKeyMap[Entity, DerivedList]) { (map, derivedType) =>
        def fromDerivedType[B <: Entity, D <: B](derivedType: DerivedType[B, D])
        : TypeKeyMap[Entity, DerivedList] = {
          val derivedTypeKey = derivedType.entityTypeKey
          implicit val polyTypeKey = derivedType.polyType.entityTypeKey

          if (!polyTypes.contains(polyTypeKey)) {
            // TODO: new exception for derived type with poly type not in subdomain
            throw new RuntimeException
          }

          val emblem = emblemPool(derivedTypeKey)
          val derivedList = map.getOrElse[B](List.empty)

          map +[B] (emblem :: derivedList)
        }

        fromDerivedType(derivedType)
      }

    polyTypes.mapValuesWiden[Any, Union] {
      new WideningTypeBoundFunction[Entity, Any, EntityType, Union] {
        def apply[TypeParam <: Entity](entityType: EntityType[TypeParam]): Union[TypeParam] = {
          val constituents = baseToDerivedsMap(entityType.entityTypeKey)
          Union[TypeParam](constituents: _*)(entityType.entityTypeKey)
        }
      }
    }
  }

  private def isValPolyType(pair: TypeBoundPair[Entity, TypeKey, EntityType, _ <: Entity]): Boolean = {
    pair._2.isInstanceOf[PolyType[_ <: Entity]]
  }

  private def isValDerivedType(pair: TypeBoundPair[Entity, TypeKey, EntityType, _ <: Entity]): Boolean = {
    pair._2.isInstanceOf[DerivedType[_, _]]
  }

  private[longevity] val emblematic = Emblematic(extractorPool, emblemPool, unionPool)

  pTypePool.values.foreach(_.registerEmblematic(emblematic))

  // TODO pt-#115456079: some way to express domain constraints that span multiple aggregates
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
