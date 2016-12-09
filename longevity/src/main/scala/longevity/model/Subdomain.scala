package longevity.model

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemPool
import emblem.emblematic.Emblematic
import emblem.emblematic.Union
import emblem.typeBound.TypeBoundFunction
import emblem.typeBound.TypeBoundMap
import longevity.exceptions.subdomain.DerivedHasNoPolyException
import longevity.model.realized.RealizedPType
import org.reflections.Reflections
import scala.collection.JavaConverters.asScalaSetConverter
import scala.reflect.runtime.universe.NoType
import scala.reflect.runtime.universe.runtimeMirror

/** a description of a project's domain model. contains a pool of
 * all the [[PType persistent types]] in the model, as well as
 * all the [[CType component types]].
 *
 * @constructor creates a subdomain from pools of [[PType persistent]] and
 * [[CType component]] types
 *
 * @param pTypePool a complete set of the persistent types in the subdomain.
 *
 * @param cTypePool a complete set of the component types within the
 * subdomain. defaults to empty
 */
class Subdomain(
  val pTypePool: PTypePool,
  val cTypePool: CTypePool = CTypePool.empty) {

  private def this(pools: (PTypePool, CTypePool)) = this(pools._1, pools._2)

  /** creates a subdomain by scanning the named package for [[PType persistent
   * types]] and [[CType component types]]
   *
   * @param packageName the name of the package to scan
   */
  def this(packageName: String) = this {
    val reflections = new Reflections(s"$packageName.")

    def subTypes[A](c: Class[A]): Set[Class[_ <: A]] = reflections.getSubTypesOf(c).asScala.toSet

    val pTypeClasses =
      subTypes(classOf[PType[_]]) ++ subTypes(classOf[PolyPType[_]]) ++ subTypes(classOf[DerivedPType[_, _]])
    val cTypeClasses =
      subTypes(classOf[CType[_]]) ++ subTypes(classOf[PolyCType[_]]) ++ subTypes(classOf[DerivedCType[_, _]])

    def singletons[A](classes: Set[Class[_ <: A]]) = classes.flatMap { c =>
      val r = runtimeMirror(c.getClassLoader)
      val s = r.moduleSymbol(c)
      s.typeSignature match {
        case NoType => None // not actually a module, just a class or trait or something
        case _      => Some(r.reflectModule(s).instance.asInstanceOf[A])
      }
    }

    val pTypeObjects = singletons[PType[_]](pTypeClasses)
    val cTypeObjects = singletons[CType[_]](cTypeClasses)

    (PTypePool(pTypeObjects.toSeq: _*), CTypePool(cTypeObjects.toSeq: _*))
  }

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
    val cTypesWithEmblems = cTypePool.filterValues(!_.isInstanceOf[PolyCType[_]])
    cTypesWithEmblems.mapValues[Emblem] {
      new TypeBoundFunction[Any, CType, Emblem] {
        def apply[TypeParam](cType: CType[TypeParam]): Emblem[TypeParam] =
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

  private def unionPool = entityUnions ++ pUnions

  private def entityUnions = {
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
        def apply[TypeParam](cType: CType[TypeParam]): Union[TypeParam] = {
          val constituents = baseToDerivedsMap(cType.cTypeKey)
          Union[TypeParam](constituents: _*)(cType.cTypeKey)
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

  override def toString = s"""|Subdomain(
                              |  PTypePool(
                              |    ${pTypePool.values.mkString(",\n    ")}),
                              |  CTypePool(
                              |    ${cTypePool.values.mkString(",\n    ")}))""".stripMargin

}

/** provides factory methods for constructing [[Subdomain subdomains]] */
object Subdomain {

  /** creates a subdomain from pools of [[PType persistent]] and
   * [[CType component]] types
   * 
   * @param pTypePool a complete set of the persistent types in the subdomain.
   *
   * @param cTypePool a complete set of the component types within the
   * subdomain. defaults to empty
   */
  def apply(
    pTypePool: PTypePool,
    cTypePool: CTypePool = CTypePool.empty)
  : Subdomain =
    new Subdomain(pTypePool, cTypePool)

  /** creates a subdomain by scanning the named package for [[PType persistent
   * types]] and [[CType component types]]
   *
   * @param packageName the name of the package to scan
   */
  def apply(packageName: String): Subdomain = Subdomain(packageName)

}
