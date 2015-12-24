package longevity.unit.subdomain.root

import emblem.imports._
import longevity.exceptions.subdomain.root.EarlyKeyAccessException
import longevity.exceptions.subdomain.root.LateKeyDefException
import longevity.exceptions.subdomain.root.NumPropValsException
import longevity.exceptions.subdomain.root.PropValTypeException
import longevity.exceptions.subdomain.SubdomainException
import longevity.subdomain._
import longevity.subdomain.root._
import org.scalatest._

/** sample domain for the KeySpec tests */
object KeySpec {

  object earlyKeyAccess {
    case class Early() extends Root
    object Early extends RootType[Early]
    Early.keys.foreach { k => println(k) }
    val entityTypes = EntityTypePool(Early)
    val subdomain = Subdomain("early key access", entityTypes)
  }

  object shorthands {
    implicit val shorthandPool = ShorthandPool.empty
  }
  import shorthands._

  case class KeySampler(
    boolean: Boolean,
    char: Char,
    double: Double,
    float: Float,
    int: Int,
    long: Long)
  extends Root

  object KeySampler extends RootType[KeySampler] {
    val booleanProp = KeySampler.prop[Boolean]("boolean")
    val charProp = KeySampler.prop[Char]("char")
    val doubleProp = KeySampler.prop[Double]("double")

    val keyFromPropPaths = KeySampler.key("boolean", "char")
    val keyFromProps = KeySampler.key(booleanProp, charProp)

    val tripleKey = KeySampler.key(booleanProp, charProp, doubleProp)
  }

  val entityTypes = EntityTypePool(KeySampler)
  val subdomain = Subdomain("Key Spec", entityTypes)(shorthandPool)

}

/** unit tests for the proper construction of [[Key keys]] */
class KeySpec extends FlatSpec with GivenWhenThen with Matchers {

  import KeySpec._
  import KeySpec.KeySampler._

  behavior of "RootType.keys"
  it should "throw exception when called before subdomain initialization" in {
    // this is an artifact of the un-artful way i constructed the test
    val e = intercept[ExceptionInInitializerError] {
      val x = earlyKeyAccess.subdomain
    }
    e.getCause shouldBe a [EarlyKeyAccessException]
  }

  behavior of "RootType.key factory methods"

  they should "throw exception when called after subdomain initialization" in {

    // trigger subdomain initialization
    import longevity.context._
    val longevityContext = LongevityContext(KeySpec.subdomain, Mongo)

    intercept[LateKeyDefException] {
      KeySampler.key("boolean", "char")
    }

    intercept[LateKeyDefException] {
      KeySampler.key(booleanProp, charProp)
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
      val keyValForRoot = keyFromProps(6.6d, 'c')
    }
  }

  it should "produce a key value when used appropriately" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d

    val keyValForRoot = tripleKey(booleanVal, charVal, doubleVal)

    keyValForRoot(booleanProp) should equal (booleanVal)
    keyValForRoot(charProp) should equal (charVal)
    keyValForRoot(doubleProp) should equal (doubleVal)
  }

  behavior of "Key.keyValForRoot"

  it should "produce a key value from a root" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d
    val sampler = KeySampler(booleanVal, charVal, doubleVal, 7.7F, 7, 77L)

    val keyValForRoot: KeyVal[KeySampler] = tripleKey.keyValForRoot(sampler)

    keyValForRoot(booleanProp) should equal (booleanVal)
    keyValForRoot(charProp) should equal (charVal)
    keyValForRoot(doubleProp) should equal (doubleVal)
  }

}
