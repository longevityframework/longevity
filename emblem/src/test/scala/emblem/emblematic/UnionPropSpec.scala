package emblem.emblematic

import emblem.testData.exhaustive.Specialization1
import emblem.testData.exhaustive.Specialization2
import emblem.testData.exhaustive.Specialization5
import emblem.testData.exhaustive.Specialization6
import emblem.testData.exhaustive.classWithAbstractPropUnion
import emblem.testData.exhaustive.traitWithAbstractPropUnion
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** [[UnionProp union property]] specifications */
class UnionPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  private val prop1 = traitWithAbstractPropUnion.prop[String]("common")
  private val spec1 = Specialization1("common 12", "special 123")
  private val spec2 = Specialization2("common 23", "special 216")

  private val prop2 = classWithAbstractPropUnion.prop[String]("common")
  private val spec5 = Specialization5("common 52", "special 512")
  private val spec6 = Specialization6("common 63", "special 612")

  "UnionProp.name" should "return the simple name of the property" in {
    prop1.name should equal ("common")
    prop2.name should equal ("common")
  }

  "UnionProp.typeKey" should "return the property value type" in {
    prop1.typeKey should equal (typeKey[String])
    prop2.typeKey should equal (typeKey[String])
  }

  "UnionProp.toString" should "return a string containing the name and the type" in {
    prop1.toString should equal ("common: String")
    prop2.toString should equal ("common: String")
  }

  "UnionProp.get" should "return the property value for the supplied instance" in {
    prop1.get(spec1) should equal (spec1.common)
    prop1.get(spec2) should equal (spec2.common)
    prop2.get(spec5) should equal (spec5.common)
    prop2.get(spec6) should equal (spec6.common)
  }

  behavior of "UnionProp.set"

  it should "return a copy of the instance with the property set" in {
    prop1.set(spec1, "common 66") should equal (spec1.copy(common = "common 66"))
    prop1.set(spec2, "common 66") should equal (spec2.copy(common = "common 66"))
    prop2.set(spec5, "common 66") should equal (spec5.copy(common = "common 66"))
    prop2.set(spec6, "common 66") should equal (spec6.copy(common = "common 66"))
  }

}
