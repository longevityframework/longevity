package longevity.subdomain.ptype

import emblem.emblematic.Emblem
import emblem.emblematic.Emblematic
import emblem.emblematic.EmblematicPropPath
import emblem.emblematic.ReflectiveProp
import emblem.TypeKey
import emblem.emblematic.basicTypes.basicTypeOrderings
import emblem.emblematic.basicTypes.isBasicType
import emblem.exceptions.EmblematicPropPathTypeMismatchException
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
import longevity.subdomain.entity.Entity
import longevity.subdomain.ShorthandPool
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
  private val shorthandPool: ShorthandPool,
  private val propLateInitializer: PropLateInitializer[P]) {

  private var emblematicPropPathOpt: Option[EmblematicPropPath[P, A]] = None

  propLateInitializer.registerProp(this)

  /** the value of this property for a persistent
   * @param p the persistent we are looking up the value of the property for
   */
  def propVal(p: P): A = emblematicPropPathOpt match {
    case Some(epp) => epp.get(p)
    case None => throw new PTypeHasNoSubdomainException(pTypeKey)(
      "you cannot call Prop.propVal without a subdomain.")
  }

  /** an ordering for property values
   * 
   * @throws longevity.exceptions.subdomain.ptype.PropNotOrderedException
   * if the ordering is accessed, and the property value type is not a type
   * for which orderings are supported. right now, we only support orderings
   * for basic types
   */
  lazy val ordering: Ordering[A] =
    if (isBasicType(propTypeKey)) {
      basicTypeOrderings(propTypeKey)
    } else if (shorthandPool.contains(propTypeKey)) {
      shorthandPool(propTypeKey).actualOrdering
    } else {
      throw new PropNotOrderedException(this)
    }

  override def toString: String = path

  private[ptype] def initializePropPath(emblematic: Emblematic): Unit = {

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
        if (! (nonLeafEmblemProp.typeKey <:< typeKey[Entity]))
          throw new UnsupportedPropTypeException(path)(pTypeKey, nonLeafEmblemProp.typeKey)
      }

    def validateLeafEmblemProp(leafEmblemProp: ReflectiveProp[_, _]): TypeKey[_] = {
      val key = leafEmblemProp.typeKey
      if (!(isBasicType(key) || key <:< typeKey[Assoc[_]] || shorthandPool.contains(key)))
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

    emblematicPropPathOpt = Some(emblematicPropPath.asInstanceOf[EmblematicPropPath[P, A]])
  }

}
