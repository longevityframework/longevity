package longevity.unit.subdomain

import emblem.imports._
import longevity.exceptions.subdomain.KeyDoesNotContainPropException
import longevity.exceptions.subdomain.KeyPropValTypeMismatchException
import longevity.exceptions.subdomain.SubdomainException
import longevity.exceptions.subdomain.UnsetKeyPropException
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
    val booleanProp = KeySampler.keyProp("boolean")
    val charProp = KeySampler.keyProp("char")
    val doubleProp = KeySampler.keyProp("double")

    val keyFromPropPaths = KeySampler.key("boolean", "char")
    val keyFromProps = KeySampler.key(booleanProp, charProp)

    val keyFromPropPathsReversed = KeySampler.key("char", "boolean")
    val keyFromPropsReversed = KeySampler.key(charProp, booleanProp)

    val tripleKey = KeySampler.key(booleanProp, charProp, doubleProp)
  }

  object context {
    val entityTypes = EntityTypePool(KeySampler)
    val subdomain = Subdomain("Nat Key Spec", entityTypes)(shorthandPool)
  }

}

/** unit tests for the proper construction of [[RootEntityType#KeyProp nat key props]] */
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
    keyFromPropPaths should equal (keyFromPropPathsReversed)
    keyFromPropPaths should equal (keyFromPropsReversed)
  }

  behavior of "RootEntityType.Key.Builder.setProp"

  it should "throw exception when the prop is not part of the nat key being built" in {
    val builder = keyFromProps.builder
    intercept[KeyDoesNotContainPropException[_]] {
      builder.setProp(doubleProp, 6.6d)
    }
  }

  it should "throw exception when the propVal does not match the type of the prop" in {
    val builder = keyFromProps.builder
    intercept[KeyPropValTypeMismatchException[_]] {
      builder.setProp(booleanProp, 6.6d)
    }
  }

  behavior of "RootEntityType.Key.Builder.build"

  it should "throw exception when not all the props in the nat key have been set" in {
    val builder = tripleKey.builder
    intercept[UnsetKeyPropException[_]] {
      builder.build
    }
    builder.setProp(booleanProp, true)
    intercept[UnsetKeyPropException[_]] {
      builder.build
    }
    builder.setProp(charProp, 'c')
    intercept[UnsetKeyPropException[_]] {
      builder.build
    }
  }

  it should "produce a valid nat key val when used appropriately" in {
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

    keyval(KeySampler.keyProp("boolean")) should equal (booleanVal)
    keyval("boolean") should equal (booleanVal)
  }

  behavior of "RootEntityType.Key.keyVal"

  it should "return nat key vals for the supplied instances" in {
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
