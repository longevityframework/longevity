package longevity.subdomain.realized

import emblem.emblematic.Emblematic
import emblem.emblematic.EmblematicPropPath
import emblem.emblematic.ReflectiveProp
import emblem.emblematic.basicTypes.isBasicType
import emblem.exceptions.EmptyPropPathException
import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblematicInPropPathException
import emblem.typeKey
import longevity.exceptions.subdomain.NoSuchPropPathException
import longevity.exceptions.subdomain.PropTypeException
import longevity.exceptions.subdomain.PropTypeException
import longevity.exceptions.subdomain.UnsupportedPropTypeException
import longevity.subdomain.KeyVal
import longevity.subdomain.ptype.Prop

private[longevity] class RealizedProp[P, A](
  private[longevity] val prop: Prop[P, A],
  private val emblematic: Emblematic,
  private[realized] val emblematicPropPath: EmblematicPropPath[P, A]) {

  def propTypeKey = prop.propTypeKey

  def inlinedPath = emblematicPropPath.inlinedPath

  def propVal(p: P): A = emblematicPropPath.get(p)

  def updatePropVal(p: P, a: A): P = emblematicPropPath.set(p, a)

  val realizedPropComponents: Seq[RealizedPropComponent[P, A, _]] = {
    if (isBasicType(propTypeKey)) {
      Seq(RealizedPropComponent[P, A, A](
        EmblematicPropPath.empty[A](propTypeKey),
        emblematicPropPath))
    } else {
      val emblem = emblematic.emblems(propTypeKey)
      val propPaths = emblem.basicPropPaths(emblematic)
      propPaths.map { propPath =>
        def component[B](propPath: EmblematicPropPath[A, B]) = {
          RealizedPropComponent[P, A, B](
            propPath,
            emblematicPropPath ++ propPath)
        }
        component(propPath)
      }
    }
  }

  val ordering: Ordering[A] = {
    val unitOrdering = new Ordering[A] { def compare(a1: A, a2: A) = 0 }
    realizedPropComponents.foldLeft(unitOrdering) { (ordering, realizedPropComponent) =>
      def accumulate[B](realizedPropComponent: RealizedPropComponent[P, A, B]) =
        new Ordering[A]() {
          def compare(a1: A, a2: A) = {
            val i = ordering.compare(a1, a2)
            if (i != 0) i else {
              realizedPropComponent.ordering.compare(
                realizedPropComponent.get(a1),
                realizedPropComponent.get(a2))
            }
          }
        }
      accumulate(realizedPropComponent)
    }
  }

  lazy val pOrdering = new Ordering[P] {
    def compare(p1: P, p2: P) = ordering.compare(propVal(p1), propVal(p2))
  }

  override def toString: String = prop.path
  
}

private[subdomain] object RealizedProp {

  private val keyValTypeKey = typeKey[KeyVal[P] forSome {
    type P
    type V <: KeyVal[P]
  }]

  def apply[P, A](prop: Prop[P, A], emblematic: Emblematic): RealizedProp[P, A] = {

    val emblematicPropPath: EmblematicPropPath[P, A] = {
      def validatePath(): EmblematicPropPath[P, _] =
        try {
          EmblematicPropPath.unbounded(emblematic, prop.path)(prop.pTypeKey)
        } catch {
          case e: EmptyPropPathException =>
            throw new NoSuchPropPathException(prop.path, prop.pTypeKey)
          case e: NoSuchPropertyException =>
            throw new NoSuchPropPathException(e.propName, prop.pTypeKey)
          case e: NonEmblematicInPropPathException[_] =>
            throw new UnsupportedPropTypeException(e.nonEmblematicPathSegment)(prop.pTypeKey, e.typeKey)
        }

      def validateLeafEmblemProp(leafEmblemProp: ReflectiveProp[_, _]): Unit = {
        val key = leafEmblemProp.typeKey
        def isBasic = isBasicType(key)
        def isNonPolyEmbeddable = emblematic.emblems.contains(key)
        def isKeyVal = key <:< keyValTypeKey
        if (! (isBasic || isNonPolyEmbeddable || isKeyVal)) {
          throw new UnsupportedPropTypeException(prop.path)(prop.pTypeKey, key)
        }
      }

      val emblematicPropPath = validatePath()
      val reflectiveProps = emblematicPropPath.props
      val leaf = reflectiveProps.last

      validateLeafEmblemProp(leaf)

      val propPathTypeKey = leaf.typeKey

      if (! (prop.propTypeKey =:= propPathTypeKey)) {
        throw new PropTypeException(prop.path, prop.pTypeKey, prop.propTypeKey)
      }

      emblematicPropPath.asInstanceOf[EmblematicPropPath[P, A]]
    }

    new RealizedProp(prop, emblematic, emblematicPropPath)
  }

}
