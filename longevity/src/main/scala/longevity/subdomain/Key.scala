package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.subdomain.PropValTypeMismatchException
import longevity.exceptions.subdomain.KeyDoesNotContainPropException
import longevity.exceptions.subdomain.UnsetPropException

/** a natural key for this root entity type
 * @param props the set of nat key properties that make up this natural key
 */
case class Key[E <: RootEntity] private [subdomain] (
  val props: Seq[Prop[E, _]])(
  private implicit val shorthandPool: ShorthandPool) {

  private lazy val propPathToProp: Map[String, Prop[E, _]] = props.map(p => p.path -> p).toMap

  /** returns a builder for nat key vals */
  def builder = new ValBuilder

  /** returns the nat key val for the supplied root entity
   * @param e the root entity
   */
  def keyVal(e: E): Val = {
    val b = builder
    props.foreach { prop => b.setPropRaw(prop, prop.propVal(e)) }
    b.build
  }

  /** a value of this natural key */
  case class Val private[Key] (val propVals: Map[Prop[E, _], Any]) {

    /** gets the value of the nat key val for the specified prop.
     * 
     * throws java.util.NoSuchElementException if the prop is not part of the key
     * @param the prop to look up a value for
     */
    def apply(prop: Prop[E, _]): Any = propVals(prop)

    /** gets the value of the nat key val for the specified prop path.
     * 
     * throws java.util.NoSuchElementException if the prop indicated by the prop path is not part of the key
     * @param the prop to look up a value for
     */
    def apply(propPath: String): Any = propVals(propPathToProp(propPath))

    /** gets the shorthanded value of the nat key val for the specified prop. if there is a shorthand in
     * the shorthand pool that applies, it is applied to the raw value before it is returned.
     * 
     * throws java.util.NoSuchElementException if the prop is not part of the key
     * @param the prop to look up a value for
     */
    def shorthand(prop: Prop[E, _]): Any = {
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

    private var propVals = Map[Prop[E, _], Any]()

    /** sets the property to the value */
    def setProp[A : TypeKey](propPath: String, propVal: A): ValBuilder =
      setProp(propPathToProp(propPath), propVal)

    /** sets the property to the value */
    def setProp[A : TypeKey](prop: Prop[E, _], propVal: A): ValBuilder = {
      if (!props.contains(prop)) throw new KeyDoesNotContainPropException(Key.this, prop)
      if (! (typeKey[A] <:< prop.typeKey)) throw new PropValTypeMismatchException(prop, propVal)
      propVals += prop -> propVal
      this
    }

    private[Key] def setPropRaw(prop: Prop[E, _], propVal: Any): Unit = {
      propVals += prop -> propVal
    }

    /** builds the nat key value
     * @throws longevity.exceptions.UnsetPropException if any of the properties of the nat key were not set
     * in this builder
     */
    def build: Val = {
      if (propVals.size < props.size) {
        throw new UnsetPropException(Key.this, props diff propVals.keys.toSeq)
      }
      Val(propVals)
    }
  }

}
