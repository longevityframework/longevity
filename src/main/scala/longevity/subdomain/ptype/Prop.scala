package longevity.subdomain.ptype

import emblem.TypeKey
import emblem.emblematic.EmblematicPropPath
import emblem.emblematic.ReflectiveProp
import emblem.exceptions.EmptyPropPathException
import emblem.exceptions.NoSuchPropertyException
import emblem.exceptions.NonEmblematicInPropPathException
import emblem.typeKey
import longevity.exceptions.subdomain.ptype.NoSuchPropException
import longevity.exceptions.subdomain.ptype.PTypeHasNoSubdomainException
import longevity.exceptions.subdomain.ptype.PropNotOrderedException
import longevity.exceptions.subdomain.ptype.PropTypeException
import longevity.exceptions.subdomain.ptype.UnsupportedPropTypeException
import longevity.subdomain.Assoc
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.Embeddable
import longevity.subdomain.persistent.Persistent

/** a property for this persistent type. properties can be used to define [[Key keys]]
 * and [[Index indexes]], as well as for building [[Query queries]]. a property
 * can descend from the root into child entities at any depth. at present, a
 * property cannot pass through any collections. at present, the type of the
 * property must be an [[Assoc]], a [[Shorthand]], or a basic type.
 * 
 * @param path a dot-separated path of the property descending from the root
 * @param pTypeKey the `TypeKey` for the enclosing [[PType persistent type]]
 * @param propTypeKey the `TypeKey` for the property value type
 * @see `emblem.emblematic.basicTypes`
 */
case class Prop[P <: Persistent, A] private[ptype] (
  path: String,
  pTypeKey: TypeKey[P],
  propTypeKey: TypeKey[A])(
  private val propLateInitializer: PropLateInitializer[P]) {

  private var subdomainOpt: Option[Subdomain] = None

  private lazy val emblematicPropPath: EmblematicPropPath[P, A] = subdomainOpt match {
    case Some(subdomain) => initializePropPath(subdomain)
    case None => throw new PTypeHasNoSubdomainException(pTypeKey)(
      "you cannot use Prop.propVal without a subdomain.")
  }

  propLateInitializer.registerProp(this)

  /** the value of this property for a persistent
   * 
   * @param p the persistent we are looking up the value of the property for
   */
  def propVal(p: P): A = emblematicPropPath.get(p)

  /** an ordering for property values
   * 
   * @throws longevity.exceptions.subdomain.ptype.PropNotOrderedException
   * if the ordering is accessed, and the property value type is not a type
   * for which orderings are supported. right now, we only support orderings
   * for basic types
   */
  lazy val ordering: Ordering[A] = {
    val subdomain = subdomainOpt match {
      case Some(subdomain) => subdomain
      case None => throw new PTypeHasNoSubdomainException(pTypeKey)(
        "you cannot use Prop.ordering without a subdomain.")
    }
    val shorthandPool = subdomain.shorthandPool

    val basicResolverOpt = subdomain.getBasicResolver(propTypeKey)
    basicResolverOpt match {
      case Some(resolver) => resolver.ordering
      case None => if (shorthandPool.contains(propTypeKey)) {
        shorthandPool(propTypeKey).actualOrdering
      } else {
        throw new PropNotOrderedException(this)
      }
    }
  }

  override def toString: String = path

  private[ptype] def registerSubdomain(subdomain: Subdomain): Unit = {
    subdomainOpt = Some(subdomain)
    // initialize lazy val so we get exceptions about problems with this prop asap:
    emblematicPropPath
  }

  private def initializePropPath(subdomain: Subdomain): EmblematicPropPath[P, A] = {
    val shorthandPool = subdomain.shorthandPool
    val emblematic = subdomain.emblematic

    def validatePath(): EmblematicPropPath[P, _] =
      try {
        EmblematicPropPath.unbounded(emblematic, path)(pTypeKey)
      } catch {
        case e: EmptyPropPathException =>
          throw new NoSuchPropException(path, pTypeKey)
        case e: NoSuchPropertyException =>
          throw new NoSuchPropException(path, pTypeKey)
        case e: NonEmblematicInPropPathException[_] =>
          throw new UnsupportedPropTypeException(path)(pTypeKey, e.typeKey)
      }

    def validateNonLeafEmblemProps(nonLeafEmblemProps: Seq[ReflectiveProp[_, _]]): Unit =
      nonLeafEmblemProps foreach { nonLeafEmblemProp =>
        if (! (nonLeafEmblemProp.typeKey <:< typeKey[Embeddable]))
          throw new UnsupportedPropTypeException(path)(pTypeKey, nonLeafEmblemProp.typeKey)
      }

    def validateLeafEmblemProp(leafEmblemProp: ReflectiveProp[_, _]): TypeKey[_] = {
      val key = leafEmblemProp.typeKey

      if (!(subdomainOpt.get.getBasicResolver(key).isDefined ||
            key <:< typeKey[Assoc[_]] ||
            shorthandPool.contains(key)
          ))
        throw new UnsupportedPropTypeException(path)(pTypeKey, key)
      key
    }

    val emblematicPropPath = validatePath()
    val reflectiveProps = emblematicPropPath.props

    val nonLeafEmblemProps = reflectiveProps.dropRight(1)
    val () = validateNonLeafEmblemProps(nonLeafEmblemProps)

    val leafEmblemProp = reflectiveProps.last
    val propPathTypeKey = validateLeafEmblemProp(leafEmblemProp)

    if (! (propTypeKey <:< propPathTypeKey)) throw new PropTypeException(path, pTypeKey, propTypeKey)

    emblematicPropPath.asInstanceOf[EmblematicPropPath[P, A]]
  }

}
