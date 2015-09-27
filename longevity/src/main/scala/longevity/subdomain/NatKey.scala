package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.NatKeyPropValTypeMismatchException
import longevity.exceptions.NatKeyDoesNotContainPropException
import longevity.exceptions.UnsetNatKeyPropException

/** a natural key for this root entity type
 * @param props the set of nat key properties that make up this natural key
 */
case class NatKey[E <: RootEntity] private [subdomain] (
  val props: Set[NatKeyProp[E]])(
  private implicit val shorthandPool: ShorthandPool) {

  private lazy val propPathToProp = props.map(p => p.path -> p).toMap

  /** returns a builder for nat key vals */
  def builder = new ValBuilder

  /** returns the nat key val for the supplied root entity
   * @param e the root entity
   */
  def natKeyVal(e: E): Val = {
    val b = builder
    props.foreach { prop => b.setPropRaw(prop, prop.natKeyPropVal(e)) }
    b.build
  }

  /** a value of this natural key */
  case class Val private[NatKey] (val propVals: Map[NatKeyProp[E], Any]) {

    /** gets the value of the nat key val for the specified prop
     * @param the prop to look up a value for
     * @throws NoSuchElementException if the prop is not part of the key
     */
    def apply(prop: NatKeyProp[E]): Any = propVals(prop)

    /** gets the value of the nat key val for the specified prop path
     * @param the prop patg to look up a value for
     * @throws NoSuchElementException if the prop indicated by the prop path is not part of the key
     */
    def apply(propPath: String): Any = propVals(propPathToProp(propPath))

    /** gets the shorthanded value of the nat key val for the specified prop. if there is a shorthand in
     * the shorthand pool that applies, it is applied to the raw value before it is returned.
     * @param the prop to look up a value for
     * @throws NoSuchElementException if the prop is not part of the key
     */
    def shorthand(prop: NatKeyProp[E]): Any = {
      val raw = propVals(prop)
      if (shorthandPool.contains(prop.typeKey)) {
        def abbreviate[PV : TypeKey] = shorthandPool[PV].abbreviate(raw.asInstanceOf[PV])
        abbreviate(prop.typeKey)
      } else {
        raw
      }
    }

  }

  /** a builder of values for this natural key */
  class ValBuilder {

    private var propVals = Map[NatKeyProp[E], Any]()

    /** sets the property to the value */
    def setProp[A : TypeKey](propPath: String, propVal: A): Unit = setProp(propPathToProp(propPath), propVal)

    /** sets the property to the value */
    def setProp[A : TypeKey](prop: NatKeyProp[E], propVal: A): Unit = {
      if (!props.contains(prop)) throw new NatKeyDoesNotContainPropException(NatKey.this, prop)
      if (! (typeKey[A] <:< prop.typeKey)) throw new NatKeyPropValTypeMismatchException(prop, propVal)
      propVals += prop -> propVal
    }

    private[NatKey] def setPropRaw(prop: NatKeyProp[E], propVal: Any): Unit = {
      propVals += prop -> propVal
    }

    /** builds the nat key value
     * @throws UnsetNatKeyPropException if any of the properties of the nat key were not set in this builder
     */
    def build: Val = {
      if (propVals.size < props.size) {
        throw new UnsetNatKeyPropException(NatKey.this, props -- propVals.keys)
      }
      Val(propVals)
    }
  }

}
