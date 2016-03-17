package longevity.unit.subdomain.root

import emblem.imports._
import longevity.exceptions.subdomain.ptype.NumPropValsException
import longevity.exceptions.subdomain.ptype.PropValTypeException
import longevity.subdomain._
import longevity.subdomain.ptype._
import org.scalatest._

/** sample domain for the KeySpec tests */
object KeySpec {

  case class KeySampler(
    boolean: Boolean,
    char: Char,
    double: Double,
    float: Float,
    int: Int,
    long: Long)
  extends Root

  object KeySampler extends RootType[KeySampler] {
    val booleanProp = prop[Boolean]("boolean")
    val charProp = prop[Char]("char")
    val doubleProp = prop[Double]("double")

    val doubleKey = key(booleanProp, charProp)
    val tripleKey = key(booleanProp, charProp, doubleProp)

    val keySet = kscan(this)
    val indexSet = emptyIndexSet
  }

}

/** unit tests for the proper construction of [[Key keys]] */
class KeySpec extends FlatSpec with GivenWhenThen with Matchers {

  import KeySpec._
  import KeySpec.KeySampler._

  behavior of "Key.apply"

  it should "throw exception when number of values does not match the number of properties in the key" in {
    intercept[NumPropValsException[_]] {
      doubleKey(true)
    }
  }

  it should "throw exception when the propVal does not match the type of the prop" in {
    intercept[PropValTypeException[_]] {
      val keyValForP = doubleKey(6.6d, 'c')
    }
  }

  it should "produce a key value when used appropriately" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d

    val keyValForP = tripleKey(booleanVal, charVal, doubleVal)

    keyValForP(booleanProp) should equal (booleanVal)
    keyValForP(charProp) should equal (charVal)
    keyValForP(doubleProp) should equal (doubleVal)
  }

  behavior of "Key.keyValForP"

  it should "produce a key value from a root" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d
    val sampler = KeySampler(booleanVal, charVal, doubleVal, 7.7F, 7, 77L)

    val keyValForP: KeyVal[KeySampler] = tripleKey.keyValForP(sampler)

    keyValForP(booleanProp) should equal (booleanVal)
    keyValForP(charProp) should equal (charVal)
    keyValForP(doubleProp) should equal (doubleVal)
  }

}
