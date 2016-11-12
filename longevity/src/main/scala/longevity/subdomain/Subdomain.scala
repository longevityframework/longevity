package longevity.subdomain

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeBound.TypeBoundMap
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemPool
import emblem.emblematic.Emblematic
import emblem.emblematic.Union
import emblem.typeBound.TypeBoundFunction
import longevity.exceptions.subdomain.DerivedHasNoPolyException
import longevity.subdomain.realized.RealizedPType

/** a specification of a subdomain of a project's domain. contains a pool of
 * all the [[PType persistent types]] in the subdomain, as well as
 * all the [[CType component types]].
 *
 * @param name the name of the subdomain
 * @param pTypePool a complete set of the persistent types in the subdomain.
 * defaults to empty
 * @param eTypePool a complete set of the component types within the
 * subdomain. defaults to empty
 */
class Subdomain(
  val name: String,
  val pTypePool: PTypePool = PTypePool.empty,
  val eTypePool: CTypePool = CTypePool.empty) {

  private[longevity] val emblematic = Emblematic(emblemPool, unionPool)

  private[longevity] val realizedPTypes: TypeBoundMap[Any, PType, RealizedPType] = {
    pTypePool.values.foldLeft(TypeBoundMap[Any, PType, RealizedPType]()) { (acc, pType) =>
      def addPair[P](pType: PType[P]) = {
        pType.validateKeysAndIndexes()
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
    pTypesWithEmblems.mapValues[Emblem] {
      new TypeBoundFunction[Any, PType, Emblem] {
        def apply[TypeParam](pType: PType[TypeParam]): Emblem[TypeParam] =
          Emblem(pType.pTypeKey)
      }
    }
  }

  private def entityEmblems = {
    val eTypesWithEmblems = eTypePool.filterValues(!_.isInstanceOf[PolyCType[_]])
    eTypesWithEmblems.mapValues[Emblem] {
      new TypeBoundFunction[Any, CType, Emblem] {
        def apply[TypeParam](eType: CType[TypeParam]): Emblem[TypeParam] =
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
    val polyTypes = eTypePool.filterValues(_.isInstanceOf[PolyCType[_]])

    type DerivedFrom[E] = DerivedCType[E, Poly] forSome { type Poly >: E }

    val derivedTypes =
      eTypePool
        .filterValues(_.isInstanceOf[DerivedFrom[_]])
        .asInstanceOf[TypeKeyMap[Any, DerivedFrom]]

    type DerivedList[E] = List[Emblem[_ <: E]]
    val baseToDerivedsMap: TypeKeyMap[Any, DerivedList] =
      derivedTypes.values.foldLeft(TypeKeyMap[Any, DerivedList]) { (map, derivedType) =>

        def fromDerivedCType[E, Poly >: E](derivedType: DerivedCType[E, Poly]): TypeKeyMap[Any, DerivedList] = {
          implicit val polyTypeKey: TypeKey[Poly] = derivedType.polyTypeKey
          if (!polyTypes.contains[Poly]) {
            throw new DerivedHasNoPolyException(polyTypeKey.name, isPType = false)
          }

          val emblem = emblemPool(derivedType.eTypeKey)
          val derivedList = map.getOrElse(List.empty)
          map.+[Poly](emblem :: derivedList)
        }

        fromDerivedCType(derivedType)
      }

    polyTypes.mapValues[Union] {
      new TypeBoundFunction[Any, CType, Union] {
        def apply[TypeParam](eType: CType[TypeParam]): Union[TypeParam] = {
          val constituents = baseToDerivedsMap(eType.eTypeKey)
          Union[TypeParam](constituents: _*)(eType.eTypeKey)
        }
      }
    }
  }

  private def pUnions = {
    val polyTypes = pTypePool.filterValues(_.isInstanceOf[PolyPType[_]])

    type DerivedFrom[P] = DerivedPType[P, Poly] forSome { type Poly >: P }

    val derivedTypes: TypeKeyMap[Any, DerivedFrom] =
      pTypePool.filterValues(_.isInstanceOf[DerivedFrom[_]]).asInstanceOf[TypeKeyMap[Any, DerivedFrom]]

    type DerivedList[P] = List[Emblem[_ <: P]]
    val baseToDerivedsMap: TypeKeyMap[Any, DerivedList] =
      derivedTypes.values.foldLeft(TypeKeyMap[Any, DerivedList]) { (map, derivedType) =>

        def fromDerivedPType[P, Poly >: P](derivedPType: DerivedPType[P, Poly])
        : TypeKeyMap[Any, DerivedList] = {
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

    polyTypes.mapValues[Union] {
      new TypeBoundFunction[Any, PType, Union] {
        def apply[TypeParam](pType: PType[TypeParam]): Union[TypeParam] = {
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
   *
   * @param pTypePool a complete set of the persistent types in the subdomain.
   * defaults to empty
   *
   * @param eTypePool a complete set of the component types within the
   * subdomain. defaults to empty
   *
   * @throws longevity.exceptions.subdomain.NoSuchPropPathException if a
   * [[longevity.subdomain.ptype.Prop property]] in any of the subdomain's
   * [[PType persistent types]] has a property path that does not exist in the
   * persistent type being reflected on
   * 
   * @throws longevity.exceptions.subdomain.UnsupportedPropTypeException
   * if a [[longevity.subdomain.ptype.Prop property]] in any of the subdomain's
   * [[PType persistent types]] has a property path
   * that contains a collection or a [[PolyCType
   * polymorphic type]]
   * 
   * @throws longevity.exceptions.subdomain.PropTypeException if a
   * [[longevity.subdomain.ptype.Prop property]] in any of the subdomain's
   * [[PType persistent types]] has a property whose
   * specified type does not match the type of the corresponding path in the
   * persistent being reflected on
   */
  def apply(
    name: String,
    pTypePool: PTypePool = PTypePool.empty,
    eTypePool: CTypePool = CTypePool.empty)
  : Subdomain =
    new Subdomain(name, pTypePool, eTypePool)

}
