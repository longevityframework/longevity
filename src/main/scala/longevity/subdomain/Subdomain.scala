package longevity.subdomain

import emblem.typeKey
import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemProp
import emblem.emblematic.EmblemPool
import emblem.emblematic.Emblematic
import emblem.emblematic.ExtractorFor
import emblem.emblematic.ExtractorPool
import emblem.emblematic.Union
import emblem.emblematic.UnionPool
import emblem.emblematic.basicTypes.basicTypeOrderings
import emblem.emblematic.basicTypes.isBasicType
import emblem.typeBound.TypeBoundFunction
import emblem.typeBound.WideningTypeBoundFunction
import longevity.exceptions.subdomain.DerivedHasNoPolyException
import longevity.subdomain.embeddable.DerivedType
import longevity.subdomain.embeddable.EType
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.Embeddable
import longevity.subdomain.embeddable.PolyType
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.PolyPType

/** a specification of a subdomain of a project's domain. contains a pool of
 * all the [[ptype.PType persistent types]] in the subdomain, as well as
 * all the [[embeddable.EType embeddable types]].
 *
 * @param name the name of the subdomain
 * @param pTypePool a complete set of the persistent types in the subdomain.
 * defaults to empty
 * @param eTypePool a complete set of the entity types within the
 * subdomain. defaults to empty
 * @param shorthandPool a complete set of the shorthands used by the bounded
 * context. defaults to empty
 */
class Subdomain(
  val name: String,
  val pTypePool: PTypePool = PTypePool.empty,
  val eTypePool: ETypePool = ETypePool.empty,
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

    val eTypesWithEmblems = eTypePool.filterValues(!_.isInstanceOf[PolyType[_]])
    val entityEmblems = eTypesWithEmblems.mapValuesWiden[Any, Emblem] {
      new WideningTypeBoundFunction[Embeddable, Any, EType, Emblem] {
        def apply[TypeParam <: Embeddable](eType: EType[TypeParam]): Emblem[TypeParam] =
          Emblem(eType.eTypeKey)
      }
    }

    pEmblems ++ entityEmblems
  }

  private val unionPool: UnionPool = entityUnions ++ pUnions

  private def entityUnions = {
    val polyTypes = eTypePool.filterValues(_.isInstanceOf[PolyType[_]])

    type DerivedFrom[E <: Embeddable] = DerivedType[E, Poly] forSome { type Poly >: E <: Embeddable }

    val derivedTypes: TypeKeyMap[Embeddable, DerivedFrom] =
      eTypePool.filterValues(_.isInstanceOf[DerivedFrom[_]]).asInstanceOf[TypeKeyMap[Embeddable, DerivedFrom]]

    type DerivedList[E <: Embeddable] = List[Emblem[_ <: E]]
    val baseToDerivedsMap: TypeKeyMap[Embeddable, DerivedList] =
      derivedTypes.values.foldLeft(TypeKeyMap[Embeddable, DerivedList]) { (map, derivedType) =>

        def fromDerivedType[E <: Embeddable, Poly >: E <: Embeddable](derivedType: DerivedType[E, Poly])
        : TypeKeyMap[Embeddable, DerivedList] = {
          implicit val polyTypeKey: TypeKey[Poly] = derivedType.polyType.eTypeKey
          if (!polyTypes.contains[Poly]) {
            throw new DerivedHasNoPolyException(polyTypeKey.name, isPType = false)
          }

          val emblem = emblemPool(derivedType.eTypeKey)
          val derivedList = map.getOrElse(List.empty)
          map +[Poly] (emblem :: derivedList)
        }

        fromDerivedType(derivedType)
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

  // OKAY, I need to:
  // - know if a type is composed of a single basic
  // - know what that single basic type is
  // - resolve an actual basic from an actual wrapper type

  /** this type key either represents a single basic value, or has an emblem
   * that boils down to a single basic value
   */
  private[longevity] def getBasicResolver[A : TypeKey]: Option[BasicResolver[A, _]] = {
    val key = typeKey[A]
    if (isBasicType(key)) {
      Some(BasicResolver(
        key,
        key,
        identity,
        basicTypeOrderings(key)))
    } else {
      val emblemOpt = emblemPool.get(key)
      emblemOpt.flatMap { emblem =>
        if (emblem.props.size == 1) {
          def resolver[B](prop: EmblemProp[A, B]) = {
            def outer[C](inner: BasicResolver[B, C]) =
              BasicResolver(
                key,
                inner.basicTypeKey,
                inner.resolve compose prop.get,
                Ordering.by(prop.get)(inner.ordering))
            getBasicResolver(prop.typeKey).map(inner => outer(inner))
          }
          resolver(emblem.props.head)
        } else {
          None
        }
      }
    }
  }

}

object Subdomain {

  /** constructs a new subdomain.
   * 
   * @param name the name of the subdomain
   * @param pTypePool a complete set of the persistent types in the subdomain. defaults to empty
   * @param eTypePool a complete set of the embeddable types within the subdomain. defaults to empty
   * @param shorthandPool a complete set of the shorthands used by the bounded context. defaults to empty
   */
  def apply(
    name: String,
    pTypePool: PTypePool = PTypePool.empty,
    eTypePool: ETypePool = ETypePool.empty,
    shorthandPool: ShorthandPool = ShorthandPool.empty)
  : Subdomain =
    new Subdomain(name, pTypePool, eTypePool, shorthandPool)

}
