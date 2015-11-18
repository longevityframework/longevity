package longevity.subdomain

import emblem.EmblemPropPath
import emblem.basicTypes.isBasicType
import emblem.exceptions.EmblemPropPathTypeMismatchException
import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblemInPropPathException
import emblem.exceptions.{ EmptyPropPathException => EmblemEmptyPropPathException }
import emblem.imports._
import longevity.exceptions.subdomain.EmptyPropPathException
import longevity.exceptions.subdomain.InvalidPropPathLeafException
import longevity.exceptions.subdomain.NoSuchPropPathSegmentException
import longevity.exceptions.subdomain.NonEntityPropPathSegmentException
import longevity.exceptions.subdomain.PropTypeMismatchException

/** a property of the root that can be used as part of a natural key. the property can belong to a
 * contained entity of the root at any depth of containment, so long as every containment step along the
 * path is exactly-one. the type of the property must be a [[Assoc]], a [[Shorthand]], or a basic type.
 * 
 * @param path a dot-separated path of the property descending from the root entity
 * @param typeKey the `TypeKey` for the property value type
 * @see `emblem.basicTypes`
 */
case class Prop[E <: RootEntity, A] private (
  val path: String,
  val typeKey: TypeKey[_])(
  private val emblemPropPath: EmblemPropPath[E, _]) {

  /** the value of this property for a specific root entity
   * @param e the root entity we are looking up the value of the property for
   */
  def propVal(e: E): Any = emblemPropPath.get(e)

  // TODO scaladocs

  // import Query._
  // def eq(a: A) = EqRelationalQuery(this, a)
  // def neq(a: A) = RelationalQuery(this, NeqOp, a)
  // def lt(a: A) = RelationalQuery(this, LtOp, a)
  // def lte(a: A) = RelationalQuery(this, LteOp, a)
  // def gt(a: A) = RelationalQuery(this, GtOp, a)
  // def gte(a: A) = RelationalQuery(this, GteOp, a)

}

object Prop {

  private[subdomain] def apply[E <: RootEntity, A : TypeKey](
    path: String,
    emblem: Emblem[E],
    rootTypeKey: TypeKey[E],
    shorthandPool: ShorthandPool)
  : Prop[E, A] = {
    val prop = unbounded(path, emblem, rootTypeKey, shorthandPool)
    if (!(prop.typeKey <:< typeKey[A]))
      throw new PropTypeMismatchException(path, rootTypeKey)
    prop.asInstanceOf[Prop[E, A]]
  }

  private[subdomain] def unbounded[E <: RootEntity](
    path: String,
    emblem: Emblem[E],
    rootTypeKey: TypeKey[E],
    shorthandPool: ShorthandPool)
  : Prop[E, _] = {

    def validatePath(): EmblemPropPath[E, _] =
      try {
        EmblemPropPath.unbounded(emblem, path)
      } catch {
        case e: EmblemEmptyPropPathException =>
          throw new EmptyPropPathException(e)
        case e: NoSuchPropertyException =>
          throw new NoSuchPropPathSegmentException(e.propName, path, rootTypeKey, e)
        case e: NonEmblemInPropPathException =>
          throw new NonEntityPropPathSegmentException(e.nonEmblemPathSegment, path, rootTypeKey, e)
      }

    def validateNonLeafEmblemProps(nonLeafEmblemProps: Seq[EmblemProp[_ <: HasEmblem, _]]): Unit =
      nonLeafEmblemProps foreach { nonLeafEmblemProp =>
        if (!(nonLeafEmblemProp.typeKey <:< typeKey[Entity]))
          throw new NonEntityPropPathSegmentException(nonLeafEmblemProp.name, path, rootTypeKey)
      }

    def validateLeafEmblemProp(leafEmblemProp: EmblemProp[_ <: HasEmblem, _]): TypeKey[_] = {
      val key = leafEmblemProp.typeKey
      if (!(isBasicType(key) || key <:< typeKey[Assoc[_]] || shorthandPool.contains(key)))
        throw new InvalidPropPathLeafException(path, rootTypeKey)
      key
    }

    val emblemPropPath = validatePath()
    val emblemProps = emblemPropPath.props

    val nonLeafEmblemProps = emblemProps.dropRight(1)
    val () = validateNonLeafEmblemProps(nonLeafEmblemProps)

    val leafEmblemProp = emblemProps.last
    val propTypeKey = validateLeafEmblemProp(leafEmblemProp)
    new Prop(path, propTypeKey)(emblemPropPath)
  }

}
