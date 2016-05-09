package emblem.emblematic

import emblem.TypeKey
import emblem.typeKey
import emblem.exceptions.EmptyPropPathException
import emblem.exceptions.NonEmblematicInPropPathException
import emblem.exceptions.EmblematicPropPathTypeMismatchException

/** a property path that recurses through an emblem tree to a specific leaf */
trait EmblematicPropPath[A, B] {

  /** the full path name (dot-separated) */
  val name: String

  /** a function that retrieves the property value from an instance */
  val get: (A) => B

  /** a [[TypeKey type key]] for the property path value type */
  val typeKey: TypeKey[B]

  /** a list of the [[ReflectiveProp properties]] that make up the path */
  val props: List[ReflectiveProp[_, _]]

  override def toString = s"EmblematicPropPath($name)"

  // yes, these are chintzy. please make em better
  override def hashCode: Int = name.hashCode
  override def equals(that: Any): Boolean =
    try {
      that.asInstanceOf[EmblematicPropPath[A, B]].name == name
    } catch {
      case e: ClassCastException => false
    }

}

/** an [[EmblematicPropPath]] factory */
object EmblematicPropPath {

  /** constructs an [[EmblematicPropPath]]
   * 
   * @tparam A the type of the starting point of the prop path
   * @tparam B the leaf property value type
   * @param emblematic the emblematic to use to traverse the path
   * @param path a dot-separated string representation of the path
   * @throws emblem.exceptions.EmblematicPropPathException whenever there is a
   * problem constructing the path. either your path string is poorly formed, or
   * you asked for an incorrect leaf type.
   */
  def apply[A : TypeKey, B : TypeKey](emblematic: Emblematic, path: String): EmblematicPropPath[A, B] = {
    val rootTypeKey = typeKey[A]
    val uepp = unbounded(emblematic, path)(rootTypeKey)
    val requestedPropTypeKey = typeKey[B]
    if (! (requestedPropTypeKey =:= uepp.typeKey)) {
      throw new EmblematicPropPathTypeMismatchException(
        emblematic, path, rootTypeKey, requestedPropTypeKey, uepp.typeKey)
    }
    uepp.asInstanceOf[EmblematicPropPath[A, B]]
  }

  /** constructs an [[EmblematicPropPath]]
   * 
   * @tparam A the type of the starting point of the prop path
   * @param emblematic the emblematic to use to traverse the path
   * @param path a dot-separated string representation of the path
   * @throws emblem.exceptions.EmblematicPropPathException on poorly formed path string
   */
  def unbounded[A : TypeKey](emblematic: Emblematic, path: String): EmblematicPropPath[A, _] = {
    if (path.isEmpty) throw new EmptyPropPathException

    val typeKeyA = typeKey[A]

    def lookupReflective[B : TypeKey](segment: String): Reflective[B] =
      emblematic.reflectives.getOrElse(
        throw new NonEmblematicInPropPathException(emblematic, path, segment)(typeKeyA))(
        typeKey[B])

    // nope, not tail recursive!
    def propPath0[A](prop: ReflectiveProp[A, _], pathSegments: Seq[String]): EmblematicPropPath[A, _] = {
      if (pathSegments.isEmpty) {
        Leaf(prop)
      } else {
        def introB[B](prop: ReflectiveProp[A, B]): EmblematicPropPath[A, _] = {
          val typeKeyB = prop.typeKey
          val reflective = lookupReflective(prop.name)(typeKeyB)
          val nextProp = reflective(pathSegments.head)
          ::(prop, propPath0(nextProp, pathSegments.tail))
        }
        introB(prop)
      }
    }

    val reflective = lookupReflective("")(typeKeyA)
    val pathSegments = path.split('.')
    propPath0(reflective(pathSegments.head), pathSegments.tail)
  }

  private case class Leaf[A, B](prop: ReflectiveProp[A, B])
  extends EmblematicPropPath[A, B] {
    val name = prop.name
    val get = prop.get
    val typeKey = prop.typeKey
    val props = prop :: Nil
  }

  private case class ::[A, B, C](head: ReflectiveProp[A, B], tail: EmblematicPropPath[B, C])
  extends EmblematicPropPath[A, C] {
    val name = s"${head.name}.${tail.name}"
    val get = { a: A => tail.get(head.get(a)) }
    val typeKey = tail.typeKey
    val props = head :: tail.props
  }

}
