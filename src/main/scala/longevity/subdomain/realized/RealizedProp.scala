package longevity.subdomain.realized

import emblem.emblematic.Emblematic
import emblem.emblematic.EmblematicPropPath
import emblem.emblematic.ReflectiveProp
import emblem.emblematic.basicTypes.isBasicType
import emblem.exceptions.EmptyPropPathException
import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblematicInPropPathException
import emblem.typeKey
import longevity.exceptions.subdomain.ptype.NoSuchPropException
import longevity.exceptions.subdomain.ptype.PropTypeException
import longevity.exceptions.subdomain.ptype.PropTypeException
import longevity.exceptions.subdomain.ptype.UnsupportedPropTypeException
import longevity.subdomain.embeddable.Embeddable
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Prop

/** TODO */
private[longevity] class RealizedProp[P <: Persistent, A](
  // TODO figure out which ones actually need to be vals
  private[realized] val prop: Prop[P, A],
  private val emblematic: Emblematic,
  private[realized] val emblematicPropPath: EmblematicPropPath[P, A]) {

  // TODO reorder methods here

  def inlinedPath = emblematicPropPath.inlinedPath

  def propTypeKey = prop.propTypeKey

  def path = prop.path

  /** the value of this property for a persistent
   * 
   * @param p the persistent we are looking up the value of the property for
   */
  def propVal(p: P): A = emblematicPropPath.get(p)

  def updatePropVal(p: P, a: A): P = emblematicPropPath.set(p, a)

  val basicPropComponents: Seq[BasicPropComponent[P, A, _]] = {
    if (isBasicType(propTypeKey)) {
      Seq(BasicPropComponent[P, A, A](
        EmblematicPropPath.empty[A](propTypeKey),
        emblematicPropPath))
    } else {

      val emblem = emblematic.emblems.getOrElse(
        throw new RuntimeException("can't handle unions"))( // TODO RuntimeException
        propTypeKey)
      val propPaths = emblem.basicPropPaths(emblematic)

      propPaths.map { propPath =>
        def component[B](propPath: EmblematicPropPath[A, B]) = {
          BasicPropComponent[P, A, B](
            propPath,
            emblematicPropPath ++ propPath)
        }
        component(propPath)
      }
    }
  }

  /** resolves to underlying basic type */
  def resolvedPropVals(p: P): Seq[Any] = {
    basicPropComponents.map(_.get(propVal(p)))
  }

  /** an ordering for property values */
  val ordering: Ordering[A] = {
    val unitOrdering = new Ordering[A] {
      def compare(a1: A, a2: A) = 0
    }
    basicPropComponents.foldLeft(unitOrdering) { (ordering, basicPropComponent) =>
      def accumulate[B](basicPropComponent: BasicPropComponent[P, A, B]) =
        new Ordering[A]() {
          def compare(a1: A, a2: A) = {
            val i = ordering.compare(a1, a2)
            if (i != 0) i else {
              basicPropComponent.ordering.compare(
                basicPropComponent.get(a1),
                basicPropComponent.get(a2))
            }
          }
        }
      accumulate(basicPropComponent)
    }
  }

  override def toString: String = prop.path
  
}

private[subdomain] object RealizedProp {

  def apply[P <: Persistent, A](prop: Prop[P, A], emblematic: Emblematic): RealizedProp[P, A] = {

    val emblematicPropPath: EmblematicPropPath[P, A] = {
      def validatePath(): EmblematicPropPath[P, _] =
        try {
          EmblematicPropPath.unbounded(emblematic, prop.path)(prop.pTypeKey)
        } catch {
          case e: EmptyPropPathException =>
            throw new NoSuchPropException(prop.path, prop.pTypeKey)
          case e: NoSuchPropertyException =>
            throw new NoSuchPropException(prop.path, prop.pTypeKey)
          case e: NonEmblematicInPropPathException[_] =>
            throw new UnsupportedPropTypeException(prop.path)(prop.pTypeKey, e.typeKey)
        }

      def validateNonLeafEmblemProps(nonLeafEmblemProps: Seq[ReflectiveProp[_, _]]): Unit =
        nonLeafEmblemProps foreach { nonLeafEmblemProp =>
          if (! (nonLeafEmblemProp.typeKey <:< typeKey[Embeddable]))
            throw new UnsupportedPropTypeException(prop.path)(prop.pTypeKey, nonLeafEmblemProp.typeKey)
        }

      val emblematicPropPath = validatePath()
      val reflectiveProps = emblematicPropPath.props

      validateNonLeafEmblemProps(reflectiveProps.dropRight(1))

      val propPathTypeKey = reflectiveProps.last.typeKey

      if (! (prop.propTypeKey <:< propPathTypeKey)) {
        throw new PropTypeException(prop.path, prop.pTypeKey, prop.propTypeKey)
      }

      emblematicPropPath.asInstanceOf[EmblematicPropPath[P, A]]
    }

    new RealizedProp(prop, emblematic, emblematicPropPath)
  }

}
