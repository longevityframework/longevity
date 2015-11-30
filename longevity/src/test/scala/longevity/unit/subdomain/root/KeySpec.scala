package longevity.unit.subdomain.root

import emblem.imports._
import longevity.exceptions.subdomain.KeyHasNoSuchPropException
import longevity.exceptions.subdomain.PropValTypeException
import longevity.exceptions.subdomain.SubdomainException
import longevity.exceptions.subdomain.UnsetPropException
import org.scalatest._
import longevity.subdomain._

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

  import KeySpec.KeySampler
  import KeySpec.KeySampler._

  behavior of "RootEntityType.key factory methods"

  they should "throw exception when called after subdomain initialization" in {

    // trigger subdomain initialization
    import longevity.context._
    val longevityContext = LongevityContext(KeySpec.context.subdomain, Mongo)

    intercept[SubdomainException] {
      KeySampler.key("boolean", "char")
    }
  }

  they should "produce equivalent keys for equivalent inputs" in {
    keyFromPropPaths should equal (keyFromProps)
  }

  behavior of "RootEntityType.Key.Builder.setProp"

  it should "throw exception when the prop is not part of the key being built" in {
    val builder = keyFromProps.builder
    intercept[KeyHasNoSuchPropException[_]] {
      builder.setProp(doubleProp, 6.6d)
    }
  }

  it should "throw exception when the propVal does not match the type of the prop" in {
    val builder = keyFromProps.builder
    intercept[PropValTypeException[_]] {
      builder.setProp(booleanProp, 6.6d)
    }
  }

  behavior of "RootEntityType.Key.Builder.build"

  it should "throw exception when not all the props in the key have been set" in {
    val builder = tripleKey.builder
    intercept[UnsetPropException[_]] {
      builder.build
    }
    builder.setProp(booleanProp, true)
    intercept[UnsetPropException[_]] {
      builder.build
    }
    builder.setProp(charProp, 'c')
    intercept[UnsetPropException[_]] {
      builder.build
    }
  }

  it should "produce a valid val when used appropriately" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d

    val builder = tripleKey.builder
    builder.setProp(booleanProp, booleanVal)
    builder.setProp(charProp, charVal)
    builder.setProp(doubleProp, doubleVal)

    val keyval = builder.build

    keyval(booleanProp) should equal (booleanVal)
    keyval("boolean") should equal (booleanVal)
  }

  it should "work the same when prop vals are set by prop paths" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d

    val builder = tripleKey.builder
    builder.setProp("boolean", booleanVal)
    builder.setProp("char", charVal)
    builder.setProp("double", doubleVal)

    val keyval = builder.build

    keyval(KeySampler.prop[Boolean]("boolean")) should equal (booleanVal)
    keyval("boolean") should equal (booleanVal)
  }

  behavior of "RootEntityType.Key.keyVal"

  it should "return key vals for the supplied instances" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d
    val floatVal = 3.44F
    val intVal = 7
    val longVal = 99L
    val sampler = KeySampler(booleanVal, charVal, doubleVal, floatVal, intVal, longVal)

    val keyval = keyFromPropPaths.keyVal(sampler)
    keyval("boolean") should equal (booleanVal)
    keyval("char") should equal (charVal)
  }

}
