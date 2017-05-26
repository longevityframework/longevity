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
import longevity.model.realized.RealizedPType
import org.reflections.Reflections
import scala.collection.JavaConverters.asScalaSetConverter
import scala.reflect.runtime.universe.NoType
import scala.reflect.runtime.universe.runtimeMirror

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
 * @constructor creates a model type from pools of [[PType persistent]] and [[CType component]]
 * types
 *
 * @param pTypePool a complete set of the persistent types in the domain model.
 *
 * @param cTypePool a complete set of the component types within the domain model. defaults to empty
 *
 * @see longevity.model.annotations.domainModel
 */
class ModelType[M](
  val pTypePool: PTypePool[M],
  val cTypePool: CTypePool = CTypePool.empty) {

  /** a persistent type where the model is fixed */
  type PTypeM[P] = PType[M, P]

  private def this(pools: (PTypePool[M], CTypePool)) = this(pools._1, pools._2)

  /** creates a model type by scanning the named package for [[PType persistent types]] and [[CType
   * component types]]
   *
   * @tparam M the model
   *
   * @param packageName the name of the package to scan
   */
  def this(packageName: String) = this {
    val reflections = new Reflections(s"$packageName.")

    def subTypes[A](c: Class[A]): Set[Class[_ <: A]] = reflections.getSubTypesOf(c).asScala.toSet

    val pTypeClasses = {
      subTypes(classOf[PType[M, _]]) ++
      subTypes(classOf[PolyPType[M, _]]) ++
      subTypes(classOf[DerivedPType[M, _, _]])
    }
    val cTypeClasses = {
      subTypes(classOf[CType[_]]) ++
      subTypes(classOf[PolyCType[_]]) ++
      subTypes(classOf[DerivedCType[_, _]])
    }

    def singletons[A](classes: Set[Class[_ <: A]]) = classes.flatMap { c =>
      val r = runtimeMirror(c.getClassLoader)
      val s = r.moduleSymbol(c)
      s.typeSignature match {
        case NoType => None // not actually a module, just a class or trait or something
        case _      => Some(r.reflectModule(s).instance.asInstanceOf[A])
      }
    }

    val pTypeObjects = singletons[PType[M, _]](pTypeClasses)
    val cTypeObjects = singletons[CType[_]](cTypeClasses)

    (PTypePool(pTypeObjects.toSeq: _*), CTypePool(cTypeObjects.toSeq: _*))
  }

  private[longevity] val emblematic = Emblematic(emblemPool, unionPool)

  private[longevity] val realizedPTypes: TypeBoundMap[Any, PTypeM, RealizedPType] = {
    pTypePool.values.foldLeft(TypeBoundMap[Any, PTypeM, RealizedPType]()) { (acc, pType) =>
      def addPair[P](pType: PTypeM[P]) = {
        pType.validateKeysAndIndexes()
        val polyPTypeOpt = pType match {
          case derivedPType: DerivedPType[M, P, _] =>
            if (!pTypePool.typeKeyMap.contains(derivedPType.polyPTypeKey)) {
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

  private def emblemPool = pEmblems ++ componentEmblems ++ keyValEmblems

  private def pEmblems = {
    val pTypesWithEmblems = pTypePool.typeKeyMap.filterValues(!_.isInstanceOf[PolyPType[M, _]])
    pTypesWithEmblems.mapValues[Emblem] {
      new TypeBoundFunction[Any, PTypeM, Emblem] {
        def apply[P](pType: PTypeM[P]): Emblem[P] = Emblem(pType.pTypeKey)
      }
    }
  }

  private def componentEmblems = {
    val cTypesWithEmblems = cTypePool.filterValues(!_.isInstanceOf[PolyCType[_]])
    cTypesWithEmblems.mapValues[Emblem] {
      new TypeBoundFunction[Any, CType, Emblem] {
        def apply[C](cType: CType[C]): Emblem[C] =
          Emblem(cType.cTypeKey)
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

  private def unionPool = componentUnions ++ pUnions

  private def componentUnions = {
    val polyTypes = cTypePool.filterValues(_.isInstanceOf[PolyCType[_]])

    type DerivedFrom[E] = DerivedCType[E, Poly] forSome { type Poly >: E }

    val derivedTypes =
      cTypePool
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

          val emblem = emblemPool(derivedType.cTypeKey)
          val derivedList = map.getOrElse(List.empty)
          map.+[Poly](emblem :: derivedList)
        }

        fromDerivedCType(derivedType)
      }

    polyTypes.mapValues[Union] {
      new TypeBoundFunction[Any, CType, Union] {
        def apply[C](cType: CType[C]): Union[C] = {
          val constituents = baseToDerivedsMap(cType.cTypeKey)
          Union[C](constituents: _*)(cType.cTypeKey)
        }
      }
    }
  }

  private def pUnions = {
    val polyTypes = pTypePool.typeKeyMap.filterValues(_.isInstanceOf[PolyPType[M, _]])

    type DerivedFrom[P] = DerivedPType[M, P, Poly] forSome { type Poly >: P }

    val derivedTypes: TypeKeyMap[Any, DerivedFrom] =
      pTypePool.typeKeyMap.filterValues(_.isInstanceOf[DerivedFrom[_]]).asInstanceOf[TypeKeyMap[Any, DerivedFrom]]

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
      new TypeBoundFunction[Any, PTypeM, Union] {
        def apply[P](pType: PTypeM[P]): Union[P] = {
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

/** provides factory methods for constructing [[ModelType model types]] */
object ModelType {

  /** creates a model type from pools of [[PType persistent]] and [[CType component]] types
   * 
   * @tparam M the model
   *
   * @param pTypePool a complete set of the persistent types in the domain model.
   *
   * @param cTypePool a complete set of the component types within the domain model. defaults to
   * empty
   */
  def apply[M](
    pTypePool: PTypePool[M],
    cTypePool: CTypePool = CTypePool.empty)
  : ModelType[M] =
    new ModelType(pTypePool, cTypePool)

  /** creates a model type by scanning the named package for [[PType persistent types]] and [[CType
   * component types]]
   *
   * @tparam M the model
   *
   * @param packageName the name of the package to scan
   */
  def apply[M](packageName: String): ModelType[M] = ModelType(packageName)

}
