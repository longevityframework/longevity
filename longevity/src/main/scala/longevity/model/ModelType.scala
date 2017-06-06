package longevity.model

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemPool
import emblem.emblematic.Emblematic
import emblem.emblematic.Union
import emblem.typeBound.TypeBoundFunction
import emblem.typeBound.TypeBoundMap
import longevity.exceptions.model.DerivedHasNoPolyException
import longevity.exceptions.model.DuplicateCTypesException
import longevity.exceptions.model.DuplicateKVTypesException
import longevity.exceptions.model.DuplicatePTypesException
import longevity.model.realized.RealizedPType

/** a description of a project's domain model. contains a pool of all the [[PType persistent types]]
 * in the model, as well as all the [[CType component types]].
 *
 * the model class `M` is intended to be a phantom class, available in the root package of the
 * package structure where the domain model elements are defined. the `ModelType` is intended to be
 * implicitly available within the domain model's companion object. this comes for free if you use
 * the `longevity.model.annotations.domainModel` annotation on your model class `M`.
 *
 * @tparam M the model
 *
 * @constructor constructs a model type
 *
 * @param pTypes a complete sequence of the persistent types in the domain model.
 *
 * @param cTypes a complete sequence of the component types within the domain model. defaults to
 * empty
 *
 * @param kvTypes a complete sequence of the key value types within the domain model. defaults to
 * empty
 *
 * throws longevity.exceptions.model.DuplicatePTypesException when two `PTypes` refer tothe same
 * persistent class
 * 
 * throws longevity.exceptions.model.DuplicateCTypesException when two `CTypes` refer to the same
 * component class
 *
 * throws longevity.exceptions.model.DuplicateKVTypesException when two `KVTypes` refer to the same
 * component class
 *
 * @see longevity.model.annotations.domainModel
 */
