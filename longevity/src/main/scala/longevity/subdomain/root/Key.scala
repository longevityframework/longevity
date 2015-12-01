package longevity.subdomain.root

import emblem.basicTypes.isBasicType
import emblem.imports._
import longevity.exceptions.subdomain.NumPropValsException
import longevity.exceptions.subdomain.PropValTypeException
import longevity.subdomain._

/** a natural key for this root entity type. a set of properties for which, given specific
 * property values for each of the properties, will match no more than one root instance.
 * 
 * @tparam R the root entity type
 * @param props the set of properties that make up this key
 */
case class Key[R <: RootEntity] private [subdomain] (val props: Seq[Prop[R, _]]) {

  // TODO scaladoc

  def apply[A : TypeKey](a: A): KeyVal[R] = ValueList(Seq(Value(a, typeKey[A])))

  def withValues[A : TypeKey](a: A): ValueList = ValueList(Seq(Value(a, typeKey[A])))

  def ~[A : TypeKey](a: A): ValueList = withValues(a)

  case class Value[A](value: A, typeKey: TypeKey[A])

  case class ValueList(values: Seq[Value[_]]) {
    def and[A : TypeKey](a: A) = ValueList(values :+ Value(a, emblem.typeKey[A]))
    def ~[A : TypeKey](a: A) = and(a)
  }

  object ValueList {
    implicit def toKeyVal(valueList: ValueList): KeyVal[R] = {
      val values = valueList.values
      if (props.size != values.size) throw new NumPropValsException(Key.this, props.size, values.size)
      val propVals = props.zip(values).map {
        case (prop, value) =>
          if (! (value.typeKey <:< prop.typeKey)) throw new PropValTypeException(prop, value.value)
          prop -> value.value
      }
      KeyVal(Key.this, propVals.toMap)
    }
  }

  /** returns the key val for the supplied root entity
   * @param e the root entity
   */
  def keyVal(root: R): KeyVal[R] = {
    val propVals = props.map { prop => prop -> prop.propVal(root) }
    KeyVal(this, propVals.toMap)
  }

}
