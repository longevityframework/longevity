package longevity.subdomain

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.NatKeyPropValTypeMismatchException
import longevity.exceptions.NatKeyDoesNotContainPropException
import longevity.exceptions.UnsetNatKeyPropException

/** a natural key for this root entity type
 * @param props the set of nat key properties that make up this natural key
 */
case class NatKey[E <: RootEntity] private [subdomain] (val props: Set[NatKeyProp[E]]) {

  private lazy val propPathToProp = props.map(p => p.path -> p).toMap

  // TODO scaladocs here

  def builder = new ValBuilder

  def natKeyVal(e: E): Val = {
    val b = builder
    props.foreach { prop => b.setPropRaw(prop, prop.natKeyPropVal(e)) }
    b.build
  }

  case class Val private[NatKey] (val propVals: Map[NatKeyProp[E], Any]) {
    def apply(prop: NatKeyProp[E]): Any = propVals(prop)
    def apply(propPath: String): Any = propVals(propPathToProp(propPath))
  }

  class ValBuilder {

    private var propVals = Map[NatKeyProp[E], Any]()

    def setProp[A : TypeKey](propPath: String, propVal: A): Unit = setProp(propPathToProp(propPath), propVal)

    def setProp[A : TypeKey](prop: NatKeyProp[E], propVal: A): Unit = {
      if (!props.contains(prop)) throw new NatKeyDoesNotContainPropException(NatKey.this, prop)
      if (! (typeKey[A] <:< prop.typeKey)) throw new NatKeyPropValTypeMismatchException(prop, propVal)
      propVals += prop -> propVal
    }

    private[NatKey] def setPropRaw(prop: NatKeyProp[E], propVal: Any): Unit = {
      propVals += prop -> propVal
    }

    def build: Val = {
      if (propVals.size < props.size) {
        throw new UnsetNatKeyPropException(NatKey.this, props -- propVals.keys)
      }
      Val(propVals)
    }
  }

}
