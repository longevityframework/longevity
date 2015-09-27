package longevity.subdomain

import emblem.imports._
import longevity.exceptions.NatKeyDoesNotContainPropException
import longevity.exceptions.NatKeyPropValTypeMismatchException
import longevity.exceptions.SubdomainException
import longevity.exceptions.UnsetNatKeyPropException
import org.scalatest._
import NatKeySpec._
import NatKeySpec.NatKeySampler._

object NatKeySpec {

  implicit val shorthandPool = ShorthandPool.empty

  case class NatKeySampler(
    boolean: Boolean,
    char: Char,
    double: Double,
    float: Float,
    int: Int,
    long: Long)
  extends RootEntity

  object NatKeySampler extends RootEntityType[NatKeySampler] {
    val booleanProp = NatKeySampler.natKeyProp("boolean")
    val charProp = NatKeySampler.natKeyProp("char")
    val doubleProp = NatKeySampler.natKeyProp("double")

    val keyFromPropPaths = NatKeySampler.natKey("boolean", "char")
    val keyFromProps = NatKeySampler.natKey(booleanProp, charProp)

    val keyFromPropPathsReversed = NatKeySampler.natKey("char", "boolean")
    val keyFromPropsReversed = NatKeySampler.natKey(charProp, booleanProp)

    val tripleKey = NatKeySampler.natKey(booleanProp, charProp, doubleProp)
  }

  object context {
    val entityTypes = EntityTypePool(NatKeySampler)
    val subdomain = Subdomain("Nat Key Spec", entityTypes, shorthandPool)
  }

}

/** unit tests for the proper construction of [[RootEntityType#NatKeyProp nat key props]] */
@longevity.UnitTest
class NatKeySpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "RootEntityType.natKey factory methods"

  they should "throw exception when called after subdomain initialization" in {

    // trigger subdomain initialization
    import longevity.context._
    val longevityContext = LongevityContext(context.subdomain, Mongo)

    intercept[SubdomainException] {
      NatKeySampler.natKey("boolean", "char")
    }
  }

  they should "produce equivalent keys for equivalent inputs" in {
    keyFromPropPaths should equal (keyFromProps)
    keyFromPropPaths should equal (keyFromPropPathsReversed)
    keyFromPropPaths should equal (keyFromPropsReversed)
  }

  behavior of "RootEntityType.NatKey.Builder.setProp"

  it should "throw exception when the prop is not part of the nat key being built" in {
    val builder = keyFromProps.builder
    intercept[NatKeyDoesNotContainPropException[_]] {
      builder.setProp(doubleProp, 6.6d)
    }
  }

  it should "throw exception when the propVal does not match the type of the prop" in {
    val builder = keyFromProps.builder
    intercept[NatKeyPropValTypeMismatchException[_]] {
      builder.setProp(booleanProp, 6.6d)
    }
  }

  behavior of "RootEntityType.NatKey.Builder.build"

  it should "throw exception when not all the props in the nat key have been set" in {
    val builder = tripleKey.builder
    intercept[UnsetNatKeyPropException[_]] {
      builder.build
    }
    builder.setProp(booleanProp, true)
    intercept[UnsetNatKeyPropException[_]] {
      builder.build
    }
    builder.setProp(charProp, 'c')
    intercept[UnsetNatKeyPropException[_]] {
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

    keyval(NatKeySampler.natKeyProp("boolean")) should equal (booleanVal)
    keyval("boolean") should equal (booleanVal)
  }

  behavior of "RootEntityType.NatKey.natKeyVal"

  it should "return nat key vals for the supplied instances" in {
    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d
    val floatVal = 3.44F
    val intVal = 7
    val longVal = 99L
    val sampler = NatKeySampler(booleanVal, charVal, doubleVal, floatVal, intVal, longVal)

    val keyval = keyFromPropPaths.natKeyVal(sampler)
    keyval("boolean") should equal (booleanVal)
    keyval("char") should equal (charVal)
  }

}
