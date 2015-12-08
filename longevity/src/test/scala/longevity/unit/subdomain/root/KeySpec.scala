package longevity.unit.subdomain.root

import emblem.imports._
import longevity.exceptions.subdomain.root.LateKeyDefException
import longevity.exceptions.subdomain.root.NumPropValsException
import longevity.exceptions.subdomain.root.PropValTypeException
import longevity.exceptions.subdomain.SubdomainException
import longevity.subdomain._
import longevity.subdomain.root._
import org.scalatest._

object KeySpec {

  implicit val shorthandPool = ShorthandPool.empty

  case class KeySampler(
    boolean: Boolean,
    char: Char,
    double: Double,
    float: Float,
    int: Int,
    long: Long)
  extends RootEntity

  object KeySampler extends RootEntityType[KeySampler] {
    val booleanProp = KeySampler.prop[Boolean]("boolean")
    val charProp = KeySampler.prop[Char]("char")
    val doubleProp = KeySampler.prop[Double]("double")

    val keyFromPropPaths = KeySampler.key("boolean", "char")
    val keyFromProps = KeySampler.key(booleanProp, charProp)

    val tripleKey = KeySampler.key(booleanProp, charProp, doubleProp)
  }

  object context {
    val entityTypes = EntityTypePool(KeySampler)
    val subdomain = Subdomain("Key Spec", entityTypes)(shorthandPool)
  }

}

/** unit tests for the proper construction of [[Key keys]] */
class KeySpec extends FlatSpec with GivenWhenThen with Matchers {

  // TODO revisit these tests

  import KeySpec.KeySampler
  import KeySpec.KeySampler._

  behavior of "RootEntityType.key factory methods"

  they should "throw exception when called after subdomain initialization" in {

    // trigger subdomain initialization
    import longevity.context._
    val longevityContext = LongevityContext(KeySpec.context.subdomain, Mongo)

    intercept[LateKeyDefException] {
      KeySampler.key("boolean", "char")
    }
  }

  they should "produce equivalent keys for equivalent inputs" in {
    keyFromPropPaths should equal (keyFromProps)
  }

  behavior of "Key.apply"

  it should "throw exception when number of values does not match the number of properties in the key" in {
    intercept[NumPropValsException[_]] {
      keyFromProps(true)
    }
  }

  it should "throw exception when the propVal does not match the type of the prop" in {
    intercept[PropValTypeException[_]] {
      val keyVal = keyFromProps(6.6d, 'c')
    }
  }

  it should "produce a key value when used appropriately" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d

    val keyVal: KeyVal[KeySampler] = tripleKey(booleanVal, charVal, doubleVal)

    keyVal(booleanProp) should equal (booleanVal)
    keyVal(charProp) should equal (charVal)
    keyVal(doubleProp) should equal (doubleVal)
  }

}
