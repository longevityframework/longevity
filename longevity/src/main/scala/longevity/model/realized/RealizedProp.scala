package longevity.model.realized

import emblem.TypeKey
import emblem.emblematic.Emblematic
import emblem.emblematic.EmblematicPropPath
import emblem.emblematic.ReflectiveProp
import emblem.emblematic.basicTypes.isBasicType
import emblem.exceptions.EmblemNotComposedOfBasicsException
import emblem.exceptions.EmptyPropPathException
import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblematicInPropPathException
import longevity.exceptions.model.NoSuchPropPathException
import longevity.exceptions.model.PropTypeException
import longevity.exceptions.model.PropTypeException
import longevity.exceptions.model.UnsupportedPropTypeException
import longevity.model.ptype.Prop

private[longevity] class RealizedProp[P, A](
  private[longevity] val prop: Prop[P, A],
  private val emblematic: Emblematic,
  private[realized] val emblematicPropPath: EmblematicPropPath[P, A]) {

  def propTypeKey = prop.propTypeKey

  def inlinedPath = emblematicPropPath.inlinedPath

  def propVal(p: P): A = emblematicPropPath.get(p)

  def updatePropVal(p: P, a: A): P = emblematicPropPath.set(p, a)

  lazy val realizedPropComponents: Seq[RealizedPropComponent[P, A, _]] = {
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

  // at the moment (2016.11.30), ordering and pOrdering below are used only by
  // InMemQuery and QuerySpec for implementing & testing OrderBy query clauses

  lazy val ordering: Ordering[A] = {
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

private[model] object RealizedProp {

  def apply[P, A](implicit prop: Prop[P, A], emblematic: Emblematic): RealizedProp[P, A] = {
    val emblematicPropPath = validatePath().asInstanceOf[EmblematicPropPath[P, A]]

    val leaf = emblematicPropPath.props.last
    validateLeafEmblemProp(leaf)

    val propPathTypeKey = leaf.typeKey
    validatePropTypeMatches(propPathTypeKey)
    validatePropTypeComposedOfBasics()

    new RealizedProp(prop, emblematic, emblematicPropPath)
  }

  private def validatePath[P, A]()(implicit prop: Prop[P, A], emblematic: Emblematic)
  : EmblematicPropPath[P, _] = {
    try {
      EmblematicPropPath.unbounded(emblematic, prop.path)(prop.pTypeKey)
    } catch {
      case e: EmptyPropPathException =>
        throw new NoSuchPropPathException(prop.path, prop.pTypeKey)
      case e: NoSuchPropertyException =>
        throw new NoSuchPropPathException(e.propName, prop.pTypeKey)
      case e: NonEmblematicInPropPathException[_] =>
        throw new UnsupportedPropTypeException(prop)
    }
  }

  def validateLeafEmblemProp[P, A](leafEmblemProp: ReflectiveProp[_, _])(
    implicit prop: Prop[P, A],
    emblematic: Emblematic)
  : Unit = {
    val key = leafEmblemProp.typeKey
    def isBasic = isBasicType(key)
    def isNonPolyEmbeddable = emblematic.emblems.contains(key)
    if (! (isBasic || isNonPolyEmbeddable)) {
      throw new UnsupportedPropTypeException(prop)
    }
  }

  private def validatePropTypeMatches[P, A](propPathTypeKey: TypeKey[_])(implicit prop: Prop[P, A]): Unit = {
    if (! (prop.propTypeKey =:= propPathTypeKey)) {
      throw new PropTypeException(prop.path, prop.pTypeKey, prop.propTypeKey, propPathTypeKey)
    }
  }

  private def validatePropTypeComposedOfBasics[P, A]()(implicit prop: Prop[P, A], emblematic: Emblematic)
  : Unit = {
    try {
      emblematic.emblems.get(prop.propTypeKey).map(_.basicPropPaths(emblematic))
    } catch {
      case e: EmblemNotComposedOfBasicsException[_] => throw new UnsupportedPropTypeException(prop)
    }
  }

}
