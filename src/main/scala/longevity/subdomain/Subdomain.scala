package longevity.subdomain

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemPool
import emblem.emblematic.Emblematic
import emblem.emblematic.ExtractorFor
import emblem.emblematic.ExtractorPool
import emblem.emblematic.Union
import emblem.emblematic.UnionPool
import emblem.typeBound.TypeBoundFunction
import emblem.typeBound.TypeBoundPair
import emblem.typeBound.WideningTypeBoundFunction
import emblem.typeKey
import longevity.exceptions.subdomain.DerivedHasNoPolyException
import longevity.subdomain.entity.DerivedType
import longevity.subdomain.entity.Entity
import longevity.subdomain.entity.EntityType
import longevity.subdomain.entity.EntityTypePool
import longevity.subdomain.entity.PolyType
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.PolyPType

/** a specification of a subdomain of a project's domain. contains a pool of
 * all the [[entity.EntityType entity types]] in the subdomain, as well as all
 * the [[Shorthand shorthands]] used by the entities.
 *
 * @param name the name of the subdomain
 * @param pTypePool a complete set of the persistent types in the subdomain.
 * defaults to empty
 * @param entityTypePool a complete set of the entity types within the
 * subdomain. defaults to empty
 * @param shorthandPool a complete set of the shorthands used by the bounded
 * context. defaults to empty
 */
class Subdomain(
  val name: String,
  val pTypePool: PTypePool = PTypePool.empty,
  val entityTypePool: EntityTypePool = EntityTypePool.empty,
  val shorthandPool: ShorthandPool = ShorthandPool.empty) {

  private val extractorPool: ExtractorPool = {
    val shorthandToExtractor = new TypeBoundFunction[Any, ShorthandFor, ExtractorFor] {
      def apply[TypeParam](shorthand: ShorthandFor[TypeParam]): ExtractorFor[TypeParam] =
        shorthand.extractor
    }
    shorthandPool.mapValues(shorthandToExtractor)
  }

  private val emblemPool: EmblemPool = {
    val pTypesWithEmblems = pTypePool.filterValues(!_.isInstanceOf[PolyPType[_]])
    val pEmblems = pTypesWithEmblems.mapValuesWiden[Any, Emblem] {
      new WideningTypeBoundFunction[Persistent, Any, PType, Emblem] {
        def apply[TypeParam <: Persistent](pType: PType[TypeParam]): Emblem[TypeParam] =
          Emblem(pType.pTypeKey)
      }
    }

    val entityTypesWithEmblems = entityTypePool.filterValues(!_.isInstanceOf[PolyType[_]])
    val entityEmblems = entityTypesWithEmblems.mapValuesWiden[Any, Emblem] {
      new WideningTypeBoundFunction[Entity, Any, EntityType, Emblem] {
        def apply[TypeParam <: Entity](entityType: EntityType[TypeParam]): Emblem[TypeParam] =
          Emblem(entityType.entityTypeKey)
      }
    }

    pEmblems ++ entityEmblems
  }

  private val unionPool: UnionPool = entityUnions ++ pUnions

  private def entityUnions = {
    val polyTypes = entityTypePool.filterValues(_.isInstanceOf[PolyType[_]])

    type DerivedFrom[E <: Entity] = DerivedType[E, Poly] forSome { type Poly >: E <: Entity }

    val derivedTypes: TypeKeyMap[Entity, DerivedFrom] =
      entityTypePool.filterValues(_.isInstanceOf[DerivedFrom[_]]).asInstanceOf[TypeKeyMap[Entity, DerivedFrom]]

    type DerivedList[E <: Entity] = List[Emblem[_ <: E]]
    val baseToDerivedsMap: TypeKeyMap[Entity, DerivedList] =
      derivedTypes.values.foldLeft(TypeKeyMap[Entity, DerivedList]) { (map, derivedType) =>

        def fromDerivedType[E <: Entity, Poly >: E <: Entity](derivedType: DerivedType[E, Poly])
        : TypeKeyMap[Entity, DerivedList] = {
          implicit val polyTypeKey: TypeKey[Poly] = derivedType.polyType.entityTypeKey
          if (!polyTypes.contains[Poly]) {
            throw new DerivedHasNoPolyException(polyTypeKey.name, isPType = false)
          }

          val emblem = emblemPool(derivedType.entityTypeKey)
          val derivedList = map.getOrElse(List.empty)
          map +[Poly] (emblem :: derivedList)
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

  private def pUnions = {
    val polyTypes = pTypePool.filterValues(_.isInstanceOf[PolyPType[_]])

    type DerivedFrom[P <: Persistent] = DerivedPType[P, Poly] forSome { type Poly >: P <: Persistent }

    val derivedTypes: TypeKeyMap[Persistent, DerivedFrom] =
      pTypePool.filterValues(_.isInstanceOf[DerivedFrom[_]]).asInstanceOf[TypeKeyMap[Persistent, DerivedFrom]]

    type DerivedList[P <: Persistent] = List[Emblem[_ <: P]]
    val baseToDerivedsMap: TypeKeyMap[Persistent, DerivedList] =
      derivedTypes.values.foldLeft(TypeKeyMap[Persistent, DerivedList]) { (map, derivedType) =>

        def fromDerivedType[P <: Persistent, Poly >: P <: Persistent](derivedPType: DerivedPType[P, Poly])
        : TypeKeyMap[Persistent, DerivedList] = {
          implicit val polyTypeKey = derivedPType.polyPType.pTypeKey

          if (!polyTypes.contains[Poly]) {
            throw new DerivedHasNoPolyException(polyTypeKey.name, isPType = false)
          }

          val emblem = emblemPool(derivedPType.pTypeKey)
          val derivedList = map.getOrElse(List.empty)
          map +[Poly] (emblem :: derivedList)
        }

        fromDerivedType(derivedType)
      }

    polyTypes.mapValuesWiden[Any, Union] {
      new WideningTypeBoundFunction[Persistent, Any, PType, Union] {
        def apply[TypeParam <: Persistent](pType: PType[TypeParam]): Union[TypeParam] = {
          val constituents = baseToDerivedsMap(pType.pTypeKey)
          Union[TypeParam](constituents: _*)(pType.pTypeKey)
        }
      }
    }
  }

  private[longevity] val emblematic = Emblematic(extractorPool, emblemPool, unionPool)

  pTypePool.values.foreach(_.registerSubdomain(this))

}

object Subdomain {

  /** constructs a new subdomain.
   * 
   * @param name the name of the subdomain
   * @param pTypePool a complete set of the persistent types in the subdomain. defaults to empty
   * @param entityTypePool a complete set of the entity types within the subdomain. defaults to empty
   * @param shorthandPool a complete set of the shorthands used by the bounded context. defaults to empty
   */
  def apply(
    name: String,
    pTypePool: PTypePool = PTypePool.empty,
    entityTypePool: EntityTypePool = EntityTypePool.empty,
    shorthandPool: ShorthandPool = ShorthandPool.empty)
  : Subdomain =
    new Subdomain(name, pTypePool, entityTypePool, shorthandPool)

}
