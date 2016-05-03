package longevity.unit.subdomain.ptype

import longevity.exceptions.subdomain.ptype.NumPropValsException
import longevity.exceptions.subdomain.ptype.PropValTypeException
import longevity.subdomain.Assoc
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.KeyVal
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.RootType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

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
    object props {
      val boolean = prop[Boolean]("boolean")
      val char = prop[Char]("char")
      val double = prop[Double]("double")
    }
    object keys {
      val double = key(props.boolean, props.char)
      val triple = key(props.boolean, props.char, props.double)
    }
    object indexes {
    }
  }

}

/** unit tests for the proper construction of [[Key keys]] */
class KeySpec extends FlatSpec with GivenWhenThen with Matchers {

  import KeySpec.KeySampler
  import KeySpec.KeySampler.keys
  import KeySpec.KeySampler.props

  val subdomain = Subdomain("key sampler", PTypePool(KeySampler))

  behavior of "Key.apply"

  it should "throw exception when number of values does not match the number of properties in the key" in {
    intercept[NumPropValsException[_]] {
      keys.double(true)
    }
  }

  it should "throw exception when the propVal does not match the type of the prop" in {
    intercept[PropValTypeException[_]] {
      val keyValForP = keys.double(6.6d, 'c')
    }
  }

  it should "produce a key value when used appropriately" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d

    val keyValForP = keys.triple(booleanVal, charVal, doubleVal)

    keyValForP(props.boolean) should equal (booleanVal)
    keyValForP(props.char) should equal (charVal)
    keyValForP(props.double) should equal (doubleVal)
  }

  behavior of "Key.keyValForP"

  it should "produce a key value from a root" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d
    val sampler = KeySampler(booleanVal, charVal, doubleVal, 7.7F, 7, 77L)

    val keyValForP: KeyVal[KeySampler] = keys.triple.keyValForP(sampler)

    keyValForP(props.boolean) should equal (booleanVal)
    keyValForP(props.char) should equal (charVal)
    keyValForP(props.double) should equal (doubleVal)
  }

}
