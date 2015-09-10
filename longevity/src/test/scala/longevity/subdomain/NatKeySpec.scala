package longevity.subdomain

import emblem.imports._
import longevity.exceptions.NatKeyDoesNotContainPropException
import longevity.exceptions.NatKeyPropValTypeMismatchException
import longevity.exceptions.UnsetNatKeyPropException
import org.scalatest._
import longevity.integration.master._

/** unit tests for the proper construction of [[RootEntityType#NatKeyProp nat key props]] */
@longevity.UnitTest
class NatKeySpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "RootEntityType.NatKey factory methods"

  they should "produce equivalent keys for equivalent inputs" in {

    // this factory method take property paths as input
    val key1 = AllAttributes.natKey("boolean", "char")

    // this factory method take properties as input
    val booleanProp = AllAttributes.natKeyProp("boolean")
    val charProp = AllAttributes.natKeyProp("char")
    val key2 = AllAttributes.natKey(booleanProp, charProp)

    key1 should equal (key2)

    // order of arguments should not matter
    key1 should equal (AllAttributes.natKey(charProp, booleanProp))
    key1 should equal (AllAttributes.natKey("char", "boolean"))

  }

  behavior of "RootEntityType.NatKey.Builder.setProp"

  it should "throw exception when the prop is not part of the nat key being built" in {
    val booleanProp = AllAttributes.natKeyProp("boolean")
    val charProp = AllAttributes.natKeyProp("char")
    val doubleProp = AllAttributes.natKeyProp("double")
    val key = AllAttributes.natKey(booleanProp, charProp)
    val builder = key.builder
    intercept[NatKeyDoesNotContainPropException[_]] {
      builder.setProp(doubleProp, 6.6d)
    }
  }

  it should "throw exception when the propVal does not match the type of the prop" in {
    val booleanProp = AllAttributes.natKeyProp("boolean")
    val charProp = AllAttributes.natKeyProp("char")
    val key = AllAttributes.natKey(booleanProp, charProp)
    val builder = key.builder
    intercept[NatKeyPropValTypeMismatchException[_]] {
      builder.setProp(booleanProp, 6.6d)
    }
  }


  behavior of "RootEntityType.NatKey.Builder.build"

  it should "throw exception when not all the props in the nat key have been set" in {
    val booleanProp = AllAttributes.natKeyProp("boolean")
    val charProp = AllAttributes.natKeyProp("char")
    val doubleProp = AllAttributes.natKeyProp("double")
    val key = AllAttributes.natKey(booleanProp, charProp, doubleProp)
    val builder = key.builder
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
    val booleanProp = AllAttributes.natKeyProp("boolean")
    val charProp = AllAttributes.natKeyProp("char")
    val doubleProp = AllAttributes.natKeyProp("double")

    val booleanVal = true
    val charVal = 'c'
    val doubleVal = 6.667d

    val key = AllAttributes.natKey(booleanProp, charProp, doubleProp)

    val builder = key.builder
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

    val key = AllAttributes.natKey("boolean", "char", "double")

    val builder = key.builder
    builder.setProp("boolean", booleanVal)
    builder.setProp("char", charVal)
    builder.setProp("double", doubleVal)

    val keyval = builder.build

    keyval(AllAttributes.natKeyProp("boolean")) should equal (booleanVal)
    keyval("boolean") should equal (booleanVal)
  }

}
