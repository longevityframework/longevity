package longevity.subdomain

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeBound.TypeBoundMap
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemPool
import emblem.emblematic.Emblematic
import emblem.emblematic.Union
import emblem.typeBound.WideningTypeBoundFunction
import longevity.exceptions.subdomain.DerivedHasNoPolyException
import longevity.subdomain.realized.RealizedPType

/** a specification of a subdomain of a project's domain. contains a pool of
 * all the [[ptype.PType persistent types]] in the subdomain, as well as
 * all the [[embeddable.EType embeddable types]].
 *
 * @param name the name of the subdomain
 * @param pTypePool a complete set of the persistent types in the subdomain.
 * defaults to empty
 * @param eTypePool a complete set of the entity types within the
 * subdomain. defaults to empty
 */
class Subdomain(
  val name: String,
  val pTypePool: PTypePool = PTypePool.empty,
  val eTypePool: ETypePool = ETypePool.empty) {

  private[longevity] val emblematic = Emblematic(emblemPool, unionPool)

  private[longevity] val realizedPTypes: TypeBoundMap[Persistent, PType, RealizedPType] = {
    pTypePool.values.foldLeft(TypeBoundMap[Persistent, PType, RealizedPType]()) { (acc, pType) =>
      def addPair[P <: Persistent](pType: PType[P]) = {
        val polyPTypeOpt = pType match {
          case derivedPType: DerivedPType[P, _] =>
            if (!pTypePool.contains(derivedPType.polyPTypeKey)) {
              throw new DerivedHasNoPolyException(derivedPType.polyPTypeKey.name, isPType = true)
            }
            Some(pTypePool(derivedPType.polyPTypeKey))
          case _ =>
            None
        }
        val realizedPType = new RealizedPType[P](pType, polyPTypeOpt, emblematic)
        acc + (pType -> realizedPType)
      }
      addPair(pType)
    }
  }

  private def emblemPool = pEmblems ++ entityEmblems ++ keyValEmblems

  private def pEmblems = {
    val pTypesWithEmblems = pTypePool.filterValues(!_.isInstanceOf[PolyPType[_]])
    pTypesWithEmblems.mapValuesWiden[Any, Emblem] {
      new WideningTypeBoundFunction[Persistent, Any, PType, Emblem] {
        def apply[TypeParam <: Persistent](pType: PType[TypeParam]): Emblem[TypeParam] =
          Emblem(pType.pTypeKey)
      }
    }
  }

  private def entityEmblems = {
    val eTypesWithEmblems = eTypePool.filterValues(!_.isInstanceOf[PolyEType[_]])
    eTypesWithEmblems.mapValuesWiden[Any, Emblem] {
      new WideningTypeBoundFunction[Embeddable, Any, EType, Emblem] {
        def apply[TypeParam <: Embeddable](eType: EType[TypeParam]): Emblem[TypeParam] =
          Emblem(eType.eTypeKey)
      }
    }
  }

  private def keyValEmblems = pTypePool.values.foldLeft(EmblemPool()) { (acc, pType) =>
    val keyValEmblems: EmblemPool = pType.keySet.foldLeft(EmblemPool()) { (acc, key) =>
      def addToPool[A](emblem: Emblem[A]) = acc + (emblem.typeKey -> emblem)
      addToPool(key.keyValEmblem)
    }
    acc ++ keyValEmblems
  }

  private def unionPool = entityUnions ++ pUnions

  private def entityUnions = {
    val polyTypes = eTypePool.filterValues(_.isInstanceOf[PolyEType[_]])

    type DerivedFrom[E <: Embeddable] = DerivedEType[E, Poly] forSome { type Poly >: E <: Embeddable }

    val derivedTypes =
      eTypePool
        .filterValues(_.isInstanceOf[DerivedFrom[_]])
        .asInstanceOf[TypeKeyMap[Embeddable, DerivedFrom]]

    type DerivedList[E <: Embeddable] = List[Emblem[_ <: E]]
    val baseToDerivedsMap: TypeKeyMap[Embeddable, DerivedList] =
      derivedTypes.values.foldLeft(TypeKeyMap[Embeddable, DerivedList]) { (map, derivedType) =>

        def fromDerivedEType[E <: Embeddable, Poly >: E <: Embeddable](derivedType: DerivedEType[E, Poly])
        : TypeKeyMap[Embeddable, DerivedList] = {
          implicit val polyTypeKey: TypeKey[Poly] = derivedType.polyTypeKey
          if (!polyTypes.contains[Poly]) {
            throw new DerivedHasNoPolyException(polyTypeKey.name, isPType = false)
          }

          val emblem = emblemPool(derivedType.eTypeKey)
          val derivedList = map.getOrElse(List.empty)
          map.+[Poly](emblem :: derivedList)
        }

        fromDerivedEType(derivedType)
      }

    polyTypes.mapValuesWiden[Any, Union] {
      new WideningTypeBoundFunction[Embeddable, Any, EType, Union] {
        def apply[TypeParam <: Embeddable](eType: EType[TypeParam]): Union[TypeParam] = {
          val constituents = baseToDerivedsMap(eType.eTypeKey)
          Union[TypeParam](constituents: _*)(eType.eTypeKey)
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

        def fromDerivedPType[P <: Persistent, Poly >: P <: Persistent](derivedPType: DerivedPType[P, Poly])
        : TypeKeyMap[Persistent, DerivedList] = {
          implicit val polyTypeKey = derivedPType.polyPTypeKey

          if (!polyTypes.contains[Poly]) {
            throw new DerivedHasNoPolyException(polyTypeKey.name, isPType = false)
          }

          val emblem = emblemPool(derivedPType.pTypeKey)
          val derivedList = map.getOrElse(List.empty)
          map.+[Poly] (emblem :: derivedList)
        }

        fromDerivedPType(derivedType)
      }

    polyTypes.mapValuesWiden[Any, Union] {
      new WideningTypeBoundFunction[Persistent, Any, PType, Union] {
        def apply[TypeParam <: Persistent](pType: PType[TypeParam]): Union[TypeParam] = {
          val constituents = baseToDerivedsMap.getOrElse(List[Emblem[TypeParam]]())(pType.pTypeKey)
          Union[TypeParam](constituents: _*)(pType.pTypeKey)
        }
      }
    }
  }

}

/** provides a factory method for constructing [[Subdomain subdomains]] */
object Subdomain {

  /** constructs a new subdomain
   * 
   * @param name the name of the subdomain
   * @param pTypePool a complete set of the persistent types in the subdomain. defaults to empty
   * @param eTypePool a complete set of the embeddable types within the subdomain. defaults to empty
   *
   * @throws longevity.exceptions.subdomain.NoSuchPropPathException if a
   * [[longevity.subdomain.ptype.Prop property]] in any of the subdomain's
   * [[longevity.subdomain.ptype.PType persistent types]] has a property path
   * that does not exist in the [[longevity.subdomain.persistent.Persistent
   * persistent]] being reflected on
   * 
   * @throws longevity.exceptions.subdomain.UnsupportedPropTypeException
   * if a [[longevity.subdomain.ptype.Prop property]] in any of the subdomain's
   * [[longevity.subdomain.ptype.PType persistent types]] has a property path
   * that contains a collection or a [[longevity.subdomain.embeddable.PolyEType
   * polymorphic type]]
   * 
   * @throws longevity.exceptions.subdomain.PropTypeException if a
   * [[longevity.subdomain.ptype.Prop property]] in any of the subdomain's
   * [[longevity.subdomain.ptype.PType persistent types]] has a property whose
   * specified type does not match the type of the corresponding path in the
   * [[longevity.subdomain.persistent.Persistent persistent]] being reflected on
   */
  def apply(
    name: String,
    pTypePool: PTypePool = PTypePool.empty,
    eTypePool: ETypePool = ETypePool.empty)
  : Subdomain =
    new Subdomain(name, pTypePool, eTypePool)

}