@throws[DuplicatePTypesException]("when two PTypes refer to the same persistent class")
@throws[DuplicateCTypesException]("when two CTypes refer to the same component class")
@throws[DuplicateKVTypesException]("when two KVTypes refer to the same key value class")
class ModelType[M](
  pTypes: Seq[PType[M, _]],
  cTypes: Seq[CType[M, _]] = Nil,
  kvTypes: Seq[KVType[M, _, _]] = Nil) {

  private[longevity] val pTypePool = {
    val map = pTypes.foldLeft(TypeKeyMap[Any, PType[M, ?]]()) {
      case (map, pType) => map + (pType.pTypeKey -> pType)
    }
    if (pTypes.size != map.size) throw new DuplicatePTypesException
    map
  }
  
  private[longevity] val cTypePool = {
    val map = cTypes.foldLeft(TypeKeyMap[Any, CType[M, ?]]()) {
      case (map, cType) => map + (cType.cTypeKey -> cType)
    }
    if (cTypes.size != map.size) throw new DuplicateCTypesException
    map
  }

  private[longevity] val emblematic = Emblematic(emblemPool, unionPool)

  private[longevity] val realizedPTypes: TypeBoundMap[Any, PType[M, ?], RealizedPType[M, ?]] = {
    pTypePool.values.foldLeft(TypeBoundMap[Any, PType[M, ?], RealizedPType[M, ?]]()) { (acc, pType) =>
      def addPair[P](pType: PType[M, P]) = {
        pType.validateKeysAndIndexes()
        val polyPTypeOpt = pType match {
          case derivedPType: DerivedPType[M, P, _] =>
            if (!pTypePool.contains(derivedPType.polyPTypeKey)) {
              throw new DerivedHasNoPolyException(derivedPType.polyPTypeKey.name, isPType = true)
            }
            Some(pTypePool(derivedPType.polyPTypeKey))
          case _ =>
            None
        }
        val realizedPType = new RealizedPType[M, P](pType, polyPTypeOpt, emblematic)
        acc + (pType -> realizedPType)
      }
      addPair(pType)
    }
  }

  private def emblemPool = pEmblems ++ componentEmblems ++ keyValEmblems

  private def pEmblems = {
    val pTypesWithEmblems = pTypePool.filterValues(!_.isInstanceOf[PolyPType[M, _]])
    pTypesWithEmblems.mapValues[Emblem] {
      new TypeBoundFunction[Any, PType[M, ?], Emblem] {
        def apply[P](pType: PType[M, P]): Emblem[P] = Emblem(pType.pTypeKey)
      }
    }
  }

  private def componentEmblems = {
    val cTypesWithEmblems = cTypePool.filterValues(!_.isInstanceOf[PolyCType[M, _]])
    cTypesWithEmblems.mapValues[Emblem] {
      new TypeBoundFunction[Any, CType[M, ?], Emblem] {
        def apply[C](cType: CType[M, C]): Emblem[C] =
          Emblem(cType.cTypeKey)
      }
    }
  }

  private def keyValEmblems = kvTypes.foldLeft(EmblemPool()) { (acc, kvType) =>
    def addToPool[A](emblem: Emblem[A]) = acc + (emblem.typeKey -> emblem)
    addToPool(Emblem(kvType.kvEv.key))
  }

  private def unionPool = componentUnions ++ pUnions

  private def componentUnions = {
    val polyTypes = cTypePool.filterValues(_.isInstanceOf[PolyCType[M, _]])

    type DerivedFrom[E] = DerivedCType[M, E, Poly] forSome { type Poly >: E }

    val derivedTypes =
      cTypePool
        .filterValues(_.isInstanceOf[DerivedFrom[_]])
        .asInstanceOf[TypeKeyMap[Any, DerivedFrom]]

    type DerivedList[E] = List[Emblem[_ <: E]]
    val baseToDerivedsMap: TypeKeyMap[Any, DerivedList] =
      derivedTypes.values.foldLeft(TypeKeyMap[Any, DerivedList]) { (map, derivedType) =>

        def fromDerivedCType[E, Poly >: E](derivedType: DerivedCType[M, E, Poly])
        : TypeKeyMap[Any, DerivedList] = {
          implicit val polyTypeKey: TypeKey[Poly] = derivedType.polyTypeKey
          if (!polyTypes.contains[Poly]) {
            throw new DerivedHasNoPolyException(polyTypeKey.name, isPType = false)
          }

          val emblem = emblemPool(derivedType.cTypeKey)
          val derivedList = map.getOrElse(List.empty)
          map.+[Poly](emblem :: derivedList)
        }

        fromDerivedCType(derivedType)
      }

    polyTypes.mapValues[Union] {
      new TypeBoundFunction[Any, CType[M, ?], Union] {
        def apply[C](cType: CType[M, C]): Union[C] = {
          val constituents = baseToDerivedsMap(cType.cTypeKey)
          Union[C](constituents: _*)(cType.cTypeKey)
        }
      }
    }
  }

  private def pUnions = {
    val polyTypes = pTypePool.filterValues(_.isInstanceOf[PolyPType[M, _]])

    type DerivedFrom[P] = DerivedPType[M, P, Poly] forSome { type Poly >: P }

    val derivedTypes: TypeKeyMap[Any, DerivedFrom] =
      pTypePool.filterValues(_.isInstanceOf[DerivedFrom[_]]).asInstanceOf[TypeKeyMap[Any, DerivedFrom]]

    type DerivedList[P] = List[Emblem[_ <: P]]
    val baseToDerivedsMap: TypeKeyMap[Any, DerivedList] =
      derivedTypes.values.foldLeft(TypeKeyMap[Any, DerivedList]) { (map, derivedType) =>

        def fromDerivedPType[P, Poly >: P](derivedPType: DerivedPType[M, P, Poly])
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
      new TypeBoundFunction[Any, PType[M, ?], Union] {
        def apply[P](pType: PType[M, P]): Union[P] = {
          val constituents = baseToDerivedsMap.getOrElse(List[Emblem[P]]())(pType.pTypeKey)
          Union[P](constituents: _*)(pType.pTypeKey)
        }
      }
    }
  }

  override def toString = s"""|ModelType(
                              |  PTypePool(
                              |    ${pTypePool.values.mkString(",\n    ")}),
                              |  CTypePool(
                              |    ${cTypePool.values.mkString(",\n    ")}))""".stripMargin

}
