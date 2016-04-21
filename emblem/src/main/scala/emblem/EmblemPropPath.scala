package emblem

// TODO: more emblem subpackages

import emblem.exceptions.TypeIsNotCaseClassException
import emblem.exceptions.EmptyPropPathException
import emblem.exceptions.NonEmblemInPropPathException
import emblem.exceptions.EmblemPropPathTypeMismatchException

/** a property path that recurses through an emblem tree to a specific leaf */
trait EmblemPropPath[A, B] {

  /** the full path name (dot-separated) */
  val name: String

  /** a function that retrieves the property value from an instance */
  val get: (A) => B

  /** a function that updates the property value to produce a new instance */
  val set: (A, B) => A

  /** a [[TypeKey type key]] for the property path value type */
  val typeKey: TypeKey[B]

  /** a list of the [[EmblemProp emblem props]] that make up the path */
  val props: List[EmblemProp[_, _]]

  override def toString = s"EmblemPropPath($name)"

  // yes, these are chintzy. please make em better
  override def hashCode: Int = name.hashCode
  override def equals(that: Any): Boolean =
    try {
      that.asInstanceOf[EmblemPropPath[A, B]].name == name
    } catch {
      case e: ClassCastException => false
    }

}

/** an [[EmblemPropPath]] factory */
object EmblemPropPath {

  /** constructs an [[EmblemPropPath]]
   * 
   * @tparam A the type of the starting point of the prop path
   * @tparam B the leaf property value type
   * @param path a dot-separated string representation of the path
   * @throws emblem.exceptions.EmblemPropPathException whenever there is a problem constructing the path. either
   * your path string is poorly formed, or you asked for an incorrect leaf type.
   */
  def apply[A : TypeKey, B : TypeKey](path: String): EmblemPropPath[A, B] = apply(Emblem[A], path)

  /** constructs an [[EmblemPropPath]]
   * 
   * @tparam A the type of the starting point of the prop path
   * @tparam B the leaf property value type
   * @param emblem the emblem for the starting point of the prop path
   * @param path a dot-separated string representation of the path
   * @throws emblem.exceptions.EmblemPropPathException whenever there is a problem constructing the path. either
   * your path string is poorly formed, or you asked for an incorrect leaf type.
   */
  def apply[A, B : TypeKey](emblem: Emblem[A], path: String): EmblemPropPath[A, B] = {
    val uepp = unbounded(emblem, path)
    val requestedType = typeKey[B]
    if (!(requestedType =:= uepp.typeKey))
      throw new EmblemPropPathTypeMismatchException(emblem, path, requestedType, uepp.typeKey)
    uepp.asInstanceOf[EmblemPropPath[A, B]]
  }

  /** constructs an [[EmblemPropPath]]
   * 
   * @tparam A the type of the starting point of the prop path
   * @param path a dot-separated string representation of the path
   * @throws emblem.exceptions.EmblemPropPathException on poorly formed path string
   */
  def unbounded[A : TypeKey](path: String): EmblemPropPath[A, _] = unbounded(Emblem[A], path)

  /** constructs an [[EmblemPropPath]]
   * 
   * @tparam A the type of the starting point of the prop path
   * @param emblem the emblem for the starting point of the prop path
   * @param path a dot-separated string representation of the path
   * @throws emblem.exceptions.EmblemPropPathException on poorly formed path string
   */
  def unbounded[A](emblem: Emblem[A], path: String): EmblemPropPath[A, _] = {
    if (path.isEmpty) throw new EmptyPropPathException
    val pathSegments = path.split('.')

    // nope, not tail recursive!
    def propPath0[A](prop: EmblemProp[A, _], pathSegments: Seq[String]): EmblemPropPath[A, _] = {
      if (pathSegments.isEmpty)
        Leaf(prop)
      else {
        // TODO: should be retrieving this Emblem from the emblematic, not generating it on the fly
        def introB[B](prop: EmblemProp[A, B]): EmblemPropPath[A, _] = {
          try {
            val nextProp = Emblem(prop.typeKey).apply(pathSegments.head)
            ::(prop, propPath0(nextProp, pathSegments.tail))
          } catch {
            case e: TypeIsNotCaseClassException =>
              throw new NonEmblemInPropPathException(emblem, path, prop.name)(prop.typeKey)
          }
        }
        introB(prop.asInstanceOf[EmblemProp[A, _]])
      }
    }

    propPath0(emblem(pathSegments.head), pathSegments.tail)
  }

  private case class Leaf[A, B](prop: EmblemProp[A, B])
  extends EmblemPropPath[A, B] {
    val name = prop.name
    val get = prop.get
    val set = prop.set
    val typeKey = prop.typeKey
    val props = prop :: Nil
  }

  private case class ::[A, B, C](head: EmblemProp[A, B], tail: EmblemPropPath[B, C])
  extends EmblemPropPath[A, C] {
    val name = s"${head.name}.${tail.name}"
    val get = { a: A => tail.get(head.get(a)) }
    val set = { (a: A, c: C) => head.set(a, tail.set(head.get(a), c)) }
    val typeKey = tail.typeKey
    val props = head :: tail.props
  }

}
